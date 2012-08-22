package math;

import java.io.Serializable;

public class Rect implements Serializable {
	public Vec topLeft, botRight;
	
	public Rect(Vec topLeft, Vec botRight)	{
		this.topLeft = new Vec(topLeft);
		this.botRight = new Vec(botRight);
	}
	
	public Rect(Rect other)	{
		this(other.topLeft, other.botRight);
	}
	
	public double getWidth()	{
		return botRight.x - topLeft.x;
	}
	
	public double getHeight()	{
		return topLeft.y - botRight.y;
	}
	
	public boolean equals(Rect other)	{
		return topLeft.equals(other.topLeft) && botRight.equals(other.botRight);
	}
}
