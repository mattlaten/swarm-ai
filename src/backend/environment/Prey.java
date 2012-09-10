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
	 * A total source vector for each of these sources is calculated using weighted averaging. Then the three vectors are placed in
	 * an accumulator to find the final velocity.
	 * 
	 * Note: Collision avoidance and flock centering aren't linearly dependent on the distance of the other prey.
	 */
	public void calculateUpdate(List<Element> influences) {
		//calculate the sums
		Vec collisionAvoidance = new Vec(),
			velocityMatching = new Vec(),
			flockCentering = new Vec(),
			predatorAvoidance = new Vec();
		int neighbourhoodCount = 0, predatorCount = 0;
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
					predatorAvoidance = predatorAvoidance.plus(dir.unit().mult(Math.pow((getRadius()-dir.size())/getRadius(), 1.0/4)).neg());
				}
			}
		}
		
		//take the average weighting
		if(predatorCount > 0)
			predatorAvoidance = predatorAvoidance.mult(1.0/predatorCount);
		if(neighbourhoodCount > 0)	{
			collisionAvoidance = collisionAvoidance.mult(1.0/neighbourhoodCount);
			velocityMatching = velocityMatching.mult(1.0/neighbourhoodCount);
			flockCentering = flockCentering.mult(1.0/neighbourhoodCount);
		}
		
		//now perform accumulation\
		Vec ret = new Vec(predatorAvoidance);
		if(ret.size() < 1)
			ret = ret.plus(collisionAvoidance).plus(flockCentering).mult(0.5);
		/*if(ret.size() < 1)
			ret = ret.plus(flockCentering);*/
		if(ret.size() < 1)
			ret = ret.plus(velocityMatching);
		velocity = velocity.plus(ret.truncate(1)).truncate(1);
	}
	
	public Object clone()	{
		return new Prey(this);
	}
}
