package backend.environment;

import java.util.ArrayList;

import math.Vec;

public class Prey extends Entity {
	Vec velocity;
	
	public Prey()	{
		size = 5;
		sightRadius = 50;
		velocity = new Vec(0,0);
	}
	
	public RenderObject getROb() {
		return null;
	}

	public Vec getVelocity() {
		return new Vec(velocity);
	}
	
	public void update(ArrayList<Element> influences)	{
		Vec vel = getVelocity();
		for(Element e : influences)	{
			Vec dir = e.getPosition().minus(getPosition());
			if(dir.size() <= getSightRadius())	{
				Vec unit = dir.unit();
				if(e instanceof Predator)
					unit = unit.neg();
				vel = vel.plus(unit);
			}
		}
		velocity = vel;
	}
}
