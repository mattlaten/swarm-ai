package backend.environment;


import java.util.List;

import math.Vec;
/**
 * Animal is a subclass of Element and superclass to Prey and Predator. It enables us to
 * group Prey and Predators together when we don't need to distinguish between the two.
 * For all Animals, the radius is understood as the sight radius (ie. how far the animal
 * can see).
 * 
 * The calculateUpdate(...) method makes use of a number of behaviour weights that determine
 * how the animal behaves at any given frame:
 * 
 * <ol>
 * <li> collision avoidance - the tendency to avoid fellow flock-mates </li>
 * <li> velocity matching - the tendency to move in the same direction as fellow flock-mates </li>
 * <li> flock centering - the tendency to keep near other flock-mates </li>
 * <li> waypoint attraction - the tendency to move towards target waypoints </li>
 * <li> terrain avoidance - the tendency to move along smoother paths </li>
 * </ol>
 */
public abstract class Animal extends Element implements Cloneable {
	public @Property Vec velocity;	//this stores a vector that is at most a unit vector which is multiplied by the maxSpeed 
	private Vec oldVelocity = null;
	Waypoint previousTarget = null;
	
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
		maxSpeed = 1;
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
	
	public void setTarget(Waypoint t)	{
		previousTarget = getTarget();
		super.setTarget(t);
	}
	
	//public Object clone()		{	return new Animal(this);	}
	
	/**
	 * Initialises the different behaviours to their default values
	 */
	protected abstract void initWeights();
	
	public void update()	{
		position = position.plus(velocity.mult(getMaxSpeed()));
		oldVelocity = new Vec(velocity);
	}
}