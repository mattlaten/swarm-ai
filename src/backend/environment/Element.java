package backend.environment;

import java.io.Serializable;
import java.util.List;

import math.Vec;
import backend.HeightMap;

/**
 * Abstract class that models a single element in our environment. Elements are the things that
 * exist in the environment. Each Element has the following attributes:
 * <ol>
 * 	<li> position - the position in space </li>
 * 	<li> velocity - the velocity vector </li>
 * 	<li> target - the target waypoint </li>
 * 	<li> max speed - the maximum speed that the Element can move at </li>
 * 	<li> radius - depending on what the Element is this changes </li>
 * </ol>
 */
public abstract class Element implements Serializable, Cloneable	{
	protected Waypoint target = null;
	protected Vec position;
	double size;
	double maxSpeed;
	double radius = 1;
	
	/**
	 * Returns the target waypoint of the Element
	 * @return The target waypoint
	 */
	public Waypoint getTarget()	{
		return target;
	}
	
	/**
	 * Sets the target waypoint of the Element
	 * @param target The new target of the Element
	 */
	public void setTarget(Waypoint target)	{
		this.target = target;
	}
	
	/**
	 * Returns the position of the Element in space
	 * @return The position vector of the Element
	 */
	public Vec getPosition()	{
		return new Vec(position);
	}
	
	/**
	 * Sets the position of the Element
	 * @param v The new position of the Element
	 */
	public void setPosition(Vec v)	{
		position = new Vec(v.x, v.y);
	}
	
	/**
	 * Every Element needs to be updated every simulation loop cycle. In order for this to
	 * happen two things need to happen:
	 * <ol>
	 *  <li> The update for the next cycle needs to be calculated </li>
	 *  <li> The update needs to be applied </li>
	 * </ol>
	 * This function does the first step
	 * @param influences All Elements in the world
	 * @param hm The heightMap of the Simulation
	 */
	public void calculateUpdate(List<Element> influences, HeightMap hm)	{}
	
	/**
	 * Every Element needs to be updated every simulation loop cycle. In order for this to
	 * happen two things need to happen:
	 * <ol>
	 *  <li> The update for the next cycle needs to be calculated </li>
	 *  <li> The update needs to be applied </li>
	 * </ol>
	 * This function does the second step
	 */
	public void update()									{}
	
	/**
	 * Sets the size of the Element
	 * @param s The new size of the Element
	 */
	public void setSize(double s)	{
		size = s;
	}
	
	/**
	 * Sets the Maximum speed of the Element
	 * @param s The new maximum speed of the Element
	 */
	public void setMaxSpeed(double s) {
		maxSpeed = s;
	}
	
	/**
	 * Sets the radius of the Element
	 * @param r The new radius of the Element
	 */
	public void setRadius(double r)	{
		radius = r;
	}
	
	/**
	 * Returns the radius of the Element
	 * @return The current radius of the Element as a double
	 */
	public double getRadius()	{
		return radius;
	}
	
	/**
	 * Returns the size of the Element
	 * @return The current size of the Element
	 */
	public double getSize()	{
		return size;
	}
	
	/**
	 * Returns the maximum speed of the Element
	 * @return The current maximum speed of the Element
	 */
	public double getMaxSpeed()	{
		return maxSpeed;
	}
	
	/**
	 * Returns the velocity vector of the Element
	 * The velocity vector of an Element is a vector with size < 1. To get the
	 * final velocity (which is returned), this vector is scaled by the maximum speed
	 * @return The final velocity vector of the Element
	 */
	abstract public Vec getVelocity();
	
	/**
	 * Sets the velocity vector of the Element. If the vector is too big
	 * then it is truncated. The vector given is scaled down by the maximum
	 * speed of the Element.
	 * @param v The to-scale velocity vector of the Element
	 */
	abstract public void setVelocity(Vec v);
	
	/**
	 * Every Element must be forced to be able to clone itself
	 */
	abstract public Object clone();		//force all Elements to have a clone method
}
