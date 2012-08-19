package environment;

import java.io.Serializable;

import math.Vec;

public abstract class Entity extends Element implements Serializable	{
	public Vec position;
	
	public Entity()	{
		position = new Vec();
	}
}
