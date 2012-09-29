package backend;

import java.util.ArrayList;

import backend.environment.Element;

/**
 * Snapshot is a class used to store the state of 
 * a simulation at a distinct point in time 
 */
class Snapshot extends ArrayList<RenderObject>	{
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
	
	
	public String toString(){
		String str = "";
		for (int i = 0; i < this.size(); i++){
			RenderObject rob = this.get(i);
			str = rob.toString() + '\n';
		}
		return str;
	}
}