package environment;

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

	@Override
	float getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	Vec getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	RenderObject getROb() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Vec getVelocity() {
		// TODO Auto-generated method stub
		return null;
	}
}
