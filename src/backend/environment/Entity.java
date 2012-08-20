package backend.environment;

import java.io.Serializable;

import math.Vec;

/*
 * Abstract class that models a single Entity in our environment
 */
public abstract class Entity extends Element implements Serializable	{
	@Property
	public Vec position;
	@Property
	public float size;
	
	public Entity()	{
		position = new Vec();
	}
	
	public Vec getPosition()	{
		return position;
	}
	
	public float getSize()	{
		return size;
	}
	
	public Vec getVelocity()	{
		return new Vec();
	}
}
