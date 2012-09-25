package backend.environment;

import java.io.Serializable;
import java.util.List;

import math.Vec;
import backend.HeightMap;

/*
 * Abstract class that models a single element in our environment
 */
public abstract class Element implements Serializable, Cloneable	{
	protected Waypoint target = null;
	protected Vec position;
	protected volatile boolean alive = true;
	
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
	public void setSize(double s)	{}
	public void setRadius(double r)	{}
	public synchronized void setAlive(boolean a)	{
		alive = a;
	}
	public boolean isAlive()	{
		return alive;
	}
	
	abstract public double getSize();
	abstract public double getMaxSpeed();
	abstract public double getRadius();
	abstract public Vec getVelocity();
	abstract public void setVelocity(Vec v);
	abstract public Object clone();		//force all Elements to have a clone method
}
