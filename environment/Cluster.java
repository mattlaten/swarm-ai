package environment;

import java.util.ArrayList;

public class Cluster extends Element implements Serializable	{
	public ArrayList<Element> elements;
	
	public Cluster()	{
		elements = new ArrayList<Element>();
	}
}
