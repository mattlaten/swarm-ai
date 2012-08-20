package backend.environment;

import java.io.Serializable;

import math.Vec;

/*
 * Abstract class that models a single element in our environment
 */
public abstract class Element implements Serializable	{
	
	abstract public float getSize();
	abstract public Vec getPosition();
	abstract public Vec getVelocity();
	abstract public RenderObject getROb();
}
