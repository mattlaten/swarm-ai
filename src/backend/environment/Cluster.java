package backend.environment;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import math.Rect;
import math.Vec;

/*
 * Class that models a logical group of elements
 */
public class Cluster extends Element implements Serializable	{
	public ArrayList<Element> elements;
	
	public Cluster()	{
		elements = new ArrayList<Element>();
	}
	
	public void addElement(Element e)	{
		elements.add(e);
	}
	
	public RenderObject getRenderObject()	{
		return null;
	}

	public double getSize() {
		Rect r = getBounds();
		return Math.max(r.getHeight(), r.getWidth());
	}
	
	public float getSightRadius()	{
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
	
	private Rect getBounds()	{
		Vec topLeft = new Vec(Double.MAX_VALUE, Double.MIN_VALUE),
			botRight = new Vec(Double.MIN_VALUE, Double.MAX_VALUE);
		for(Element e: elements)	{
			Vec pos = e.getPosition();
			double size = e.getSize();
			topLeft.x = Math.min(topLeft.x, pos.x-size/2);
			topLeft.y = Math.max(topLeft.y, pos.y+size/2);
			botRight.x = Math.max(botRight.x, pos.x+size/2);
			botRight.y = Math.min(botRight.y, pos.y-size/2);
		}
		return new Rect(topLeft, botRight);
	}
	
	public void update(ArrayList<Element> influentials, ArrayList<Vec> influences)	{
		
	}
}
