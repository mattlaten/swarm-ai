package backend.environment;

import java.util.HashMap;
import java.util.List;

import math.Vec;
import backend.HeightMap;

/**
 * The Prey class is one of the two concrete Animals that can exist in the world.
 * It seeks, as it's primary goal, to move towards it's target waypoint. The factors
 * that affect it's behaviour, by default are:
 * <ol>
 * <li> desire not to be eaten by predators </li>
 * <li> desire to get to target waypoint </li>
 * <li> desire to stick with the flock </li>
 * </ol>
 */
public class Prey extends Animal {
	double maxSpeedVar = 0.05,
			maxTurningAngle = Math.PI/8;
	
	public Prey(Prey other)					{	super(other);	}
	public Prey(Vec position, Vec velocity)	{	super(position, velocity);	}
	public Prey()							{	super();	}
	public Prey(double x, double y, double xvel, double yvel, double size)	{	super(x,y,xvel,yvel,size);	}
	
	public double weighted(double x)	{
		return Math.pow(x, 3)*4+0.5;
	}
	
	private int sign(double d)	{
		if(d > 0)
			return 1;
		else if(d < 0)
			return -1;
		return 0;
	}
	
	public void initWeights()	{
		collisionAvoidanceWeight = 0.15;
		velocityMatchingWeight = 0.1;
		flockCenteringWeight = 0.15;
		otherAnimalWeight = 0.4;			//this is predatorAvoidance (for Prey) or preyAttacking (for Predators)
		waypointAttractionWeight = 0.3;
		terrainAvoidanceWeight = 0.2;
	}
	
