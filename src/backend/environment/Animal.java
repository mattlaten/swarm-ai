package backend.environment;


import java.util.List;

import math.Vec;

public abstract class Animal extends Element implements Cloneable {
	public @Property Vec velocity;	//this stores a vector that is at most a unit vector which is multiplied by the maxSpeed 
	private Vec oldVelocity = null;
	
	public double collisionAvoidanceWeight = 0.15,
			   velocityMatchingWeight = 0.1,
			   flockCenteringWeight = 0.15,
			   otherAnimalWeight = 0.4,			//this is predatorAvoidance (for Prey) or preyAttacking (for Predators)
			   waypointAttractionWeight = 0.3,
			   terrainAvoidanceWeight = 0.2;
	
	public Animal(Vec position, Vec velocity)	{
		this();
		this.position = new Vec(position);
		this.velocity = new Vec(velocity);
	}
	
	public Animal(Animal other)	{
		this();
		this.position = new Vec(other.position);
		this.velocity = new Vec(other.velocity);
		this.size = other.size;
		this.maxSpeed = other.maxSpeed;
		this.radius = other.radius;
	}
	
	public Animal()	{
		position = new Vec(0,0);
		velocity = new Vec(0,0);
		size = 5;
		maxSpeed = 0.2;
		radius = 100;
		initWeights();
	}
	
	public Animal(double x, double y, double xvel, double yvel, double size)	{
		this();
		position = new Vec(x,y);
		setVelocity(new Vec(xvel,yvel));
		this.size = size;
		initWeights();
	}
	
	public Vec getVelocity() 	{	return (oldVelocity == null ? velocity : oldVelocity).mult(getMaxSpeed());	}
	public void setVelocity(Vec v)	{
		velocity = new Vec(v).mult(1.0/getMaxSpeed()).truncate(1);
		oldVelocity = null;
	}
	
	//public Object clone()		{	return new Animal(this);	}
	
	protected abstract void initWeights();
	
	public void update()	{
		position = position.plus(getVelocity());
		oldVelocity = new Vec(velocity);
	}
}