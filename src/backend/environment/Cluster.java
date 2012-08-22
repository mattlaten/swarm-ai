package backend.environment;

import java.awt.geom.Rectangle2D;
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
	
	public void addElement(Element e)	{
		elements.add(e);
	}
	
	public RenderObject getRenderObject()	{
		return null;
	}

	public float getSize() {
		return 0;
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
	
	private Rectangle2D.Double getBounds()	{
		Vec topLeft = new Vec(Double.MAX_VALUE, Double.MIN_VALUE),
			botRight = new Vec(Double.MIN_VALUE, Double.MAX_VALUE);
		for(Element e: elements)	{
			Vec pos = e.getPosition();
			float size = e.getSize();
			topLeft.x = Math.min(topLeft.x, pos.x-size/2);
			topLeft.y = Math.max(topLeft.y, pos.y+size/2);
			botRight.x = Math.max(botRight.x, pos.x+size/2);
			botRight.y = Math.min(botRight.y, pos.y-size/2);
		}
		return new Rectangle2D.Double(topLeft.x, topLeft.y, botRight.x-topLeft.x, topLeft.y-botRight.y);
	}
	
	public void update(ArrayList<Element> influences)	{
		
	}
}
