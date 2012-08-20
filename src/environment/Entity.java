package environment;

import java.io.Serializable;

import math.Vec;

/*
 * Abstract class that models a single Entity in our environment
 */
public abstract class Entity extends Element implements Serializable	{
	public Vec position;
	
	public Entity()	{
		position = new Vec();
	}
}
