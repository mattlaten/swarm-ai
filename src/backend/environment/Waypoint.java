package backend.environment;

import java.util.List;

import math.Vec;

public class Waypoint extends Element {
	public Waypoint()	{
		position = new Vec(0,0);
	}
	
	public Waypoint(Vec position)	{
		this.position = new Vec(position);
	}
	
	public Waypoint(double x, double y)	{
		this();
		position = new Vec(x,y);
	}
	
	public Waypoint(Waypoint other)	{
		this.position = new Vec(other.position);
	}
	
	public double getSize() 	{	return 3;		}
	public double getMaxSpeed()	{	return 0;		}
	public double getRadius()	{	return 0;		}
	public Vec getVelocity() 	{	return new Vec();	}
	public void setVelocity(Vec v)	{}
	
	public Object clone()		{	return new Waypoint(this);	}
}
