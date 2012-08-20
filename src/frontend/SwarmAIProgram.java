package frontend;

import backend.Simulation;

public class SwarmAIProgram {
	
	public static void main(String [] args)
	{
		//Needs a UI and a Simulation 
		Simulation sim = new Simulation();
		UserInterface ui = new UserInterface(sim);
	}
}
