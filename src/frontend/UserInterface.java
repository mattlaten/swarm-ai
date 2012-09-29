package frontend;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import math.Vec;
import util.Logger;
import backend.HeightMap;
import backend.Simulation;
import backend.environment.Element;
import backend.environment.Predator;
import backend.environment.Prey;
import backend.environment.Waypoint;
import frontend.components.ControlBar;
import frontend.components.PropertiesPanel;
import frontend.components.StatusBar;

public class UserInterface extends JFrame implements KeyListener {
	
	Logger log = new Logger(UserInterface.class, System.out, System.err);

	public Simulation sim;
	Canvas canv;
	
	Element previousWaypoint;
	
	public HashSet<Element> selection;
	File terrainFile;
	
	public enum Mode {SELECT, PAINT_PREY, PAINT_PREDATOR, PAINT_WAYPOINT};
	Mode mode;
	StatusBar statusBar = null;
	ControlBar controlBar;
	PropertiesPanel properties;
	Toolbar toolbar;
	JPanel viewPort;
	MenuBar menuBar;

	public UserInterface(final Simulation sim) throws Exception	{
		super("Swarm AI");
		this.sim = sim;
		mode = Mode.SELECT;
		
		addKeyListener(this);
		
		selection = new HashSet<Element>();
		
		canv = new Canvas(this);
		
		properties = new PropertiesPanel(this);
		controlBar = new ControlBar(sim);
		statusBar = new StatusBar();
		toolbar = new Toolbar();
		menuBar = new MenuBar();
		
		//set up things
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
			
		setJMenuBar(menuBar);
		
		viewPort = new JPanel();
		viewPort.setLayout(new BorderLayout());
		viewPort.add(toolbar, BorderLayout.PAGE_START);
		viewPort.add(canv, BorderLayout.CENTER);
		viewPort.add(controlBar, BorderLayout.PAGE_END);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(properties);
		splitPane.add(viewPort);
		
		getContentPane().add(statusBar, BorderLayout.PAGE_END);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		/*PropertyDialog pd = new PropertyDialog(this);
		pd.targetEntity(sim.elements.get(0));*/
		
		setVisible(true);
		sim.start();
	}
	
	public void setMode(Mode m)	{
		mode = m;
		if(m == Mode.PAINT_WAYPOINT)
			previousWaypoint = null;
	}
	
