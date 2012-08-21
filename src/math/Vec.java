package math;

import java.awt.Point;
import java.io.Serializable;

public class Vec implements Serializable	{
	public double x,y;
	
	public Vec()	{
		this(0,0);
	}
	
	public Vec(Point p)	{
		this(p.x, p.y);
	}
	
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
	
	public Point getPoint()	{
		return new Point((int)x, (int)y);
	}
	
	public String toString()	{
		return "<" + x + ", " + y + ">";
	}
}
