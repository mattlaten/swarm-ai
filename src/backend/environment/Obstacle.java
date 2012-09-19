package backend.environment;

import java.awt.Polygon;
import java.util.List;

import math.Vec;

/* An obstacle is a polygon defined by a list of waypoints
 */
public class Obstacle extends Element {
	public Waypoint start;
	
	public Obstacle(Waypoint... ws)	{
		if(ws.length == 0)
			throw new InvalidWaypointListException("List must be at least size 1");
		start = ws[0];
		for(int i = 1; i < ws.length; i++)
			ws[i-1].setTarget(ws[i]);
		ws[ws.length-1].setTarget(start);
	}
	
	public Obstacle(List<Waypoint> ws)	{
		if(ws.size() == 0)
			throw new InvalidWaypointListException("List must be at least size 1");
		start = ws.get(0);
		for(int i = 1; i < ws.size(); i++)
			ws.get(i-1).setTarget(ws.get(i));
		ws.get(ws.size()-1).setTarget(start);
	}
	
	public Obstacle(Obstacle other)	{
	}
	
	public double getSize() 	{	return 3;		}
	public double getMaxSpeed()	{	return 0;		}
	public double getRadius()	{	return 50;		}
	public Vec getVelocity() 	{	return new Vec();	}
	public void setVelocity(Vec v)	{}
	
	public Object clone()		{	return new Obstacle(this);	}

}

class InvalidWaypointListException extends RuntimeException	{
	public InvalidWaypointListException()	{
		super();
	}
	
	public InvalidWaypointListException(String message)	{
		super(message);
	}
}