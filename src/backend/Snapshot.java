package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import math.Vec;
import backend.environment.Animal;
import backend.environment.Element;

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
	
	/**
	 * Compare two snapshots on their timestamps
	 */
	public int compareTo(Snapshot other)	{
		if(other.timeTaken > timeTaken)
			return -1;
		else if(other.timeTaken < timeTaken)
			return 1;
		return 0;
	}
	
	/**
	 * Return a string representation of this snapshot
	 * @param elementNames A map of elements to names
	 * @return A string representation of this snapshot
	 */
	public String toString(HashMap<Element, String> elementNames){
		String str = "{\n" + timeTaken + "\n";
		synchronized(this)	{
			for (int i = 0; i < size(); i++){
				RenderObject rob = get(i);
				str += rob.toString(elementNames) + '\n';
			}
		}
		return str + "}";
	}
	
	/**
	 * Return a string representation of this snapshot, usable for XSI exports
	 * @param elementNames A map from elements to names
	 * @param frame The current frame number
	 * @param hm The heightmap to use
	 * @return A string representation of this snapshot, usable for XSI exports
	 */
	public String toExportString(HashMap<Element, String> elementNames, int frame, HeightMap hm)	{
		String str = "";
		synchronized(this)	{
			for (int i = 0; i < size(); i++){
				RenderObject rob = get(i);
				if(rob.element instanceof Animal)	{
					Vec pos = new Vec(rob.position);
					Vec vel = new Vec(rob.velocity);
					String name = elementNames.get(rob.element);
					Vec velUnit = vel.unit();
					double posZ = hm.getUnnormalisedInterpolatedHeightAt(pos);
					double velZ = hm.getUnnormalisedInterpolatedHeightAt(pos.plus(velUnit)) - hm.getUnnormalisedInterpolatedHeightAt(pos);
					str += "\n" + name
						+ " " + pos.x
						+ " " + posZ
						+ " " + pos.y
						+ " " + vel.x
						+ " " + velZ
						+ " " + vel.y
						+ " " + frame;
				}
			}
		}
		return str.substring(1);
	}
	
	/**
	 * Returns a map from elements to names
	 * @param s A List of snapshots
	 * @return A map from elements to names
	 */
	public static HashMap<Element, String> getNamesForElements(List<Snapshot> s)	{
		int elements = 0;
		HashMap<Element, String> names = new HashMap<Element, String>();
		for(Snapshot snap : s)
			for(RenderObject r : snap)
				if(!names.containsKey(r.element))
					names.put(r.element, "e" + (elements++));
		return names;
	}
}