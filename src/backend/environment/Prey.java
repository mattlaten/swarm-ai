package backend.environment;

import java.util.ArrayList;

import math.Vec;

public class Prey extends Element implements Cloneable {
	public @Property Vec position;
	public @Property Vec velocity;
	private Vec oldVelocity = null;
	public @Property double size;
	public @Property double maxSpeed;
	public @Property double sightRadius;
	
	public Prey(Vec position, Vec velocity)	{
		this.position = new Vec(position);
		this.velocity = new Vec(velocity);
	}
	
	public Prey(Prey other)	{
		this.position = new Vec(other.position);
		this.velocity = new Vec(other.velocity);
		this.size = other.size;
		this.maxSpeed = other.maxSpeed;
		this.sightRadius = other.sightRadius;
	}
	
	public Prey()	{
		position = new Vec(0,0);
		velocity = new Vec(0,0);
		size = 5;
		maxSpeed = 0.2;
		sightRadius = 20;
	}
	
	public Prey(double x, double y, double xvel, double yvel, double size)	{
		position = new Vec(x,y);
		velocity = new Vec(0,0);
		this.size = size;
	}
	
	public double getSize() 	{	return size;		}
	public double getMaxSpeed()	{	return maxSpeed;	}
	public double getRadius()	{	return sightRadius;	}
	public Vec getPosition() 	{	return position;	}
	public Vec getVelocity() 	{	return (oldVelocity == null ? velocity : oldVelocity);	}
	
	public Object clone()		{	return new Prey(this);	}

	public void calculateUpdate(ArrayList<Element> influences) {
		//calculate the sums
		Vec collisionAvoidance = new Vec(),
			velocityMatching = new Vec(),
			flockCentering = new Vec();
		int neighbourhoodCount = 0;
		for(Element e : influences)	{
			Vec dir = e.getPosition().minus(getPosition());
			if(dir.size() > 0 && dir.size() <= getRadius())	{
				neighbourhoodCount ++;	
				collisionAvoidance = collisionAvoidance.plus(dir.unit().mult((getRadius()-dir.size())/getRadius()).neg());
				velocityMatching.plus(e.getVelocity());
				flockCentering.plus(dir.unit().mult(dir.size()/getRadius()));
			}
		}
		
		//take the average weighting
		if(neighbourhoodCount > 0)	{
			collisionAvoidance = collisionAvoidance.mult(1.0/neighbourhoodCount);
			velocityMatching = velocityMatching.mult(1.0/neighbourhoodCount);
			flockCentering = flockCentering.mult(1.0/neighbourhoodCount);
		}
		
		//now perform accumulation
		Vec ret = new Vec(collisionAvoidance);
		if(ret.size() < 1)
			ret = ret.plus(velocityMatching);
		if(ret.size() < 1)
			ret = ret.plus(flockCentering);
		velocity = velocity.plus(ret.truncate(1).mult(getMaxSpeed())).truncate(1);
	}
	
	public void update()	{
		position = position.plus(velocity);
		oldVelocity = new Vec(velocity);
	}

	public RenderObject getROb() {
		return null;
	}
	
}
