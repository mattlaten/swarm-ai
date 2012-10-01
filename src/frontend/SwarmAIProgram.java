package frontend;

import backend.Simulation;

/**
 * The main driver class
 */
public class SwarmAIProgram {
	
	public static void main(String [] args) throws Exception
	{
		//Needs a UI and a Simulation 
		Simulation sim = new Simulation();
		UserInterface ui = new UserInterface(sim);
	}
}
