package backend;

import java.io.File;
import java.util.ArrayList;

import math.Vec;
import backend.environment.Element;
import backend.environment.Prey;

public class Simulation extends Thread {
	public ArrayList<Element> elements;
	public HeightMap hm = null;
	public ArrayList<Object> snapshots;
	
	public int timeStep = 20, stepsPerSave = 10;
	private volatile int time = 0, totalTime = 0;
	
	public boolean isRunning = false;
	
	public Simulation()	{
		elements = new ArrayList<Element>();
		/*Prey p = new Prey();
		p.position = new Vec(10, 10);
		p.velocity = new Vec(1,1).truncate(p.getMaxSpeed());
		elements.add(p);
		p = new Prey();
		p.position = new Vec(20, 10);
		p.velocity = new Vec(1,1).truncate(p.getMaxSpeed());
		elements.add(p);*/
		
		snapshots = new ArrayList<Object>();
		
		hm = new HeightMap(new File("./maps/GC2.map"));
		//hm = new HeightMap();
	}
	
	public void run()	{
		try {
			while(true)	{
				Thread.sleep(timeStep);
				if(!isRunning)	continue;
				boolean upTT = totalTime <= time;
				time += timeStep;
				if(upTT)
					totalTime = time;
				if(time == totalTime && time % (timeStep*stepsPerSave) == 0)	{
					snapshots.add(elements.clone());
				}
				for(Element e : elements)
					e.calculateUpdate(elements);
				for(Element e : elements)	{
					e.update();
				}
			}
		}
		catch(InterruptedException ie)	{}
	}
	
	public void setTime(int t)	{
		time = Math.max(t - t%timeStep, 0);
	}
	public int getTime()	{
		return time;
	}
	
	public int getTotalTime()	{
		return totalTime;
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
