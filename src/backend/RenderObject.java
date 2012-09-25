package backend;

import math.Vec;
import backend.environment.Element;
import backend.environment.Waypoint;

public class RenderObject {
	public Element element;
	public Vec velocity, position;
	public Waypoint target;
	public double size, radius;
	
	public RenderObject(Element e, Vec v, Vec p, Waypoint t, double s, double r)	{
		element = e;
		velocity = v;
		position = p;
		target = t;
		size = s;
		radius = r;
	}
}
