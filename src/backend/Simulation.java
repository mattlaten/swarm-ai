package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import backend.environment.Element;
import frontend.UserInterface;

/**
 * The Simulation handles the interaction between elements and each
 * other, as well as the environment. It also takes care of the timeline,
 * allowing the user to backtrack to previous points in the simulation and
 * replay them.
 * 
 * To achieve backtracking, every ten frames a "snapshot" of the world is stored.
 * If a certain time is requested (by clicking on the control bar) then the latest
 * snapshot before the requested time is found and the positions and velocities
 * of the Elements is extrapolated from there (this is possible because the simulation
 * is purely deterministic).
 * 
 * The snapshots are not only stored at every tenth frame. After all, if there is
 * a change (by a user) between two of these "captures" then we must record that too.
 */
public class Simulation extends Thread implements Serializable {
	public UnforgivingArrayList<Element> elements;
	public HeightMap hm = null;
	public ArrayList<Snapshot> snapshots;
	
	private int timeStep = 20, stepsPerSave = 10;
	public volatile int time = 0, totalTime = 0;
	
	public boolean isRunning = false;
	public boolean saved = false;
	
	public Simulation()	{
		elements = new UnforgivingArrayList<Element>(0);
		
		snapshots = new ArrayList<Snapshot>();
		
		hm = new HeightMap(new File("./maps/GC2.map"));
		setName("Simulation");
	}
	
	/**
	 * The main thread for the simulation
	 */
	public void run()	{
		try {
			while(true)	{
				//Make the thread sleep
				Thread.sleep(timeStep);
				
				//If the Simulation isn't running then we're not going to update the elements
				if(!isRunning)	continue;
				
				/* upTT tells us if the current time whould update the total time.
				 * This happens in the case where the current and total time are the same
				 */
				boolean upTT = totalTime <= time;
				//move time up
				time += timeStep;
				//update total time if need be
				if(upTT)
					totalTime = time;
				
				/* find the snapshot index for the current time
				 * if we're at the end, then this will be too big
				 */
				int ind = getSnapshotsIndex(time);
				
				/* if we're at a capturing point in time and if
				 * we're updating total time or if we can't find a
				 * snapshot to save, then we will create a brand new
				 * snapshot and append it to the snapshot list
				 */
				if(time % (timeStep*stepsPerSave) == 0 && (time == totalTime || ind >= snapshots.size()))	{
					Snapshot ss = new Snapshot(elements.size(), time);
					for(Element e : elements)
						ss.add(new RenderObject(e));
					snapshots.add(ss);
					elements.clean();
					update();
				}
				/* If we found a snapshot that we are currently on then we need
				 * to show that
				 */
				else if(ind < snapshots.size() && snapshots.get(ind).timeTaken == time)	{
					/* if the element list has been updated in any way, then we need
					 * to update the current snapshot and discard all future snapshots
					 */
					if(elements.isDirty())	{
						while(snapshots.size() > ind+1)
							snapshots.remove(snapshots.size()-1);
						Snapshot l = snapshots.get(ind);
						for(Element e: elements.removed)
							l.remove(new RenderObject(e));
						for(Element e: elements.added)
							l.add(new RenderObject(e));
						setTotalTime(time);
					}
					apply(ind);
				}
				/* otherwise, if we aren't on a snapshot, then and the elements
				 * have changed in any way, then we need to create a new snapshot
				 * and discard all future ones.
				 */
				else if(elements.isDirty())	{
					Snapshot ss = new Snapshot(elements.size(), time);
					for(Element e : elements)
						ss.add(new RenderObject(e));
					while(snapshots.size() > 0
							&& snapshots.get(snapshots.size()-1).timeTaken > time)
						snapshots.remove(snapshots.size()-1);
					snapshots.add(ss);
					elements.clean();
					setTotalTime(time);
					update();
				}
				else
					update();
			}
		}
		catch(InterruptedException ie)	{}
	}
	
	/**
	 * Sets the simulation to the data stored in the snapshot
	 * @param i The index of the snapshot to use
	 */
	private void apply(int i)	{
		if(i >= snapshots.size() || i < 0)
			return;
		synchronized(elements)	{
			elements.clear();
			for(RenderObject r : snapshots.get(i))	{
				r.apply();
				elements.add(r.element);
			}
			elements.clean();
		}
	}
	
	/**
	 * Goes through the element list, telling each element to calculate its update
	 * and then applying the updates
	 */
	private void update()	{
		synchronized(elements)	{
			for(Element e : elements)
				e.calculateUpdate(elements, hm);
			for(Element e : elements)
				e.update();
		}
	}
	
