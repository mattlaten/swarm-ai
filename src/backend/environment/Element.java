package backend.environment;

import java.io.Serializable;
import java.util.List;

import math.Vec;
import backend.HeightMap;

/**
 * Abstract class that models a single element in our environment
 */
public abstract class Element implements Serializable, Cloneable	{
	protected Waypoint target = null;
	protected Vec position;
	double size;
	double maxSpeed;
	double radius = 1;
	
	public Waypoint getTarget()	{
		return target;
	}
	public void setTarget(Waypoint target)	{
		this.target = target;
	}
	
	public Vec getPosition()	{
		return new Vec(position);
	}
	
	public void setPosition(Vec v)	{
		position = new Vec(v.x, v.y);
	}
	
	public void calculateUpdate(List<Element> influences, HeightMap hm)	{}
	public void update()									{}
	public void setSize(double s)	{
		size = s;
	}
	public void setMaxSpeed(double s) {
		maxSpeed = s;
	}
	public void setRadius(double r)	{
		radius = r;
	}
	
	public double getRadius()	{
		return radius;
	}
	
	public double getSize()	{
		return size;
	}
	public double getMaxSpeed()	{
		return maxSpeed;
	}
	abstract public Vec getVelocity();
	abstract public void setVelocity(Vec v);
	abstract public Object clone();		//force all Elements to have a clone method
}
