package environment;

import math.Vec;

public abstract class Entity implements Serializable	{
	public Vec position;
	
	public Entity()	{
		position = new Vec();
	}
}
