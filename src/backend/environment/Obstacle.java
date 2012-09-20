package backend.environment;

import java.util.Iterator;
import java.util.List;

import math.Vec;

/* An obstacle is a polygon defined by a list of waypoints
 */
public class Obstacle extends Element implements Iterable<Waypoint> {
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
	
	public Vec getPosition()	{
		int count = 0;
		Vec pos = start.getPosition();
		Waypoint cur = start.getTarget();
		while(cur != start)	{
			pos = pos.plus(cur.getPosition());
			cur = cur.getTarget();
		}
		return pos.mult(1.0/count);
	}
	
	public Object clone()		{	return new Obstacle(this);	}
	
	public Iterator<Waypoint> iterator()	{
		return new ObstacleWaypointIterator(start);
	}
	
	class ObstacleWaypointIterator implements Iterator<Waypoint>	{
		private Waypoint cur, start;
		public ObstacleWaypointIterator(Waypoint cur)	{
			this.cur = cur;
			this.start = cur;
		}
		
		public boolean hasNext()	{
			return cur.getTarget() != start;
		}
		
		public Waypoint next()	{
			Waypoint r = cur;
			cur = cur.getTarget();
			return r;
		}
		
		public void remove()	{
			throw new UnsupportedOperationException("Why are you removing waypoints son?");
		}
	}

}

class InvalidWaypointListException extends RuntimeException	{
	public InvalidWaypointListException()	{
		super();
	}
	
	public InvalidWaypointListException(String message)	{
		super(message);
	}
}