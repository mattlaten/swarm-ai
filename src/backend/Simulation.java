package backend;

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
		
		//hm = new HeightMap("/media/Data/UCT/2 - CSC3003S/Capstone/src/Terrain+Generation+Data/Terrain Generation Project Data/GC2.map");
		hm = new HeightMap();
	}
}
