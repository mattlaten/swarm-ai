package frontend;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
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
import backend.environment.Prey;

public class UserInterface extends JFrame implements KeyListener {
	
	Logger log = new Logger(UserInterface.class, System.out, System.err);
	JPanel toolbar, viewPort;
	JButton modeSelect, modePrey, modePredator, modeModifier, 
			modeObstacle, modeLoad, modeRandom, clearButton;
	
	JFileChooser fc;
	
	PropertiesPanel properties;
	
	JMenuBar menubar;
	JMenu file, view;
	JMenuItem fileLoadTerrain, fileGenerateRandomTerrain, fileExit;
	JCheckBoxMenuItem viewGrid, viewAxes, viewMap, viewDirections, viewRadii;
	
	File terrainFile;
	
	Simulation sim;
	Canvas canv;
	
	HashSet<Element> selection;
	
	StatusBar status;
	ControlBar control;
	
	public enum Mode {SELECT, PAINT_PREY, PAINT_PREDATOR};
	
	public UserInterface(final Simulation sim) throws Exception	{
		super("Swarm AI");
		this.sim = sim;
		
		addKeyListener(this);
		
		selection = new HashSet<Element>();
		fc = new JFileChooser("./maps/");
		properties = new PropertiesPanel();
		canv = new Canvas(this);
		control = new ControlBar(sim);
		status = new StatusBar();
		
		//set up things
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
			
		initMenu();
		setJMenuBar(menubar);
		
		initToolbar();
		
		viewPort = new JPanel();
		viewPort.setLayout(new BorderLayout());
		viewPort.add(toolbar, BorderLayout.PAGE_START);
		viewPort.add(canv, BorderLayout.CENTER);
		viewPort.add(control, BorderLayout.PAGE_END);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.add(properties);
		splitPane.add(viewPort);
		
		getContentPane().add(status, BorderLayout.PAGE_END);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		/*PropertyDialog pd = new PropertyDialog(this);
		pd.targetEntity(sim.elements.get(0));*/
		
		setVisible(true);
		sim.start();
	}
	
	public void initMenu()
	{
		//FILE
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
			            log.info("Opening: " + terrainFile.getName());
			            
			        }
					status.setMode("");
				}
				else
					status.setMode("Can't change terrain while simulation is running");
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
					status.setMode("");
				}
				else
					status.setMode("Can't change terrain while simulation is running!");
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
		
		//MENU BAR
		menubar = new JMenuBar();
			file = new JMenu("File");
				file.add(fileLoadTerrain);
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
		menubar.add(file);
		menubar.add(view);
	}

	public void initToolbar()
	{
		//BUTTONS
		modeSelect = new JButton("Select");
		modeSelect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				status.setMode("Selecting");
			}
		});
		
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
		
		clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				clear();
			}
		});

		//TOOLBAR
		toolbar = new JPanel();
		toolbar.setLayout(new FlowLayout());
		getContentPane().setLayout(new BorderLayout());
			toolbar.add(modeSelect);
			toolbar.add(modePrey);
			toolbar.add(modePredator);
			toolbar.add(modeModifier);
			toolbar.add(modeObstacle);
			toolbar.add(clearButton);
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
		
		for (Element e : sim.elements)
		{
			Vec pos = e.getPosition();
			if (pos.x >= minx && pos.x <= maxx && pos.y >= miny && pos.y <= maxy)
				selection.add(e);
		}
		canv.repaint();
	}

	public void placePrey(Vec mPoint) {
		//convert mPoint to worldspace
		//place prey on closest dot
		double xloc = Math.round((mPoint.x/10))*10;
		double yloc = Math.round(mPoint.y/10)*10;
		
		boolean validLocation = true;
		
		for (Element e : sim.elements)
			if (e.getPosition().equals(new Vec(xloc, yloc)))
				validLocation = false;
		
		if (validLocation)
			sim.elements.add(new Prey(xloc,yloc,1,1,5));
		canv.repaint();
	}

	public void setPreyDirection(Vec mPoint) {
		for (Element e : selection)
		{
			e.setVelocity(mPoint.minus(e.getPosition()));
		}
 		//set all elements in selection's dir to mPoint
		
	}
	
	public void clear()
	{
		selection.clear();
		sim.elements.clear();
		sim.setTime(0);
		sim.setTotalTime(0);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// TODO Auto-generated method stub
		char eventChar = event.getKeyChar();
        int eventCode = event.getKeyCode();
        System.out.println(eventChar);
        System.out.println(eventCode);
        if (eventChar == ' ')
        {
            control.flip();
        }
	}
}

