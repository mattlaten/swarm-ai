package backend;

import java.io.File;
import java.util.ArrayList;

import math.Vec;
import backend.environment.Element;
import backend.environment.Prey;

public class Simulation {
	public ArrayList<Element> elements;
	public HeightMap hm = null;
	
	public Simulation()	{
		elements = new ArrayList<Element>();
		Prey p = new Prey();
		p.position = new Vec(10, 10);
		elements.add(p);
		hm = new HeightMap();
	}
	
	public void loadHeightMap(File map)
	{
		hm = new HeightMap(map);
	}
	
	public void setHeightMap(HeightMap hm)
	{
		this.hm = hm;
	}
}
