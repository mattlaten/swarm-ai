package backend;

import java.io.File;
import java.util.ArrayList;

import math.Vec;
import backend.environment.Element;
import backend.environment.Prey;

public class Simulation implements Runnable {
	public ArrayList<Element> elements;
	public HeightMap hm = null;
	
	public Simulation()	{
		elements = new ArrayList<Element>();
		Prey p = new Prey();
		p.position = new Vec(10, 10);
		elements.add(p);
		
		hm = new HeightMap("./maps/GC2.map");
		//hm = new HeightMap();
	}
	
	public void run()	{
		try {
			while(true)	{
				Thread.sleep(200);
				for(Element e : elements)	{
					
				}
			}
		}
		catch(InterruptedException ie)	{}
	}
	
	public void setHeightMap(File map)
	{
		hm = new HeightMap(map);
	}
}
