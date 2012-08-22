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
	
	public void update(ArrayList<Element> influentials, ArrayList<Vec> influences)	{
		Vec vel = getVelocity();
		for(Element e : influentials)	{
			Vec dir = e.getPosition().minus(getPosition());
			if(dir.size() <= getSightRadius())	{
				Vec unit = dir.unit();
				if(e instanceof Predator)
					unit = unit.neg();
				if(e instanceof Prey)	{
					unit = unit.neg();
					unit.mult(0.5);
				}
				vel = vel.plus(unit);
			}
		}
		if(influences != null)
			for(Vec v : influences)	{
				Vec dir = v.minus(getPosition());
				if(dir.size() <= getSightRadius())
					vel = vel.plus(dir.unit());
			}
		velocity = vel;
	}
}
