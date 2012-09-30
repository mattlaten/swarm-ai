package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	
	public int compareTo(Snapshot other)	{
		if(other.timeTaken > timeTaken)
			return -1;
		else if(other.timeTaken < timeTaken)
			return 1;
		return 0;
	}
	
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