	public void selectPrey(Vec point, boolean addToSelection) {
		//Add prey to selection
		//Colour it differently (green?)
		boolean added = false;
		for (Element e : sim.elements)
		{
			if (e.getPosition().withinRadius(point, e.getSize()))	
			{
				if (!addToSelection)
					selection.clear();
				selection.add(e);
				try {
					properties.targetEntity(e);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				added = true;
				//log.info("Added element " + e.getPosition() + " : " + point);
			}
			else
				continue;
				//log.info("Did not add element " + e.getPosition() + " : " + point);
		}
		if (!added)
			selection.clear();
		canv.repaint();
	}
	
	public void selectBox(Vec start, Vec end, boolean addToSelection)
	{
		double minx = Math.min(start.x, end.x);
		double maxx = Math.max(start.x, end.x);
		double miny = Math.min(start.y, end.y);
		double maxy = Math.max(start.y, end.y);
		
		if (!addToSelection)
			selection.clear();
		
		for (Element e : sim.elements)	{
			Vec pos = e.getPosition();
			if (pos.x >= minx && pos.x <= maxx && pos.y >= miny && pos.y <= maxy)
				selection.add(e);
		}
		canv.repaint();
	}

	public <T> void placeElement(Vec mPoint, Class<T> cl) throws SecurityException, NoSuchMethodException {
		Constructor c = cl.getConstructor(double.class, double.class, double.class, double.class, double.class);
		//convert mPoint to worldspace
		//place prey on closest dot
		double xloc = Math.round((mPoint.x/10))*10;
		double yloc = Math.round(mPoint.y/10)*10;
		
		boolean validLocation = true;
		
		Waypoint firstWaypoint = null;
		for(Element e : sim.elements)
			if(e instanceof Waypoint)	{
				firstWaypoint = (Waypoint)e;
				break;
			}
		
		for (Element e : sim.elements)
			if ((e instanceof Prey || e instanceof Predator) && e.getPosition().equals(new Vec(xloc, yloc)))
				validLocation = false;
		
		if (validLocation)
			try {
				sim.elements.add((Element)c.newInstance(xloc,yloc,1,1,5));
				sim.elements.get(sim.elements.size()-1).setTarget(firstWaypoint);
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		canv.repaint();
	}
	
	public void placeElement(Element toAdd)	{
		//convert mPoint to worldspace
		//place prey on closest dot
		Vec v = toAdd.getPosition();
		double xloc = Math.round((v.x/10))*10;
		double yloc = Math.round(v.y/10)*10;
		toAdd.setPosition(new Vec(xloc, yloc));
		
		boolean validLocation = true;
		
		for (Element e : sim.elements)
			if ((e instanceof Prey || e instanceof Predator)
					&& e.getPosition().equals(new Vec(xloc, yloc)))
				validLocation = false;
		
		if (validLocation)	{
			sim.elements.add(toAdd);
			if(toAdd instanceof Waypoint)	{
				if(previousWaypoint != null && previousWaypoint.getTarget() == null)
					previousWaypoint.setTarget((Waypoint)toAdd);
				previousWaypoint = toAdd;
			}
		}
		canv.repaint();
	}

	public void setSelectionDirection(Vec mPoint) {
		for (Element e : selection)
			e.setVelocity(mPoint.minus(e.getPosition()));
	}
	
	public void clear()
	{
		selection.clear();
		sim.elements.clear();
		sim.setTime(0);
		sim.setTotalTime(0);
	}

	//@Override
	public void keyPressed(KeyEvent event) {
		
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void keyTyped(KeyEvent event) {
		// TODO Auto-generated method stub
		char eventChar = event.getKeyChar();
        int eventCode = event.getKeyCode();
        System.out.println(eventChar);
        System.out.println(eventCode);
        if (eventChar == ' ')
        {
            controlBar.flip();
        }
	}
	
	private class MenuBar extends JMenuBar {
		
		JMenuBar menubar;
		JMenu file, view;
		JMenuItem 	
					fileNew,
					fileOpen,
					fileClose,
					fileSave,
					fileSaveAs,
					fileImportTerrain,
					fileExportTerrain,
					fileGenerateRandomTerrain, fileExit;
		JCheckBoxMenuItem 
					viewGrid, 
					viewAxes, 
					viewMap, 
					viewDirections, 
					viewRadii;
		JFileChooser fileChooser; 
		
		
		public MenuBar()
		{
			fileChooser = new JFileChooser("./maps/");
			
			//FILE
			
			fileNew = new JMenuItem("New");
			fileNew.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					clear();
				}
			});
			
			fileOpen = new JMenuItem("Open...");
			fileOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					System.exit(0);
				}
			});
			
			fileClose = new JMenuItem("Close");
			fileClose.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					System.exit(0);
				}
			});
			
			fileSave = new JMenuItem("Save");
			fileSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					System.exit(0);
				}
			});
			
			fileSaveAs = new JMenuItem("Save As...");
			fileSaveAs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					System.exit(0);
				}
			});
			
			fileImportTerrain = new JMenuItem("Import Terrain...");
			fileImportTerrain.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					if (!sim.isRunning)
					{
						fileChooser.setCurrentDirectory(new File("./maps/"));
						statusBar.setMode("Select Terrain");
						int returnVal = fileChooser.showOpenDialog(UserInterface.this);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
				            terrainFile = fileChooser.getSelectedFile();
				            sim.loadHeightMap(terrainFile);
				            canv.hmc.setHeightMap(sim.hm);
				            log.info("Importing Terrain: " + terrainFile.getName());
				            
				        }
						statusBar.setMode("");
					}
					else
						statusBar.setMode("Can't change terrain while simulation is running");
				}
			});
			
			fileExportTerrain = new JMenuItem("Export Terrain...");
			fileExportTerrain.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae){
					fileChooser.setCurrentDirectory(new File("./maps/"));
					int returnVal = fileChooser.showSaveDialog(UserInterface.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
			            terrainFile = fileChooser.getSelectedFile();
			            try {
							sim.hm.writeOBJ(terrainFile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            log.info("Exporting Terrain: " + terrainFile.getName());
			            
			        }
					statusBar.setMode("");
					
			}});
			
			fileGenerateRandomTerrain = new JMenuItem("Generate Random Terrain");
			fileGenerateRandomTerrain.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					if (!sim.isRunning)
					{
						statusBar.setMode("Generating Random Terrain");
						sim.hm = new HeightMap();
						sim.hm.generateRandomHeights();
						canv.hmc.setHeightMap(sim.hm);
						statusBar.setMode("");
					}
					else
						statusBar.setMode("Can't change terrain while simulation is running!");
			}});
			
			fileExit = new JMenuItem("Exit");
			fileExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					System.exit(0);
				}
			});
			
			//VIEW
			viewGrid = new JCheckBoxMenuItem("Show Grid", canv.renderGrid);
			viewGrid.addActionListener(new ActionListener()	{
				public void actionPerformed(ActionEvent ae)	{
					canv.renderGrid = viewGrid.isSelected();
					canv.repaint();
				}
			});
			
			viewAxes = new JCheckBoxMenuItem("Show Axes", canv.renderAxes);
			viewAxes.addActionListener(new ActionListener()	{
				public void actionPerformed(ActionEvent ae)	{
					canv.renderAxes = viewAxes.isSelected();
					canv.repaint();
				}
			});

			viewMap = new JCheckBoxMenuItem("Show Map", canv.renderHeightMap);
			viewMap.addActionListener(new ActionListener()	{
				public void actionPerformed(ActionEvent ae)	{
					canv.renderHeightMap = viewMap.isSelected();
					canv.repaint();
				}
			});
			
			viewDirections = new JCheckBoxMenuItem("Show Directional Vectors", canv.renderDirections);
			viewDirections.addActionListener(new ActionListener()	{
				public void actionPerformed(ActionEvent ae)	{
					canv.renderDirections = viewDirections.isSelected();
					canv.repaint();
				}
			});
			
			viewRadii = new JCheckBoxMenuItem("Show Radii", canv.renderRadii);
			viewRadii.addActionListener(new ActionListener()	{
				public void actionPerformed(ActionEvent ae)	{
					canv.renderRadii = viewRadii.isSelected();
					canv.repaint();
				}
			});

			
			file = new JMenu("File");
				file.add(fileNew);
				file.add(fileOpen);
				file.add(fileClose);
				file.addSeparator();
				file.add(fileSave);
				file.add(fileSaveAs);
				file.addSeparator();
				file.add(fileImportTerrain);
				file.add(fileExportTerrain);
				file.add(fileGenerateRandomTerrain);
				file.addSeparator();
				file.add(fileExit);
			view = new JMenu("View");
				view.add(viewGrid);
				view.add(viewAxes);
				view.add(viewMap);
				view.addSeparator();
				view.add(viewDirections);
				view.add(viewRadii);
			add(file);
			add(view);
		}
	}

	private class Toolbar extends JPanel {

		JButton modeSelect, modePrey, modePredator, modeModifier, 
		modeObstacle, modeLoad, modeRandom, trackButton, modeWaypoint;
		
		public Toolbar()
		{
			//BUTTONS
			modeSelect = new JButton("Select");
			modeSelect.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Selecting");
					setMode(Mode.SELECT);
				}
			});
			
			modePrey = new JButton("Prey");
			modePrey.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Placing prey");
					setMode(Mode.PAINT_PREY);
				}
			});
			
			modePredator = new JButton("Predator");
			modePredator.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Placing predator");
					setMode(Mode.PAINT_PREDATOR);
				}
			});
			
			modeModifier = new JButton("Modifier");
			modeModifier.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Placing modifier");
				}
			});
			
			modeObstacle = new JButton("Obstacle");
			modeObstacle.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Placing obstacle");
				}
			});
			
			modeWaypoint = new JButton("Waypoint");
			modeWaypoint.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Placing waypoints");
					setMode(Mode.PAINT_WAYPOINT);
				}
			});
			
			trackButton = new JButton("Track");
			trackButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					canv.track = !canv.track;
				}
			});

			//TOOLBAR
			setLayout(new FlowLayout());
			getContentPane().setLayout(new BorderLayout());
			add(modeSelect);
			add(modePrey);
			add(modePredator);
			add(modeModifier);
			add(modeObstacle);
			add(modeWaypoint);
			add(trackButton);
		}
	}
}

