package frontend;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;

import math.Vec;
import util.Logger;
import backend.HeightMap;
import backend.Simulation;
import backend.Snapshot;
import backend.environment.Element;
import backend.environment.Predator;
import backend.environment.Prey;
import backend.environment.Waypoint;
import frontend.components.ControlBar;
import frontend.components.PropertiesPanel;
import frontend.components.StatusBar;

/**
 * The UserInterface serves to combine all the frontend components together.
 * It holds the Canvas, the PropertiesPanel, the StatusBar, the Toolbar, the Menubar
 * as well as the Simulation. It also keeps track of the current editting mode the user.
 */
public class UserInterface extends JFrame implements KeyListener {
	Logger log = new Logger(UserInterface.class, System.out, System.err);
	
	public enum Mode {SELECT, PAINT_PREY, PAINT_PREDATOR, PAINT_WAYPOINT};

	/**
	 * The currently running simulation.
	 */
	public Simulation sim;
	Canvas canv;
	
	Element previousWaypoint;
	
	/**
	 * The list of Elements that make up the current selection
	 */
	public HashSet<Element> selection;
	File terrainFile;
	
	Mode mode;
	public StatusBar statusBar = null;
	public ControlBar controlBar;
	public PropertiesPanel properties;
	Toolbar toolbar;
	JPanel viewPort;
	MenuBar menuBar;

	/**
	 * Initiates and shows the UserInterface
	 * @param sim The Simulation object to use
	 */
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
		menuBar = new MenuBar(this);
		
		//set up things
		setSize(1024, 600);
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
		