	/**
	 * This is where the central logic for the prey's behaviour takes place.
	 * For each of the six factors mentioned (in the Animal class) the prey constructs
	 * a vector of influence. These vectors are as follows:
	 * <ol>
	 * <li> collision avoidance pushes the prey away from fellow prey </li>
	 * <li> velocity matching makes the prey try and match it's fellow prey member's velocities (to go in the same direction) </li>
	 * <li> flock centering pulls the prey towards all the other prey members </li>
	 * <li> predator avoidance pushes the prey away from nearby predators </li>
	 * <li> terrain avoidance pushes the prey towards smoother terrain </li>
	 * </ol>
	 * 
	 * To deal with multiple competing vectors (pushing the prey in a number of different direction)
	 * we take the "smart" weighted average of the different vectors. By "smart" we mean
	 * that if there aren't any predators nearby then the predator avoidance weight doesn't
	 * still bring the total resultant vector size down as it would in a simple weighted average.
	 */
	public void calculateUpdate(List<Element> influences, HeightMap hm) {
		//calculate the sums
		Vec collisionAvoidance = new Vec(),
			velocityMatching = new Vec(),
			flockCentering = new Vec(),
			predatorAvoidance = new Vec(),
			waypointAttraction = new Vec(),
			obstacleAvoidance = new Vec(),
			terrainAvoidance = new Vec();
		
		int neighbourhoodCount = 0, predatorCount = 0, obstacleCount = 0;
		HashMap<Waypoint, Integer> flockTargets = new HashMap<Waypoint, Integer>();
		for(Element e : influences)	{
			Vec dir = e.getPosition().minus(getPosition());
			if(dir.size() > 0 && dir.size() <= getRadius())	{
				if(e instanceof Prey)	{
					neighbourhoodCount ++;
					collisionAvoidance = collisionAvoidance.plus(dir.unit().mult(Math.pow((getRadius()-dir.size())/getRadius(),1)).neg());
					//this needs to be fixed, it's very haxxy that I must divide by e.getMaxSpeed() to get the truncated-to-unit vector, e.velocity
					velocityMatching = velocityMatching.plus(e.getVelocity().mult(1.0/e.getMaxSpeed()));
					flockCentering = flockCentering.plus(dir.unit().mult(Math.pow(dir.size()/getRadius(),1)));
					
					//take target suggestions from flock members who are in front
					if(e.getPosition().minus(getPosition()).dot(velocity) > 0)	{
						Integer count = flockTargets.get(e.getTarget());
						count = (count == null ? 0 : count) + 1;
						flockTargets.put(e.getTarget(), count);
					}
				}
				else if(e instanceof Predator)	{
					predatorCount ++;
					predatorAvoidance = predatorAvoidance.plus(dir.unit().mult(Math.pow((getRadius()-dir.size())/getRadius(), 1.0/3)).neg());
				}
			}
		}
		
		if(getTarget() != null)	{
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
		if(predatorCount > 0)
			predatorAvoidance = predatorAvoidance.mult(1.0/predatorCount);
		if(neighbourhoodCount > 0)	{
			collisionAvoidance = collisionAvoidance.mult(1.0/neighbourhoodCount);
			velocityMatching = velocityMatching.mult(1.0/neighbourhoodCount);
			flockCentering = flockCentering.mult(1.0/neighbourhoodCount);
		}
		if(obstacleCount > 0)
			obstacleAvoidance = obstacleAvoidance.mult(1.0/obstacleCount);
		//if(waypointCount > 0)	waypointAttraction = waypointAttraction.mult(1.0/waypointCount);
		
		//now perform accumulation\
		/*Vec ret = new Vec(predatorAvoidance);
		if(ret.size() < 1)
			ret = ret.plus(collisionAvoidance).plus(flockCentering).mult(0.5);
		if(ret.size() < 1)
			ret = ret.plus(velocityMatching);*/
		double nTerrainAvoidanceWeight = terrainAvoidanceWeight * velocity.size();
		Vec ret = predatorAvoidance.mult(otherAnimalWeight)
						.plus(collisionAvoidance.mult(collisionAvoidanceWeight)
						.plus(flockCentering.mult(flockCenteringWeight)
						.plus(velocityMatching.mult(velocityMatchingWeight)
						.plus(waypointAttraction.mult(waypointAttractionWeight)
						.plus(terrainAvoidance.mult(nTerrainAvoidanceWeight))))))
						.mult(1.0/(otherAnimalWeight
								+collisionAvoidanceWeight
								+flockCenteringWeight
								+velocityMatchingWeight
								+waypointAttractionWeight
								+nTerrainAvoidanceWeight));
		/*System.out.println("predator avoidance: " + predatorAvoidance
				+ "\ncollision avoidance: " + collisionAvoidance
				+ "\nflock centering: " + flockCentering
				+ "\nvelocity matching: " + velocityMatching
				+ "\nwaypoint attraction: " + waypointAttraction
				+ "\nterrain avoidance: " + terrainAvoidance
				+ "\n");*/
		/* ret is the vector we wish to be facing. we want to affect the
		 * current velocity so that it's pointing in the direction of ret.
		 * however, the prey has a maximum turning angle and a maximum speed
		 * variation (how much it can speed up or slow down in a given period
		 * of time). so we try to adjust the velocity as much as possible.
		 */
		ret = velocity.plus(ret.truncate(1)).truncate(1);
		/*double retSize = ret.size(), velSize = velocity.size();
		if(retSize > 0)	{
			//first ensure that speed variation is maintained
			if(velSize + maxSpeedVar < retSize)
				//ret = ret.mult((retSize - maxSpeedVar)/retSize);
				retSize = velSize + maxSpeedVar;
			else if(velSize - maxSpeedVar > retSize)
				//ret = ret.mult((retSize + maxSpeedVar)/retSize);
				retSize = velSize - maxSpeedVar;
		
			/* then ensure turning angle
			 * 1. convert to polar co-ordinates
			 * 2. compare angle like we just compared size
			 * 3. convert back to cartesian co-ordinates
			 * /
			double retAngle = Math.atan2(ret.y, ret.x),
					velAngle = Math.atan2(velocity.y, velocity.x);
			
			//fix the case where we're in 1st and 4th quadrants
			if(retAngle < Math.PI/2 && velAngle > 3*Math.PI/2)
				velAngle -= 2*Math.PI;
			else if(retAngle > 3*Math.PI/2 && velAngle < Math.PI/2)
				retAngle -= 2*Math.PI;
			
			if(velAngle - maxTurningAngle > retAngle)
				retAngle = velAngle - maxTurningAngle;
			else if(velAngle + maxTurningAngle < retAngle)
				retAngle = velAngle + maxTurningAngle;
			
			ret = new Vec(Math.cos(retAngle), Math.sin(retAngle)).mult(retSize);
		}*/
		velocity = ret.truncate(1);
	}
	
	public Object clone()	{
		return new Prey(this);
	}
}