package backend.environment;

import java.io.Serializable;
import java.util.ArrayList;

import math.Vec;

/*
 * Class that models a logical group of elements
 */
public class Cluster extends Element implements Serializable	{
	public ArrayList<Element> elements;
	
	public Cluster()	{
		elements = new ArrayList<Element>();
	}
	
	public RenderObject getRenderObject()	{
		return null;
	}

	public float getSize() {
		return 0;
	}

	public Vec getPosition() {
		return null;
	}

	public RenderObject getROb() {
		return null;
	}

	public Vec getVelocity() {
		return null;
	}
}