		setVisible(true);
		sim.start();
	}
	
	/**
	 * Sets the mode of the UserInterface. The mode affects what elements can be selected
	 * by the selection box as well as what the mouse buttons will do when clicked.
	 * @param m The new mode
	 */
	public void setMode(Mode m)	{
		mode = m;
		if(m == Mode.PAINT_WAYPOINT)
			previousWaypoint = null;
	}
	
	/**
	 * Sets the selection to any prey that are found at the given position in space
	 * @param point The position where the selection prey should be
	 * @param addToSelection Whether we're adding to the current selection or defining a completely new selection
	 */
	public void selectPrey(Vec point, boolean addToSelection) {
		//Add prey to selection
		//Colour it differently (green?)
		boolean added = false;
		synchronized(sim.elements)	{
			for (Element e : sim.elements)
			{
				if (e.getPosition().withinRadius(point, e.getSize()))	
				{
					if (!addToSelection)
						selection.clear();
					selection.add(e);
					properties.update();
					added = true;
					//log.info("Added element " + e.getPosition() + " : " + point);
				}
				else
					continue;
					//log.info("Did not add element " + e.getPosition() + " : " + point);
			}
		}
		if (!added)
			selection.clear();
		canv.repaint();
	}
	
	/**
	 * Given the bounds of a selection box, select the prey found inside it
	 * @param start The starting point of the selection box
	 * @param end The ending point of the selection box
	 * @param addToSelection Whether we're adding to the current selection or defining a completely new selection 
	 */
	public void selectBox(Vec start, Vec end, boolean addToSelection)
	{
		double minx = Math.min(start.x, end.x);
		double maxx = Math.max(start.x, end.x);
		double miny = Math.min(start.y, end.y);
		double maxy = Math.max(start.y, end.y);
		
		if (!addToSelection)
			selection.clear();
		
		synchronized(sim.elements)	{
			for (Element e : sim.elements)	{
				if(mode == Mode.SELECT
						|| (mode == Mode.PAINT_PREY && e instanceof Prey)
						|| (mode == Mode.PAINT_PREDATOR && e instanceof Predator)
						|| (mode == Mode.PAINT_WAYPOINT && e instanceof Waypoint))	{
					Vec pos = e.getPosition();
					if (pos.x >= minx && pos.x <= maxx && pos.y >= miny && pos.y <= maxy)
						selection.add(e);
				}
			}
		}
		properties.update();
		canv.repaint();
	}

	/**
	 * Creates a new Animal at the given point
	 * @param mPoint The position in space to create the animal
	 * @param cl The class of the Animal to create (Prey or Predator)
	 */
	public <T> void placeElement(Vec mPoint, Class<T> cl) throws SecurityException, NoSuchMethodException {
		Constructor c = cl.getConstructor(double.class, double.class, double.class, double.class, double.class);
		//convert mPoint to worldspace
		//place prey on closest dot
		double xloc = Math.round((mPoint.x/10))*10;
		double yloc = Math.round(mPoint.y/10)*10;
		
		boolean validLocation = true;
		
		synchronized(sim.elements)	{
			for (Element e : sim.elements)
				if ((e instanceof Prey || e instanceof Predator) && e.getPosition().equals(new Vec(xloc, yloc)))
					validLocation = false;
		}
		
		if (validLocation)
			try {
				sim.elements.add((Element)c.newInstance(xloc,yloc,1,1,5));
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
	
	/**
	 * Places the given Element into the world
	 * @param toAdd The Element to add in the world
	 */
	public void placeElement(Element toAdd)	{
		//convert mPoint to worldspace
		//place prey on closest dot
		Vec v = toAdd.getPosition();
		double xloc = Math.round((v.x/10))*10;
		double yloc = Math.round(v.y/10)*10;
		toAdd.setPosition(new Vec(xloc, yloc));
		
		boolean validLocation = true;
		
		synchronized(sim.elements)	{
			for (Element e : sim.elements)
				if ((e instanceof Prey || e instanceof Predator)
						&& e.getPosition().equals(new Vec(xloc, yloc)))
					validLocation = false;
		}
		
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

	/**
	 * Sets the facing direction of the currently selected Elements
	 * @param mPoint
	 */
	public void setSelectionDirection(Vec mPoint) {
		for (Element e : selection)
			e.setVelocity(mPoint.minus(e.getPosition()));
	}
	
	/**
	 * Clears everyhing: selection, world, time
	 */
	public void clear()
	{
		selection.clear();
		sim.elements.clear();
		sim.elements.clean();
		sim.snapshots.clear();
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
		JMenu file, view, trans;
		JMenuItem 	
					fileNew,
					fileOpen,
					fileClose,
					fileSave,
					fileSaveAs,
					fileExportXSI,
					fileImportTerrain,
					fileExportTerrain,
					fileGenerateRandomTerrain, 
					fileExit,
					transSize,
					transMaxVel,
					transPos;
		JCheckBoxMenuItem 
					viewGrid, 
					viewAxes, 
					viewMap, 
					viewDirections, 
					viewRadii,
					viewQuality;
		JFileChooser fileChooser; 
		
		
		public MenuBar(UserInterface ui)
		{
			final UserInterface uiFinal = ui;
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
					try	{
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File("./saves/"));
						int returnVal = chooser.showOpenDialog(uiFinal);
						if(returnVal == JFileChooser.APPROVE_OPTION)
							sim.loadSimulationFromFile(chooser.getSelectedFile());
					}
					catch(IOException ioe)	{
						System.out.println("IO Error");
					}
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
					try	{
						boolean b = sim.saveSimulation();
						if(!b)	{
							JFileChooser chooser = new JFileChooser();
							chooser.setCurrentDirectory(new File("./saves/"));
							int returnVal = chooser.showSaveDialog(uiFinal);
							if(returnVal == JFileChooser.APPROVE_OPTION)
								sim.saveSimulationToFile(chooser.getSelectedFile());
						}	
					}
					catch(IOException ioe)	{
						System.out.println("IO Error");
					}
				}
			});
			
			fileSaveAs = new JMenuItem("Save As...");
			fileSaveAs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					try	{
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File("./saves/"));
						int returnVal = chooser.showSaveDialog(uiFinal);
						if(returnVal == JFileChooser.APPROVE_OPTION)
							sim.saveSimulationToFile(chooser.getSelectedFile());
					}
					catch(IOException ioe)	{
						System.out.println("IO Error");
					}
				}
			});
			
			fileExportXSI = new JMenuItem("Export XSI");
			fileExportXSI.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					try	{
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File("./saves/"));
						int returnVal = chooser.showSaveDialog(uiFinal);
						if(returnVal == JFileChooser.APPROVE_OPTION)
							sim.exportSimulationToFile(chooser.getSelectedFile());
					}
					catch(IOException ioe)	{
						System.out.println("IO Error");
					}
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
			
			viewQuality = new JCheckBoxMenuItem("High Quality Rendering", canv.highQualityRender);
			viewQuality.addActionListener(new ActionListener()	{
				public void actionPerformed(ActionEvent ae)	{
					canv.highQualityRender = viewQuality.isSelected();
					canv.repaint();
				}
			});
			
			transSize = new JMenuItem("Set Selection Size");
			transSize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					if (selection.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Selection cannot be empty!");
					}
					else {
						double size = Double.parseDouble(JOptionPane.showInputDialog("Enter Size"));
						for (Element e : selection)
							e.setSize(size);
					}
				}
			});

			transMaxVel = new JMenuItem("Set Selection Max Velocity");
			transMaxVel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					if (selection.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Selection cannot be empty!");
					}
					else {
						double maxVel = Double.parseDouble(JOptionPane.showInputDialog("Enter Max Velocity"));
						for (Element e : selection)
							e.setMaxSpeed(maxVel);
					}
				}
			});
			
			transPos = new JMenuItem("Set Selection Position");
			transPos.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae)	{
					if (selection.isEmpty()) {
						JOptionPane.showMessageDialog(null, "Selection cannot be empty!");
					}
					else {
						//double maxVel = Double.parseDouble(JOptionPane.showInputDialog("Enter Max Velocity"));
						//for (Element e : selection)
						//	e.setMaxSpeed(maxVel);
					}
				}
			});
			
			
			file = new JMenu("File");
				file.add(fileNew);
				file.add(fileOpen);
				file.add(fileClose);
				file.addSeparator();
				file.add(fileSave);
				file.add(fileSaveAs);
				file.add(fileExportXSI);
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
				view.addSeparator();
				view.add(viewQuality);
			trans = new JMenu("Transform");
				trans.add(transSize);
				trans.add(transMaxVel);
				trans.add(transPos);
			add(file);
			add(view);
			//add(trans);
		}
	}

	private class Toolbar extends JPanel {

		JRadioButton modeSelect, modePrey, modePredator, modeModifier, 
		modeObstacle, modeLoad, modeRandom, modeWaypoint;
		JCheckBox trackButton;
		
		public Toolbar()
		{
			//BUTTONS
			modeSelect = new JRadioButton("Select");
			modeSelect.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Selecting");
					setMode(Mode.SELECT);
				}
			});
			modeSelect.setSelected(true);
			statusBar.setMode("Selecting");
			setMode(Mode.SELECT);
			
			modePrey = new JRadioButton("Prey");
			modePrey.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Placing prey");
					setMode(Mode.PAINT_PREY);
				}
			});
			
			modePredator = new JRadioButton("Predator");
			modePredator.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Placing predator");
					setMode(Mode.PAINT_PREDATOR);
				}
			});
			
			modeModifier = new JRadioButton("Modifier");
			modeModifier.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Placing modifier");
				}
			});
			
			modeObstacle = new JRadioButton("Obstacle");
			modeObstacle.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Placing obstacle");
				}
			});
			
			modeWaypoint = new JRadioButton("Waypoint");
			modeWaypoint.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					statusBar.setMode("Placing waypoints");
					setMode(Mode.PAINT_WAYPOINT);
				}
			});
			
			trackButton = new JCheckBox("Track");
			trackButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent ae)	{
					canv.track = trackButton.isSelected();
				}
			});
			
			ButtonGroup modeGroup = new ButtonGroup();
			modeGroup.add(modeSelect);
			modeGroup.add(modePrey);
			modeGroup.add(modePredator);
			//modeGroup.add(modeModifier);
			//modeGroup.add(modeObstacle);
			modeGroup.add(modeWaypoint);

			//TOOLBAR
			setLayout(new FlowLayout());
			getContentPane().setLayout(new BorderLayout());
			JPanel modesPanel = new JPanel();
			modesPanel.setBorder(new TitledBorder("Mode"));
			modesPanel.setLayout(new FlowLayout());
			modesPanel.add(modeSelect);
			modesPanel.add(modePrey);
			modesPanel.add(modePredator);
			//modesPanel.add(modeModifier);
			//modesPanel.add(modeObstacle);
			modesPanel.add(modeWaypoint);
			
			JPanel trackingPanel = new JPanel();
			trackingPanel.setBorder(new TitledBorder("Tracking"));
			trackingPanel.setLayout(new FlowLayout());
			trackingPanel.add(trackButton);
			
			add(modesPanel);
			add(trackingPanel);
		}
	}
}

