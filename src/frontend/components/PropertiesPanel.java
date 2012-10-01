package frontend.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import math.Vec;
import backend.environment.Animal;
import backend.environment.Element;
import frontend.UserInterface;

/**
 * The PropertiesPanel allows the user to manipulate settings about the
 * currently selected elements in the world
 */
public class PropertiesPanel extends JPanel implements ChangeListener  {
	private DecimalFormat posFormat = new DecimalFormat("0.00");
	private JLabel posLabel;
	private JSlider size, maxSpeed, radius, collisionAvoidanceWeight,
			   velocityMatchingWeight,
			   flockCenteringWeight,
			   otherAnimalWeight,
			   waypointAttractionWeight,
			   terrainAvoidanceWeight;
	private VelocityWheel velWheel;
	private final UserInterface ui;
	private int oldSize = 0, oldMaxSpeed = 0;
	private boolean settingValues = false;
	
	/**
	 * The Contructor
	 * @param ui The UserInterface this Panel is connected to
	 */
	public PropertiesPanel(final UserInterface ui)	{
		this.setLayout(new BorderLayout());
		//JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		this.ui = ui;
		this.setPreferredSize(new Dimension(250,0));
		
		size = new JSlider(2, 30);
		size.setPaintTicks(true);
		size.setPaintLabels(true);
		size.setMajorTickSpacing(8);
		size.setMinorTickSpacing(4);
		oldSize = size.getValue();
		size.addChangeListener(this);
		size.addChangeListener(new ChangeListener()	{
			public void stateChanged(ChangeEvent ce)	{
				int diff = size.getValue() - oldSize;
				oldSize = size.getValue();
				if(!settingValues)
					for (Element e : ui.selection)	{
						//e.setSize(Math.max(size.getMinimum(), Math.min(size.getMaximum(), e.getSize() + diff)));
						e.setSize(size.getValue());
					}
			}
		});
		maxSpeed = new JSlider(1, 20);
		maxSpeed.setPaintTicks(true);
		maxSpeed.setPaintLabels(true);
		maxSpeed.setMajorTickSpacing(9);
		maxSpeed.setMinorTickSpacing(2);
		oldMaxSpeed = maxSpeed.getValue();
		maxSpeed.addChangeListener(this);
		maxSpeed.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)	{
				double diff = (maxSpeed.getValue() - oldMaxSpeed)/2.0;
				oldMaxSpeed = maxSpeed.getValue();
				if(!settingValues)
					for (Element e : ui.selection)	{
						//e.setMaxSpeed(Math.max(0.5, Math.min(10, e.getMaxSpeed() + diff)));
						e.setMaxSpeed(maxSpeed.getValue()/2.0);
					}
			}
		});
		
		radius = new JSlider(10, 500);
		radius.setPaintTicks(true);
		radius.setPaintLabels(true);
		radius.setMajorTickSpacing(100);
		radius.setMinorTickSpacing(40);
		radius.addChangeListener(this);
		radius.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)	{
				if(!settingValues)
					for (Element e : ui.selection)
						e.setRadius(radius.getValue());
			}
		});
		
		collisionAvoidanceWeight = new JSlider(0, 100);
		collisionAvoidanceWeight.setPaintTicks(true);
		collisionAvoidanceWeight.setPaintLabels(true);
		collisionAvoidanceWeight.setMajorTickSpacing(50);
		collisionAvoidanceWeight.setMinorTickSpacing(10);
		collisionAvoidanceWeight.addChangeListener(this);
		collisionAvoidanceWeight.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)	{
				if(!settingValues)
					for (Element e : ui.selection)
						if(e instanceof Animal)
							((Animal)e).collisionAvoidanceWeight = collisionAvoidanceWeight.getValue()/100.0;
			}
		});
						
		velocityMatchingWeight = new JSlider(0, 100);
		velocityMatchingWeight.setPaintTicks(true);
		velocityMatchingWeight.setPaintLabels(true);
		velocityMatchingWeight.setMajorTickSpacing(50);
		velocityMatchingWeight.setMinorTickSpacing(10);
		velocityMatchingWeight.addChangeListener(this);
		velocityMatchingWeight.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)	{
				if(!settingValues)
					for (Element e : ui.selection)
						if(e instanceof Animal)
							((Animal)e).velocityMatchingWeight = velocityMatchingWeight.getValue()/100.0;
			}
		});
		
		flockCenteringWeight = new JSlider(0, 100);
		flockCenteringWeight.setPaintTicks(true);
		flockCenteringWeight.setPaintLabels(true);
		flockCenteringWeight.setMajorTickSpacing(50);
		flockCenteringWeight.setMinorTickSpacing(10);
		flockCenteringWeight.addChangeListener(this);
		flockCenteringWeight.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)	{
				if(!settingValues)
					for (Element e : ui.selection)
						if(e instanceof Animal)
							((Animal)e).flockCenteringWeight = flockCenteringWeight.getValue()/100.0;
			}
		});
		
		otherAnimalWeight = new JSlider(0, 100);
		otherAnimalWeight.setPaintTicks(true);
		otherAnimalWeight.setPaintLabels(true);
		otherAnimalWeight.setMajorTickSpacing(50);
		otherAnimalWeight.setMinorTickSpacing(10);
		otherAnimalWeight.addChangeListener(this);
		otherAnimalWeight.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)	{
				if(!settingValues)
					for (Element e : ui.selection)
						if(e instanceof Animal)
							((Animal)e).otherAnimalWeight = otherAnimalWeight.getValue()/100.0;
			}
		});
		
		waypointAttractionWeight = new JSlider(0, 100);
		waypointAttractionWeight.setPaintTicks(true);
		waypointAttractionWeight.setPaintLabels(true);
		waypointAttractionWeight.setMajorTickSpacing(50);
		waypointAttractionWeight.setMinorTickSpacing(10);
		waypointAttractionWeight.addChangeListener(this);
		waypointAttractionWeight.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)	{
				if(!settingValues)
					for (Element e : ui.selection)
						if(e instanceof Animal)
							((Animal)e).waypointAttractionWeight = waypointAttractionWeight.getValue()/100.0;
			}
		});
		terrainAvoidanceWeight = new JSlider(0, 100);
		terrainAvoidanceWeight.setPaintTicks(true);
		terrainAvoidanceWeight.setPaintLabels(true);
		terrainAvoidanceWeight.setMajorTickSpacing(50);
		terrainAvoidanceWeight.setMinorTickSpacing(10);
		terrainAvoidanceWeight.addChangeListener(this);
		terrainAvoidanceWeight.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)	{
				if(!settingValues)
					for (Element e : ui.selection)
						if(e instanceof Animal)
							((Animal)e).terrainAvoidanceWeight = terrainAvoidanceWeight.getValue()/100.0;
			}
		});
		
		velWheel = new VelocityWheel(ui);
		
		JPanel radPan = new JPanel();
		radPan.setLayout(new BoxLayout(radPan, BoxLayout.PAGE_AXIS));
		radPan.add(newCenterJLabel("Radius"));
		radPan.add(radius);
		
		//this.add(heading);
		JPanel sizePan = new JPanel();
		sizePan.setLayout(new BoxLayout(sizePan, BoxLayout.PAGE_AXIS));
		sizePan.add(newCenterJLabel("Size"));
		sizePan.add(size);
		
		JPanel msPan = new JPanel();
		msPan.setLayout(new BoxLayout(msPan, BoxLayout.PAGE_AXIS));
		msPan.add(newCenterJLabel("Max Speed"));
		msPan.add(maxSpeed);
		
		JPanel posPan = new JPanel();
		posPan.setLayout(new BoxLayout(posPan, BoxLayout.PAGE_AXIS));
		posPan.add(newCenterJLabel("Position"));
		posLabel = newCenterJLabel("(,)");
		posPan.add(posLabel);
		
		JPanel velPan = new JPanel();
		velPan.setLayout(new BorderLayout());
		velPan.add(newCenterJLabel("Velocity"), BorderLayout.PAGE_START);
		velWheel.setPreferredSize(new Dimension(100, 100));
		velPan.add(velWheel, BorderLayout.CENTER);
		
		JPanel gen = new JPanel();
		gen.setLayout(new BoxLayout(gen, BoxLayout.PAGE_AXIS));
		
		JPanel stats = new JPanel();
		stats.setLayout(new BoxLayout(stats, BoxLayout.PAGE_AXIS));
		stats.setBorder(new TitledBorder("General"));
		stats.add(sizePan);
		stats.add(radPan);
		stats.add(msPan);
		stats.add(posPan);
		stats.add(velPan);
		
		JPanel behave = new JPanel();
		behave.setLayout(new BoxLayout(behave, BoxLayout.PAGE_AXIS));
		behave.setBorder(new TitledBorder("Behaviour"));
		behave.add(newCenterJLabel("Collision Avoidance"));
		behave.add(collisionAvoidanceWeight);
		behave.add(newCenterJLabel("Flock Centering"));
		behave.add(flockCenteringWeight);
		behave.add(newCenterJLabel("Velocity Matching"));
		behave.add(velocityMatchingWeight);
		behave.add(newCenterJLabel("Chase/Avoid Prey/Predator"));
		behave.add(otherAnimalWeight);
		behave.add(newCenterJLabel("Waypoint Attraction"));
		behave.add(waypointAttractionWeight);
		behave.add(newCenterJLabel("Terrain Slope Avoidance"));
		behave.add(terrainAvoidanceWeight);
		
		//gen.setPreferredSize(new Dimension(150, 0));
		gen.add(stats);
		gen.add(behave);
		
		JScrollPane genScroll = new JScrollPane(gen);
		genScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		this.add(genScroll);
		setVisible(true);
		
		update();
	}
	
	private JLabel newCenterJLabel(String text)	{
		JLabel l = new JLabel(text, JLabel.CENTER);
		l.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		return l;
	}
	
	/**
	 * A quick version of the update() method
	 */
	public void updateQuick()	{
		/*synchronized(this)	{
			Vec pos = new Vec();
			int count = 0;
			for(Element e : ui.selection)	{
				pos = pos.plus(e.getPosition());
				count ++;
			}
			if(count > 0)	{
				pos = pos.mult(1.0/count);
				posLabel.setText("("+posFormat.format(pos.x)+","+posFormat.format(pos.y)+")");
			}
			else
				posLabel.setText("(,)");
			
			velWheel.repaint();
		}*/
		update();
	}
	
	/**
	 * Updates the panel to represent the selection
	 */
	public void update()	{
		synchronized(this)	{
			settingValues = true;
			//find the average of EVERYTHING!!!
			Vec pos = new Vec();
			double avgSize = 0, avgMaxSpeed = 0, avgRadius = 0,
					avgCA = 0, avgFC = 0, avgVM = 0, avgOA = 0, avgTA = 0, avgWA = 0;
			int count = 0, specCount = 0;
			for(Element e : ui.selection)	{
				pos = pos.plus(e.getPosition());
				avgSize += e.getSize();
				avgRadius += e.getRadius();
				avgMaxSpeed += e.getMaxSpeed();
				if(e instanceof Animal)	{
					specCount ++;
					Animal a = (Animal)e;
					avgCA += a.collisionAvoidanceWeight;
					avgFC += a.flockCenteringWeight;
					avgVM += a.velocityMatchingWeight;
					avgOA += a.otherAnimalWeight;
					avgWA += a.waypointAttractionWeight;
					avgTA += a.terrainAvoidanceWeight;
				}
				count ++;
			}
			
			size.setEnabled(count > 0);
			maxSpeed.setEnabled(count > 0);
			radius.setEnabled(count > 0);
			
			if(count > 0)	{
				pos = pos.mult(1.0/count);
				avgSize /= count;
				avgMaxSpeed /= count;
				avgMaxSpeed *= 2;
				avgRadius /= count;
				
				oldSize = (int)avgSize;
				size.setValue((int)avgSize);
				oldMaxSpeed = (int)avgMaxSpeed;
				maxSpeed.setValue((int)avgMaxSpeed);
				radius.setValue((int)avgRadius);
				posLabel.setText("("+posFormat.format(pos.x)+","+posFormat.format(pos.y)+")");
			}
			else	{
				posLabel.setText("(,)");
				size.setValue(15);
				maxSpeed.setValue(1);
				radius.setValue(100);
			}
	//		x.setEnabled(count == 1);
	//		y.setEnabled(count == 1);
			
			//velWheel.setEnabled(count == 1);
			
			collisionAvoidanceWeight.setEnabled(specCount > 0);
			flockCenteringWeight.setEnabled(specCount > 0);
			velocityMatchingWeight.setEnabled(specCount > 0);
			otherAnimalWeight.setEnabled(specCount > 0);
			terrainAvoidanceWeight.setEnabled(specCount > 0);
			waypointAttractionWeight.setEnabled(specCount > 0);
			if(specCount > 0)	{
				collisionAvoidanceWeight.setValue((int)(avgCA*100/specCount));
				flockCenteringWeight.setValue((int)(avgFC*100/specCount));
				velocityMatchingWeight.setValue((int)(avgVM*100/specCount));
				otherAnimalWeight.setValue((int)(avgOA*100/specCount));
				terrainAvoidanceWeight.setValue((int)(avgTA*100/specCount));
				waypointAttractionWeight.setValue((int)(avgWA*100/specCount));
				velWheel.setAnimals(ui.selection);
			}
			else	{
				collisionAvoidanceWeight.setValue(50);
				flockCenteringWeight.setValue(50);
				velocityMatchingWeight.setValue(50);
				otherAnimalWeight.setValue(50);
				terrainAvoidanceWeight.setValue(50);
				waypointAttractionWeight.setValue(50);
				velWheel.clearAnimals();
			}
			settingValues = false;
		}
	}

	/**
	 * Allows us to dirty the simulation's element list
	 */
	public void stateChanged(ChangeEvent arg0) {
		if(!settingValues)
			ui.sim.elements.stuffChanged();
	}
}
