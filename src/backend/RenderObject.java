package backend;

import math.Vec;
import backend.environment.Element;
import backend.environment.Waypoint;

public class RenderObject {
	public Element element;
	public Vec velocity, position;
	public Waypoint target;
	public double size, radius;
	public boolean alive;
	
	public RenderObject(Element e, Vec v, Vec p, Waypoint t, double s, double r, boolean a)	{
		element = e;
		velocity = v;
		position = p;
		target = t;
		size = s;
		radius = r;
		alive = a;
	}
	
	public RenderObject(Element e)	{
		element = e;
		velocity = new Vec(e.getVelocity());
		position = new Vec(e.getPosition());
		target = e.getTarget();
		size = e.getSize();
		radius = e.getRadius();
		alive = e.isAlive();
	}
	
	public void apply()	{
		element.setVelocity(velocity);
		element.setPosition(position);
		element.setTarget(target);
		element.setSize(size);
		element.setRadius(radius);
		element.setAlive(alive);
	}
	
	public boolean equals(Object o)	{
		try	{
			return element == ((RenderObject)o).element;
		}
		catch(ClassCastException cce)	{}
		return false;
	}
	
	public String toString(){
		String str = String.format("");
		return str;
	}
}
