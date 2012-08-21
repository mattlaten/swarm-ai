package backend.environment;

import java.io.Serializable;
import java.util.ArrayList;

import math.Vec;

/*
 * Abstract class that models a single element in our environment
 */
public abstract class Element implements Serializable	{
	abstract public float getSize();
	abstract public float getSightRadius();
	abstract public Vec getPosition();
	abstract public Vec getVelocity();
	abstract public void update(ArrayList<Element> influences);
	abstract public RenderObject getROb();
}
