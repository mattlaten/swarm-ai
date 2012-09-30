package backend;

import java.util.ArrayList;
import java.util.HashMap;

import backend.environment.Element;
import backend.environment.Waypoint;

/**
 * Snapshot is a class used to store the state of 
 * a simulation at a distinct point in time 
 */
public class Snapshot extends ArrayList<RenderObject> implements Comparable<Snapshot>	{
	int timeTaken;
	
	/**
	 * Public constructor for Snapshot
	 * @param size The size of the list of RenderObjects
	 * @param timeTaken 
	 */
	public Snapshot(int size, int timeTaken)	{
		super(size);
		this.timeTaken = timeTaken;
	}
	
	public int compareTo(Snapshot other)	{
		if(other.timeTaken > timeTaken)
			return -1;
		else if(other.timeTaken < timeTaken)
			return 1;
		return 0;
	}
	
	public String toString(){
		String str = "{\n" + timeTaken + "\n";
		synchronized(this)	{
			//first go through all the render objects and find the waypoint indices
			HashMap<Waypoint, Integer> waypointIndices = new HashMap<Waypoint, Integer>();
			for(int i = 0; i < size(); i++)	{
				RenderObject e = get(i);
				if(e.element instanceof Waypoint)
					waypointIndices.put((Waypoint)(e.element), i);
			}
			
			for (int i = 0; i < size(); i++){
				RenderObject rob = get(i);
				str += rob.toString(waypointIndices) + '\n';
			}
		}
		return str + "}";
	}
}