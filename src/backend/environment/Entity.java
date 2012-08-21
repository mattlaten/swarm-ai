package backend.environment;

import java.io.Serializable;
import java.util.List;

import math.Vec;

/**
 * Abstract class that models a single Entity in our environment
 */

public abstract class Entity extends Element implements Serializable	{
	@Property
	public Vec position;
	@Property
	public float size;
	
	public Entity()	{
		position = new Vec();
	}
	
	public Vec getPosition()	{
		return position;
	}
	
	public float getSize()	{
		return size;
	}
	
	public Vec getVelocity()	{
		return new Vec();
	}
	
	//add list of vectors together, defintely needs to be changed	
	public void update(List<Vec> vectors)
	{
		Vec temp = new Vec();
		for (Vec v : vectors)
			temp.plus(v);
		position = temp;
	}
}
