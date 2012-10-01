package backend.environment;

import java.util.List;

import math.Vec;

/**
 * Waypoints are entities that can be targeted by Prey, Predators and other Waypoints
 *
 */
public class Waypoint extends Element {
	public Waypoint()	{
		position = new Vec(0,0);
		size = 20;
		maxSpeed = 0;
		radius = 50;
	}
	
	public Waypoint(Vec position)	{
		this();
		this.position = new Vec(position);
	}
	
	public Waypoint(double x, double y)	{
		this();
		position = new Vec(x,y);
	}
	
	public Waypoint(Waypoint other)	{
		this();
		this.position = new Vec(other.position);
	}
	
	public Vec getVelocity() 	{	return new Vec();	}
	public void setVelocity(Vec v)	{}
	
	public Object clone()		{	return new Waypoint(this);	}
}
