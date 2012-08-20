package environment;

import java.io.Serializable;

import math.Vec;

/*
 * Abstract class that models a single element in our environment
 */
public abstract class Element implements Serializable	{
	
	abstract float getSize();
	abstract Vec getPosition();
	abstract RenderObject getROb();
	abstract Vec getVelocity();
}
