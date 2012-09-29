package backend;

import java.util.HashMap;

import math.Vec;
import backend.environment.Animal;
import backend.environment.Element;
import backend.environment.Waypoint;

public class RenderObject {
	public Element element;
	public Vec velocity, position;
	public Waypoint target;
	public double size, radius, maxSpeed;
	
	public RenderObject(Element e, Vec v, Vec p, Waypoint t, double s, double r, double ms)	{
		element = e;
		velocity = v;
		position = p;
		target = t;
		size = s;
		radius = r;
		maxSpeed = ms;
	}
	
	public RenderObject(Element e)	{
		element = e;
		velocity = new Vec(e.getVelocity());
		position = new Vec(e.getPosition());
		target = e.getTarget();
		size = e.getSize();
		radius = e.getRadius();
		maxSpeed = e.getMaxSpeed();
	}
	
	public void apply()	{
		element.setVelocity(velocity);
		element.setPosition(position);
		element.setTarget(target);
		element.setSize(size);
		element.setRadius(radius);
		element.setMaxSpeed(maxSpeed);
	}
	
	public boolean equals(Object o)	{
		try	{
			return element == ((RenderObject)o).element;
		}
		catch(ClassCastException cce)	{}
		return false;
	}
	
	public String toString(HashMap<Waypoint, Integer> waypointIndices){
		int targetInd = -1;
		if(target != null && waypointIndices.containsKey(target))
			targetInd = waypointIndices.get(target);
		String [] elementClass = element.getClass().getName().split("\\.");
		String str = elementClass[elementClass.length-1]
				+ " " + targetInd
				+ " " + position.x
				+ " " + position.y
				+ " " + velocity.x
				+ " " + velocity.y
				+ " " + radius
				+ " " + size
				+ " " + maxSpeed;
		if(element instanceof Animal)	{
			Animal a = (Animal)element;
			str += " " + a.collisionAvoidanceWeight
				+ " " + a.flockCenteringWeight
				+ " " + a.velocityMatchingWeight
				+ " " + a.otherAnimalWeight
				+ " " + a.terrainAvoidanceWeight;
		}
		return str;
	}
	
	/*public int fromString(String s)	{
	}*/
}
