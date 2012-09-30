package frontend.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import math.Vec;
import backend.environment.Animal;
import backend.environment.Element;
import backend.environment.Prey;
import frontend.UserInterface;

public class PropertiesPanel extends JPanel  {
	JLabel posLabel;
	JSlider size, maxSpeed, radius, collisionAvoidanceWeight,
			   velocityMatchingWeight,
			   flockCenteringWeight,
			   otherAnimalWeight,
			   waypointAttractionWeight,
			   terrainAvoidanceWeight;
	VelocityWheel velWheel;
	final UserInterface ui;
	int oldSize = 0, oldMaxSpeed = 0;
	boolean settingValues = false;
	
	public PropertiesPanel(final UserInterface ui)	{
		this.setLayout(new BorderLayout());
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		this.ui = ui;
		this.setPreferredSize(new Dimension(200,0));
		
		size = new JSlider(2, 30);
		size.setPaintTicks(true);
		size.setPaintLabels(true);
		size.setMajorTickSpacing(8);
		size.setMinorTickSpacing(4);
		oldSize = size.getValue();
		size.addChangeListener(new ChangeListener()	{
			public void stateChanged(ChangeEvent ce)	{
				int diff = size.getValue() - oldSize;
				oldSize = size.getValue();
				if(!settingValues)
					for (Element e : ui.selection)	{
						e.setSize(Math.max(size.getMinimum(), Math.min(size.getMaximum(), e.getSize() + diff)));
					}
			}
		});
		maxSpeed = new JSlider(0, 100);
		maxSpeed.setPaintTicks(true);
		maxSpeed.setPaintLabels(true);
		maxSpeed.setMajorTickSpacing(50);
		maxSpeed.setMinorTickSpacing(10);
		oldMaxSpeed = maxSpeed.getValue();
		maxSpeed.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)
			{
				double diff = (maxSpeed.getValue() - oldMaxSpeed)/100.0;
				oldMaxSpeed = maxSpeed.getValue();
				if(!settingValues)
					for (Element e : ui.selection)	{
						e.setMaxSpeed(Math.max(0,
								Math.min(1, e.getMaxSpeed() + diff)));
					}
			}
		});
		
		radius = new JSlider(10, 500);
		radius.setPaintTicks(true);
		radius.setPaintLabels(true);
		radius.setMajorTickSpacing(100);
		radius.setMinorTickSpacing(40);
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
		terrainAvoidanceWeight.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)	{
				if(!settingValues)
					for (Element e : ui.selection)
						if(e instanceof Animal)
							((Animal)e).terrainAvoidanceWeight = terrainAvoidanceWeight.getValue()/100.0;
			}
		});
		
		
		Animal a = new Prey();
		velWheel = new VelocityWheel(a);
		
		JPanel radPan = new JPanel();
		radPan.setLayout(new BoxLayout(radPan, BoxLayout.Y_AXIS));
		radPan.add(new JLabel("Radius"));
		radPan.add(radius);
		
		//this.add(heading);
		JPanel sizePan = new JPanel();
		sizePan.setLayout(new BoxLayout(sizePan, BoxLayout.Y_AXIS));
		sizePan.add(new JLabel("Size"));
		sizePan.add(size);
		
		JPanel msPan = new JPanel();
		msPan.setLayout(new BoxLayout(msPan, BoxLayout.Y_AXIS));
		msPan.add(new JLabel("Max Speed"));
		msPan.add(maxSpeed);
		
		JPanel posPan = new JPanel();
		posPan.setLayout(new BoxLayout(posPan, BoxLayout.Y_AXIS));
		posPan.add(new JLabel("Position"));
		posLabel = new JLabel("(,)");
		posPan.add(posLabel);
		
		JPanel velPan = new JPanel(new BorderLayout());
		JLabel lblVel = new JLabel("Velocity");
		lblVel.setHorizontalAlignment(JLabel.CENTER);
		velPan.add(lblVel, BorderLayout.PAGE_START);
		
		JPanel velWheelPan = new JPanel(new BorderLayout());
		velWheelPan.add(velWheel);
		velPan.add(velWheelPan);
		
		JPanel gen = new JPanel();
		gen.setLayout(new BoxLayout(gen, BoxLayout.Y_AXIS));
		gen.add(sizePan);
		gen.add(radPan);
		gen.add(msPan);
		gen.add(posPan);
		gen.add(velPan);
		
		JPanel spec = new JPanel();
		spec.setLayout(new BoxLayout(spec, BoxLayout.Y_AXIS));
		spec.add(new JLabel("Collision Avoidance"));
		spec.add(collisionAvoidanceWeight);
		spec.add(new JLabel("Flock Centering"));
		spec.add(flockCenteringWeight);
		spec.add(new JLabel("Velocity Matching"));
		spec.add(velocityMatchingWeight);
		spec.add(new JLabel("Waypoint Attraction"));
		spec.add(waypointAttractionWeight);
		spec.add(new JLabel("Terrain Slope Avoidance"));
		spec.add(terrainAvoidanceWeight);
		
		splitPane.add(gen);
		splitPane.add(spec);
		
		this.add(splitPane);
		
		setVisible(true);
	}
	
	public void update()	{
		synchronized(this)	{
			settingValues = true;
			//find the average of EVERYTHING!!!
			Vec pos = new Vec(), vel = new Vec();
			double avgSize = 0, avgMaxSpeed = 0,
					avgCA = 0, avgFC = 0, avgVM = 0, avgOA = 0, avgTA = 0, avgWA = 0;
			int count = 0, specCount = 0;
			for(Element e : ui.selection)	{
				pos = pos.plus(e.getPosition());
				vel = vel.plus(e.getVelocity().mult(1.0/e.getMaxSpeed()));
				avgSize += e.getSize();
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
			
			if(count > 0)	{
				pos = pos.mult(1.0/count);
				vel = vel.mult(1.0/count);
				avgSize /= count;
				avgMaxSpeed /= count;
				avgMaxSpeed *= 100;
				
				oldSize = (int)avgSize;
				size.setValue((int)avgSize);
				oldMaxSpeed = (int)avgMaxSpeed;
				maxSpeed.setValue((int)avgMaxSpeed);
				posLabel.setText("("+pos.x+","+pos.y+")");
			}
			else	{
				posLabel.setText("(,)");
				size.setValue(15);
				maxSpeed.setValue(50);
			}
	//		x.setEnabled(count == 1);
	//		y.setEnabled(count == 1);
			
			velWheel.setEnabled(count == 1);
			
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
			}
			else	{
				collisionAvoidanceWeight.setValue(50);
				flockCenteringWeight.setValue(50);
				velocityMatchingWeight.setValue(50);
				otherAnimalWeight.setValue(50);
				terrainAvoidanceWeight.setValue(50);
				waypointAttractionWeight.setValue(50);
			}
			settingValues = false;
		}
	}
}
