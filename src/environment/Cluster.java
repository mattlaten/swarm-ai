package environment;

import java.io.Serializable;
import java.util.ArrayList;

public class Cluster extends Element implements Serializable	{
	public ArrayList<Element> elements;
	
	public Cluster()	{
		elements = new ArrayList<Element>();
	}
	
	public RenderObject getRenderObject()	{
		return null;
	}
}
