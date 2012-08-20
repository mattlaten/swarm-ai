package backend.environment;

import math.Vec;

public class Prey extends Entity {

	public Prey()	{
		size = 5;
	}
	
	public RenderObject getROb() {
		return null;
	}

	public Vec getVelocity() {
		return new Vec();
	}
}
