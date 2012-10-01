package backend.environment;


import java.util.HashMap;
import java.util.List;

import math.Vec;
import backend.HeightMap;

/**
 * The Predator class is one of the two concrete Animals that can exist in the world.
 *
 */
public class Predator extends Animal implements Cloneable {
	public Predator(Predator other)					{	super(other);	}
	public Predator(Vec position, Vec velocity)		{	super(position, velocity);	}
	public Predator()								{	super();	}
	public Predator(double x, double y, double xvel, double yvel, double size)	{	super(x,y,xvel,yvel,size);	}
	
	public void initWeights()	{
		collisionAvoidanceWeight = 0.3;
		velocityMatchingWeight = 0.1;
		flockCenteringWeight = 0.3;
		otherAnimalWeight = 0.3;
	}
	
	/* Here we have two general approaches when dealing with multiple vectors:
	 * 1. take a weighted average of the vectors
	 * 2. use an accumulator: order the vectors by priority, start adding them up and when the 
	 * 			total length exceeds some length limit, stop adding and truncate
	 * 
	 * here we have a number of sources of vectors:
	 * 		1. collision avoidance pushes the prey away from fellow prey
	 * 		2. velocity matching makes the prey try and match it's fellow prey member's velocities (to go in the same direction)
	 * 		3. flock centering pulls the prey towards all the other prey members
	 * 
	 * //A total source vector for each of these sources is calculated using weighted averaging. Then the three vectors are placed in
	 * //an accumulator to find the final velocity.
	 * 
	 * See comments for the Prey.calculateUpdate(...) function for notes on informed weighted sums, which have replaced accumulators
	 * in the code.
	 * 
	 * Note: Collision avoidance and flock centering aren't linearly dependent on the distance of the other predators.
	 */
	public void calculateUpdate(List<Element> influences, HeightMap hm) {
		//calculate the sums
		Vec collisionAvoidance = new Vec(),
			velocityMatching = new Vec(),
			flockCentering = new Vec(),
			preyAttacking = new Vec(),
			waypointAttraction = new Vec(),
			terrainAvoidance = new Vec();
		/*double collisionAvoidanceWeight = 0.3,
				   velocityMatchingWeight = 0.1,
				   flockCenteringWeight = 0.3,
				   preyAttackingWeight = 0.3;*/
		int neighbourhoodCount = 0, preyCount = 0;
		HashMap<Waypoint, Integer> flockTargets = new HashMap<Waypoint, Integer>();
		for(Element e : influences)	{
			Vec dir = e.getPosition().minus(getPosition());
			if(dir.size() > 0 && dir.size() <= getRadius())	{
				if(e instanceof Predator)	{
					neighbourhoodCount ++;
					collisionAvoidance = collisionAvoidance.plus(dir.unit().mult(Math.pow((getRadius()-dir.size()+e.getSize())/getRadius(),1.0/3)).neg());
					//this needs to be fixed, it's very haxxy that I must divide by e.getMaxSpeed() to get the truncated-to-unit vector, e.velocity
					velocityMatching = velocityMatching.plus(e.getVelocity().mult(1.0/getMaxSpeed()).truncate(1));
					flockCentering = flockCentering.plus(dir.unit().mult(Math.pow((dir.size()-e.getSize())/getRadius(),1)));
					
					//take target suggestions from flock members who are in front
					if(e.getPosition().minus(getPosition()).dot(velocity) > 0)	{
						Integer count = flockTargets.get(e.getTarget());
						count = (count == null ? 0 : count) + 1;
						flockTargets.put(e.getTarget(), count);
					}
				}
				else if(e instanceof Prey)	{
					preyCount ++;
					preyAttacking = preyAttacking.plus(dir.unit().mult(Math.pow((getRadius()-dir.size())/getRadius(), 1.0/4)));
				}
			}
		}
		
		//predators only go to their targets when they're not chasing prey
		if(getTarget() != null && preyCount == 0)	{
			waypointAttraction = target.getPosition().minus(getPosition()).truncate(1);
			//if we reach the waypoint we start moving to the next one
			if(getPosition().minus(getTarget().getPosition()).size() < getSize()+getTarget().getSize())
				setTarget(getTarget().getTarget());
			//otherwise we see if the majority of the flock has updated their target
			else	{
				Waypoint mVote = null;
				double flockCount = 0, totalFlock = 0;
				for(Waypoint w : flockTargets.keySet())			{
					if(w != previousTarget && flockTargets.get(w) > flockCount)	{
						flockCount = flockTargets.get(w);
						mVote = w;
					}
					totalFlock += flockTargets.get(w);
				}
				if(mVote != null)	{
					/* if we are sufficiently close to the waypoint and sufficiently many flock
					 * members have started towards the next, then we can move to the next one
					 */
					if(flockCount/totalFlock > 0.5 && getPosition().minus(getTarget().getPosition()).size() < getTarget().getRadius()*0.5)
						setTarget(mVote);
				}
			}
		}
		
		/* Terrain avoidance is acheived by sending out 24 feelers in different directions
		 * and then checking which one have the greatest change in height. Those are then
		 * inverted proportionate to their changes
		 */
		if(hm != null)	{
			double height = hm.getInterpolatedHeightAt(getPosition());
			double dir = Math.atan2(velocity.y, velocity.x);
			double maxSlope = 0;
			for(double rad = 0; rad < 2*Math.PI; rad += Math.PI/6)	{
				//get the option-vector (as a unit vector)
				Vec v = new Vec(Math.cos(rad+dir), Math.sin(rad+dir));
				
				/* move out in increments of the size of the prey until we go beyond
				 * the sight-radius of this prey.
				 */
				int scale = 1;
				double totalHeightDiff = 0;
				while(scale*getSize() <= getRadius())	{
					Vec modOption = v.mult(scale*getSize());
					totalHeightDiff += Math.abs(height
							- hm.getInterpolatedHeightAt(getPosition().plus(modOption)))/(scale++);
				}
				totalHeightDiff /= scale - 1;
				totalHeightDiff *= Math.abs(velocity.x*v.x + velocity.y*v.y);
				totalHeightDiff = Math.pow(totalHeightDiff, 3);
				maxSlope = Math.max(maxSlope, totalHeightDiff);
				terrainAvoidance = terrainAvoidance.plus(v.neg().mult(totalHeightDiff));
			}
			if(maxSlope > 0)
				terrainAvoidance = terrainAvoidance.mult(1.0/maxSlope);
			else
				terrainAvoidance = new Vec();
		}
		
		//take the average weighting
		if(preyCount > 0)
			preyAttacking = preyAttacking.mult(1.0/preyCount);
		if(neighbourhoodCount > 0)	{
			collisionAvoidance = collisionAvoidance.mult(1.0/neighbourhoodCount);
			velocityMatching = velocityMatching.mult(1.0/neighbourhoodCount);
			flockCentering = flockCentering.mult(1.0/neighbourhoodCount);
		}
		
		double nTerrainAvoidanceWeight = terrainAvoidanceWeight * velocity.size();
		double denom = otherAnimalWeight
				+collisionAvoidanceWeight
				+flockCenteringWeight
				+velocityMatchingWeight
				+waypointAttractionWeight
				+nTerrainAvoidanceWeight;
		Vec ret = new Vec();
		if(denom != 0)
			ret = preyAttacking.mult(otherAnimalWeight)
						.plus(collisionAvoidance.mult(collisionAvoidanceWeight)
						.plus(flockCentering.mult(flockCenteringWeight)
						.plus(velocityMatching.mult(velocityMatchingWeight)
						.plus(waypointAttraction.mult(waypointAttractionWeight)
						.plus(terrainAvoidance.mult(nTerrainAvoidanceWeight))))))
						.mult(1.0/(denom));
		velocity = velocity.plus(ret.truncate(1)).truncate(1);
	}
	
	public Object clone()	{
		return new Predator(this);
	}
}
