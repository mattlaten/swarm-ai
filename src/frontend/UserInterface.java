package frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import math.Vec;
import util.Logger;
import backend.HeightMap;
import backend.Simulation;
import backend.environment.Element;
import backend.environment.Property;

public class UserInterface extends JFrame {
	
	Logger log = new Logger(UserInterface.class, System.out, System.err);
	JPanel toolbar;
	JButton modePrey, modePredator, modeModifier, modeObstacle, modeLoad, modeRandom, startStop;
	JFileChooser fc;
	
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
		});
		
		fileGenerateRandomTerrain = new JMenuItem("Generate Random Terrain");
		fileGenerateRandomTerrain.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				status.setMode("Generating Random Terrain");
				sim.setHeightMap(new HeightMap());
		        canv.hmc.setHeightMap(sim.hm);
		        canv.hmc.render();
				status.setMode("");
			}
		});
		
		fileExit = new JMenuItem("Exit");
		fileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)	{
				System.exit(0);
			}
		});
		
		/*startStop = new JButton("Start");
		startStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				if (sim.isRunning)
				{
					status.setMode("Simulation Stopped");
					startStop.setText("Start");
					sim.isRunning = false;
					
				}
				else
				{
					status.setMode("Running Simulation");
					startStop.setText("Stop");
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
		//toolbar.add(modeLoad);
		//toolbar.add(modeRandom);
		//toolbar.add(startStop);
		
		getContentPane().add(toolbar, BorderLayout.PAGE_START);
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

