package math;

import java.awt.Point;
import java.io.Serializable;

public class Vec implements Serializable	{
	public static final Vec ZERO = new Vec(0,0);
	public double x,y;
	
	public Vec()		{	this(0,0);		}
	public Vec(Point p)	{	this(p.x, p.y);	}
	public Vec(Vec v)	{	this(v.x, v.y);	}
	
	public Vec(double x, double y)	{
		this.x = x;
		this.y = y;
	}
	
	public double dot(Vec other)	{	return x * other.x + y * other.y;			}
	public Vec plus(Vec other)		{	return new Vec(x + other.x, y + other.y);	}
	public Vec minus(Vec other)		{	return plus(other.neg());					}
	public Vec mult(double scalar)	{	return new Vec(x * scalar, y * scalar);		}
	public Vec neg()				{	return mult(-1);							}
	public double size()			{	return Math.sqrt(dot(this));				}
	public Vec unit()				{	return mult(1.0/size());					}
	
	public Vec invertY()			{	return new Vec(x, -y);						}
	
	public Vec truncate(double mag)	{
		if(size() == 0)
			return new Vec(this);
		return unit().mult(mag);
	}
	
	public Point getPoint()	{
		return new Point((int)x, (int)y);
	}
	
	public boolean equals(Vec other)	{
		return x == other.x && y == other.y;
	}
	
	public boolean withinRadius(Vec other, double r)
	{
		return Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) <= Math.pow(r, 2);
	}
	
	public String toString()	{
		return "<" + x + ", " + y + ">";
	}
}
