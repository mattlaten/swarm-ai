package backend;

import java.util.ArrayList;

import math.Vec;
import backend.environment.Element;
import backend.environment.Prey;

public class Simulation {
	public ArrayList<Element> elements;
	
	public Simulation()	{
		elements = new ArrayList<Element>();
		Prey p = new Prey();
		p.position = new Vec(10, 10);
		elements.add(p);
	}
}
