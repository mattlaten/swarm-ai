package frontend;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import util.Logger;
import backend.HeightMap;
import backend.Simulation;

public class UserInterface extends JFrame {
	
	Logger log = new Logger(UserInterface.class, System.out, System.err);
	JPanel toolbar;// control, menu, viewPort, viewPortControl;
	JButton modePrey, modePredator, modeModifier, modeObstacle, modeLoad, modeRandom, startStop;
	JFileChooser fc;
	
	PropertiesPanel properties;
	
	JMenuBar menubar;
	JMenu file;
	JMenuItem fileLoadTerrain, fileGenerateRandomTerrain, fileExit;
	
	File terrainFile;
	
	Simulation sim;
	Canvas canv;
	
	StatusBar status;
	
	public UserInterface(final Simulation sim) throws Exception	{
		super("Swarm AI");
		this.sim = sim;
		
		//set up things
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		canv = new Canvas(this);
		
		status = new StatusBar();
		
		fc = new JFileChooser("./maps/");

		properties = new PropertiesPanel();
		
		modePrey = new JButton("Prey");
		modePrey.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				status.setMode("Placing prey");
			}
		});
		modePredator = new JButton("Predator");
		modePredator.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				status.setMode("Placing predator");
			}
		});
		modeModifier = new JButton("Modifier");
		modeModifier.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				status.setMode("Placing modifier");
			}
		});
		modeObstacle = new JButton("Obstacle");
		modeObstacle.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				status.setMode("Placing obstacle");
			}
		});
		
		fileLoadTerrain = new JMenuItem("Load Terrain");
		fileLoadTerrain.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				if (!sim.isRunning)
				{
					status.setMode("Select Terrain");
					int returnVal = fc.showOpenDialog(UserInterface.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
			            terrainFile = fc.getSelectedFile();
			            sim.loadHeightMap(terrainFile);
			            canv.hmc.setHeightMap(sim.hm);
			            canv.hmc.render();
			            log.info("Opening: " + terrainFile.getName());
			            
			        }
					status.setMode("");
				}
				else
				{
					status.setMode("Can't change terrain while simulation is running!");					
				}
			}
		});
		
		fileGenerateRandomTerrain = new JMenuItem("Generate Random Terrain");
		fileGenerateRandomTerrain.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				if (!sim.isRunning)
				{
					status.setMode("Generating Random Terrain");
					sim.setHeightMap(new HeightMap());
					canv.hmc.setHeightMap(sim.hm);
					canv.hmc.render();
					status.setMode("");
				}
				else
				{
					status.setMode("Can't change terrain while simulation is running!");
				}
		}});
		
		fileExit = new JMenuItem("Exit");
		fileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)	{
				System.exit(0);
			}
		});
		
		/*
		startStop = new JButton("Start");
		startStop.setBackground(Color.GREEN);
		startStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				if (sim.isRunning)
				{
					status.setMode("Simulation Stopped");
					startStop.setText("Start");
					startStop.setBackground(Color.GREEN);
					sim.isRunning = false;
					
				}
				else
				{
					status.setMode("Running Simulation");
					startStop.setText("Stop");
					startStop.setBackground(Color.RED);
					sim.isRunning = true;
					sim.start();
				}
			}
		});*/
		
		menubar = new JMenuBar();
		file = new JMenu("File");
		file.add(fileLoadTerrain);
		file.add(fileGenerateRandomTerrain);
		file.addSeparator();
		file.add(fileExit);
		
		menubar.add(file);
		
		setJMenuBar(menubar);
		
		toolbar = new JPanel();
		toolbar.setLayout(new FlowLayout());
		
		getContentPane().setLayout(new BorderLayout());
		
		//add things
		toolbar.add(modePrey);
		toolbar.add(modePredator);
		toolbar.add(modeModifier);
		toolbar.add(modeObstacle);
		
		getContentPane().add(toolbar, BorderLayout.PAGE_START);
		getContentPane().add(properties, BorderLayout.LINE_START);
		getContentPane().add(status, BorderLayout.PAGE_END);
		
		JPanel centerThings = new JPanel();
		centerThings.setLayout(new BorderLayout());
		centerThings.add(canv, BorderLayout.CENTER);
		centerThings.add(new ControlBar(), BorderLayout.PAGE_END);
		
		getContentPane().add(centerThings, BorderLayout.CENTER);
		/*PropertyDialog pd = new PropertyDialog(this);
		pd.targetEntity(sim.elements.get(0));*/
		
		setVisible(true);
	}
}