	/**
	 * Finds the latest timeStep before the given time
	 * @param t The time to round off
	 */
	private int getRoundedTime(int t)	{
		int timeDiff = t%timeStep;
		if(timeDiff < timeStep*0.5)
			timeDiff = -timeDiff;
		else
			timeDiff = timeStep-timeDiff;
		return Math.min(Math.max(t + timeDiff, 0), totalTime);
	}
	
	/**
	 * returns the index of the latest snapshot before the given time
	 * @param t The time to find the snapshot for
	 */
	private int getSnapshotsIndex(int t)	{
		//return (int)((double)(t/timeStep)/stepsPerSave);
		for(int i = 0; i < snapshots.size(); i++)
			if(snapshots.get(i).timeTaken >= t)
				return i;
		return snapshots.size();
	}
	
	/**
	 * Sets the current time of the simulation
	 * @param t The new time for the simulation
	 */
	public void setTime(int t)	{
		//time = Math.max(t - t%timeStep, 0);
		isRunning = false;
		time = getRoundedTime(t);
		int ind = Math.min(snapshots.size()-1, getSnapshotsIndex(time));
		
		apply(ind);
		
		//now figure out how many frames to extrapolate for and then extrapolate
		int steps = time/timeStep - ind*stepsPerSave;
		for(int i = 0; i < steps; i++)
			update();
	}
	
	/**
	 * Sets the total time for the simulation
	 * @param t The new total time
	 */
	public void setTotalTime(int t)	{
		totalTime = Math.max(t - t%timeStep, 0);
	}
	
	/**
	 * Returns the current time for the simulation
	 * @return The current time for the simulation
	 */
	public int getTime()	{
		return time;
	}
	
	/**
	 * Returns the total time for the simulation
	 * @return The tital time for the simulation
	 */
	public int getTotalTime()	{
		return totalTime;
	}
	
	/**
	 * Loads the given heightmap into the simulation
	 * @param map The heightmap to load
	 */
	public void loadHeightMap(File map) 	{
		hm = new HeightMap(map);
	}
	
	/**
	 * Sets a preloaded heightmap for the simulation
	 * @param hm The new heightMap for the simulation
	 */
	public void setHeightMap(HeightMap hm)	{
		this.hm = hm;
	}
	
	/**
	 * Saves this simulation to a file. This file can later be opened using
	 * the loadSimulationFromFile(...) method
	 * @param f The file to save the simulation to
	 * @throws IOException If there is an issue with File IO
	 */
	public void saveSimulationToFile(File f) throws IOException	{
		PrintWriter out = new PrintWriter(new FileWriter(f));
		HashMap<Element, String> names = Snapshot.getNamesForElements(snapshots);
		out.println(hm.heightMapFile.getPath());
		for(Snapshot s : snapshots)
			out.println(s.toString(names));
		out.close();
	}
	
	/**
	 * Exports the simulation into and XSI format. This format has considerably
	 * less information in it and is used to interfacing with SoftImage
	 * @param f The file to save to
	 * @throws IOException If there is an issue with File IO
	 */
	public void exportSimulationToFile(File f)	throws IOException	{
		PrintWriter out = new PrintWriter(new FileWriter(f));
		HashMap<Element, String> names = Snapshot.getNamesForElements(snapshots);
		out.println(hm.heightMapFile.getPath());
		String nameLine = "";
		for(Element e: names.keySet())
			nameLine += " " + names.get(e);
		out.println(nameLine.substring(1));
		for(Snapshot s : snapshots)
			out.println(s.toExportString(names, s.timeTaken/timeStep, hm));
		out.close();
	}
	
	/**
	 * Loads a simulation from the given file. Any file saved with the saveSimulationToFile(...)
	 * method can be loaded with this function.
	 * @param f The file to load from
	 * @throws IOException If there is an issue with File IO
	 */
	public void loadSimulationFromFile(File f)	throws IOException	{
		BufferedReader in = new BufferedReader(new FileReader(f));
		HashMap<String, Element> elements = new HashMap<String, Element>();
		snapshots.clear();
		elements.clear();
		time = 0;
		Snapshot current = null;
		String heightMapName = in.readLine();
		if(heightMapName.equals("-1") || !new File(heightMapName).exists())
			hm = new HeightMap();
		else
			hm = new HeightMap(new File(heightMapName));
		while(in.ready())	{
			String line = in.readLine();
			if(line.equals("{"))
				current = new Snapshot(0, Integer.parseInt(in.readLine()));
			else if(line.equals("}") && current != null)
				snapshots.add(current);
			else if(current != null)
				current.add(RenderObject.fromString(line, elements));
		}
		if(snapshots.size() > 0)	{
			totalTime = snapshots.get(snapshots.size()-1).timeTaken;
			apply(0);
		}
		else
			totalTime = 0;
		in.close();
	}
}
