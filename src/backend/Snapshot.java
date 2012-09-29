package backend;

import java.util.ArrayList;

import backend.environment.Element;

/**
 * 
 *
 */
class Snapshot extends ArrayList<RenderObject>	{
	int timeTaken;
	
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