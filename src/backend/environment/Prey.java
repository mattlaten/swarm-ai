package backend.environment;

import java.util.List;

import math.Vec;

public class Prey extends Animal {
	public Prey(Prey other)					{	super(other);	}
	public Prey(Vec position, Vec velocity)	{	super(position, velocity);	}
	public Prey()							{	super();	}
	public Prey(double x, double y, double xvel, double yvel, double size)	{	super(x,y,xvel,yvel,size);	}
	
	public double weighted(double x)	{
		return Math.pow(x, 3)*4+0.5;
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
	 * We used to use an accumulator, now we take an "informed weighted sum" of the weighted averages. To understand what I mean by "informed"
	 * consider the case where we weight collisionAvoidance 0.2, flockCentering 0.2, velocityMatching 0.1 and predatorAvoidance 0.5.
	 * Now if there were no predator's to avoid, then the other factors should make a total weighting of 1 and not 0.5. This "accounting for
	 * absent impulses" is what we understand as "informed".
	 * 
	 * Note: Collision avoidance and flock centering aren't linearly dependent on the distance of the other prey.
	 */
	public void calculateUpdate(List<Element> influences) {
		//calculate the sums
		Vec collisionAvoidance = new Vec(),
			velocityMatching = new Vec(),
			flockCentering = new Vec(),
			predatorAvoidance = new Vec(),
			waypointAttraction = new Vec();
		double collisionAvoidanceWeight = 0.15,
			   velocityMatchingWeight = 0.1,
			   flockCenteringWeight = 0.15,
			   predatorAvoidanceWeight = 0.4,
			   waypointAttractionWeight = 0.2;
		int neighbourhoodCount = 0, predatorCount = 0, waypointCount = 0;
		for(Element e : influences)	{
			Vec dir = e.getPosition().minus(getPosition());
			if(dir.size() > 0 && dir.size() <= getRadius())	{
				if(e instanceof Prey)	{
					neighbourhoodCount ++;
					collisionAvoidance = collisionAvoidance.plus(dir.unit().mult(Math.pow((getRadius()-dir.size())/getRadius(),1.0/3)).neg());
					//this needs to be fixed, it's very haxxy that I must divide by e.getMaxSpeed() to get the truncated-to-unit vector, e.velocity
					velocityMatching = velocityMatching.plus(e.getVelocity().mult(1.0/e.getMaxSpeed()));
					flockCentering = flockCentering.plus(dir.unit().mult(Math.pow(dir.size()/getRadius(),1)));
				}
				else if(e instanceof Predator)	{
					predatorCount ++;
					predatorAvoidance = predatorAvoidance.plus(dir.unit().mult(Math.pow((getRadius()-dir.size())/getRadius(), 1.0/3)).neg());
				}
				//ignore waypoints
				/*else if(e instanceof Waypoint)	{
					waypointCount ++;
					waypointAttraction = waypointAttraction.plus(dir.unit().mult(Math.pow(dir.size()/getRadius(),1)));
				}*/
			}
		}
		
		if(getTarget() != null)	{
			waypointAttraction = target.getPosition().minus(getPosition()).truncate(1);
			System.out.println("here: " + waypointAttraction + " " + waypointAttraction.size());
			//waypointAttraction = waypointAttraction.plus(dir.unit().mult(Math.pow(dir.size()/getRadius(),1)));
			if(waypointAttraction.size() < 0.6)
				setTarget(getTarget().getTarget());
		}
		
		//take the average weighting
		if(predatorCount > 0)
			predatorAvoidance = predatorAvoidance.mult(1.0/predatorCount);
		if(neighbourhoodCount > 0)	{
			collisionAvoidance = collisionAvoidance.mult(1.0/neighbourhoodCount);
			velocityMatching = velocityMatching.mult(1.0/neighbourhoodCount);
			flockCentering = flockCentering.mult(1.0/neighbourhoodCount);
		}
		//if(waypointCount > 0)	waypointAttraction = waypointAttraction.mult(1.0/waypointCount);
		
		//now perform accumulation\
		/*Vec ret = new Vec(predatorAvoidance);
		if(ret.size() < 1)
			ret = ret.plus(collisionAvoidance).plus(flockCentering).mult(0.5);
		if(ret.size() < 1)
			ret = ret.plus(velocityMatching);*/
		Vec ret = predatorAvoidance.mult(predatorAvoidanceWeight)
						.plus(collisionAvoidance.mult(collisionAvoidanceWeight)
						.plus(flockCentering.mult(flockCenteringWeight)
						.plus(velocityMatching.mult(velocityMatchingWeight)
						.plus(waypointAttraction.mult(waypointAttractionWeight)))))
						.mult(1.0/(predatorAvoidanceWeight+collisionAvoidanceWeight+flockCenteringWeight+velocityMatchingWeight+waypointAttractionWeight));
		velocity = velocity.plus(ret.truncate(1)).truncate(1);
	}
	
	public Object clone()	{
		return new Prey(this);
	}
}
