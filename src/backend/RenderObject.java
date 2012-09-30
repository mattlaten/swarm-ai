package backend;

import java.util.HashMap;

import math.Vec;
import backend.environment.Animal;
import backend.environment.Element;
import backend.environment.Predator;
import backend.environment.Prey;
import backend.environment.Waypoint;

public class RenderObject {
	public Element element;
	public Vec velocity, position;
	public Waypoint target;
	public double size, radius, maxSpeed,
		collisionAvoidanceWeight,
		flockCenteringWeight,
		velocityMatchingWeight,
		otherAnimalWeight,
		terrainAvoidanceWeight;
	
	/*public RenderObject(Element e, Vec v, Vec p, Waypoint t, double s, double r, double ms)	{
		element = e;
		velocity = v;
		position = p;
		target = t;
		size = s;
		radius = r;
		maxSpeed = ms;
	}*/
	
	public RenderObject(Element e)	{
		element = e;
		velocity = new Vec(e.getVelocity());
		position = new Vec(e.getPosition());
		target = e.getTarget();
		size = e.getSize();
		radius = e.getRadius();
		maxSpeed = e.getMaxSpeed();
		if(e instanceof Animal)	{
			Animal a = (Animal)e;
			collisionAvoidanceWeight = a.collisionAvoidanceWeight;
			flockCenteringWeight = a.flockCenteringWeight;
			velocityMatchingWeight = a.velocityMatchingWeight;
			otherAnimalWeight = a.otherAnimalWeight;
			terrainAvoidanceWeight = a.terrainAvoidanceWeight;
		}
	}
	
	public void apply()	{
		element.setVelocity(velocity);
		element.setPosition(position);
		element.setTarget(target);
		element.setSize(size);
		element.setRadius(radius);
		element.setMaxSpeed(maxSpeed);
		if(element instanceof Animal)	{
			Animal a = (Animal)element;
			a.collisionAvoidanceWeight = collisionAvoidanceWeight;
			a.flockCenteringWeight = flockCenteringWeight;
			a.velocityMatchingWeight = velocityMatchingWeight;
			a.otherAnimalWeight = otherAnimalWeight;
			a.terrainAvoidanceWeight = terrainAvoidanceWeight;
		}
	}
	
	public boolean equals(Object o)	{
		try	{
			return element == ((RenderObject)o).element;
		}
		catch(ClassCastException cce)	{}
		return false;
	}
	
	public String toString(HashMap<Element, String> names){
		String targetName = "e";
		if(target != null && names.containsKey(target))
			targetName = names.get(target);
		String [] elementClass = element.getClass().getName().split("\\.");
		String str = names.get(element)
				+ " " + elementClass[elementClass.length-1]
				+ " " + targetName
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
	
	public static RenderObject fromString(String s, HashMap<String, Element> elements)	{
		Element e = null;
		String [] ss = s.split("\\s+");
		if(elements.containsKey(ss[0]))	{
			e = elements.get(ss[0]);
		}
		else {
			if(ss[1].equals("Waypoint"))
				e = new Waypoint();
			else if(ss[1].equals("Prey"))
				e = new Prey();
			else if(ss[1].equals("Predator"))
				e = new Predator();
			elements.put(ss[0], e);
		}
		if(e == null)
			return null;
		RenderObject r = new RenderObject(e);
		//now deal with target
		if(!ss[2].equals("e") && !elements.containsKey(ss[2]))
			elements.put(ss[2], new Waypoint());
		r.target = (Waypoint)elements.get(ss[2]);
		r.position = new Vec(Double.parseDouble(ss[3]), Double.parseDouble(ss[4]));
		r.velocity = new Vec(Double.parseDouble(ss[5]), Double.parseDouble(ss[6]));
		r.radius = Double.parseDouble(ss[7]);
		r.size = Double.parseDouble(ss[8]);
		r.maxSpeed = Double.parseDouble(ss[9]);
		if(e instanceof Animal)	{
			r.collisionAvoidanceWeight = Double.parseDouble(ss[10]);
			r.flockCenteringWeight = Double.parseDouble(ss[11]);
			r.velocityMatchingWeight = Double.parseDouble(ss[12]);
			r.otherAnimalWeight = Double.parseDouble(ss[13]);
			r.terrainAvoidanceWeight = Double.parseDouble(ss[14]);
		}
		return r;
	}
}
