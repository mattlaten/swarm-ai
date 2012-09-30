package frontend.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import backend.RenderObject;
import backend.environment.Animal;
import backend.environment.Element;
import backend.environment.Prey;
import frontend.UserInterface;

public class PropertiesPanel extends JPanel  {
	JTextField propKey, propVal, x, y;
	JSlider size, maxSpeed;
	VelocityWheel velWheel;
	HashMap<Element, RenderObject> selectionState = new HashMap<Element, RenderObject>();
	final UserInterface ui;
	
	public PropertiesPanel(final UserInterface ui)	{
		this.setLayout(new GridLayout(11,1));
		this.ui = ui;
		this.setPreferredSize(new Dimension(200,0));
		
		size = new JSlider(10, 200);
		size.setPaintTicks(true);
		size.setPaintLabels(true);
		size.setMajorTickSpacing(10);
		size.setMinorTickSpacing(2);
		size.setSnapToTicks(true);
		size.addChangeListener(new ChangeListener()	{
			public void stateChanged(ChangeEvent ce)
			{
				JSlider source = (JSlider)ce.getSource();
				//if (!source.getValueIsAdjusting())	{
					for (Element e : ui.selection)
					{	
						double s = e.getSize();
						e.setSize(s*source.getValue()/50.0);
					}
			}
		});
		maxSpeed = new JSlider();
		maxSpeed.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)
			{
				JSlider source = (JSlider)ce.getSource();
				if (!source.getValueIsAdjusting())
				{
					for (Element e : ui.selection)
					{
						System.out.println(source.getValue());
						e.setMaxSpeed(source.getValue());
						System.out.println(e.getMaxSpeed());
					}
				}
			}
		});
		x = new JTextField(3);
		new JLabel("y");
		y = new JTextField(3);
		Animal a = new Prey();
		velWheel = new VelocityWheel(a);
		
		
		
		/*
		 * Construction of the Split Pane from Props and Influences
		 */		
		JPanel props = new JPanel(new GridLayout(4,1));
		JPanel sizePan = new JPanel(new FlowLayout());
		sizePan.add(new JLabel("Size"));
		sizePan.add(size);
		
		props.add(sizePan);
		
		JPanel msPan = new JPanel(new FlowLayout());
		msPan.add(new JLabel("Speed"));
		msPan.add(maxSpeed);
		
		props.add(msPan);
		
		JPanel posPan = new JPanel(new FlowLayout());
		posPan.add(new JLabel("Position"));
		posPan.add(new JLabel("x"));
		posPan.add(x);
		posPan.add(new JLabel("y"));
		posPan.add(y);
		
		props.add(posPan);
		
		JPanel velPan = new JPanel(new BorderLayout());
		JLabel lblVel = new JLabel("Velocity");
		lblVel.setHorizontalAlignment(JLabel.CENTER);
		velPan.add(lblVel, BorderLayout.PAGE_START);
		
		JPanel velWheelPan = new JPanel(new BorderLayout());
		velWheelPan.add(velWheel);
		velPan.add(velWheelPan);
		
		props.add(velPan);
		
		props.setBorder(new TitledBorder("Selection Properties"));
		
		JPanel parameters = new JPanel();
		parameters.setBorder(new TitledBorder("Simulation Parameters"));	
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, props, parameters);
		this.add(splitPane);
		setVisible(true);
	}
	
	public void update()	{
		selectionState.clear();
		for(Element e : ui.selection)
			selectionState.put(e, new RenderObject(e));
		
	}
}
