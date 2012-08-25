package backend.environment;

import java.io.Serializable;
import java.util.ArrayList;

import math.Vec;

/*
 * Abstract class that models a single element in our environment
 */
public abstract class Element implements Serializable, Cloneable	{
	abstract public double getSize();
	abstract public double getMaxSpeed();
	abstract public double getRadius();
	abstract public Vec getPosition();
	abstract public Vec getVelocity();
	abstract public void calculateUpdate(ArrayList<Element> influences);
	abstract public void update();
	abstract public RenderObject getROb();
	abstract public Object clone();		//force all Elements to have a clone method
}
