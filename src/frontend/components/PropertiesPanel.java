package frontend.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import frontend.UserInterface;

import backend.environment.Animal;
import backend.environment.Element;
import backend.environment.Prey;
import backend.environment.Property;

public class PropertiesPanel extends JPanel implements CellEditorListener  {
	
	JTable jt;
	JTextField propKey, propVal, x, y;
	JLabel heading;
	JSlider size, maxSpeed;
	PropertyTableModel model;
	VelocityWheel velWheel;
	
	public PropertiesPanel(final UserInterface ui)	{
		//heading = new JLabel("Properties");
		//heading.setFont(new Font("Arial", 1, 14));
		this.setLayout(new GridLayout(11,1));
		this.setPreferredSize(new Dimension(200,0));
		
		size = new JSlider();
		size.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce)
			{
				JSlider source = (JSlider)ce.getSource();
				if (!source.getValueIsAdjusting())
				{
					for (Element e : ui.selection)
					{
						System.out.println(source.getValue());
						e.setSize(source.getValue());
					}
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
					}
				}
			}
		});
		x = new JTextField(3);
		new JLabel("y");
		y = new JTextField(3);
		Animal a = new Prey();
		velWheel = new VelocityWheel(a);
		
		
		//this.add(heading);
		JPanel sizePan = new JPanel(new FlowLayout());
		sizePan.add(new JLabel("Size"));
		sizePan.add(size);
		
		this.add(sizePan);
		
		JPanel msPan = new JPanel(new FlowLayout());
		
		msPan.add(new JLabel("Max Speed"));
		msPan.add(maxSpeed);
		
		this.add(msPan);
		
		JPanel posPan = new JPanel(new FlowLayout());
		posPan.add(new JLabel("Position"));
		posPan.add(new JLabel("x"));
		posPan.add(x);
		posPan.add(new JLabel("y"));
		posPan.add(y);
		
		this.add(posPan);
		
		JPanel velPan = new JPanel(new BorderLayout());
		JLabel lblVel = new JLabel("Velocity");
		lblVel.setHorizontalAlignment(JLabel.CENTER);
		velPan.add(lblVel, BorderLayout.PAGE_START);
		
		JPanel velWheelPan = new JPanel(new BorderLayout());
		velWheelPan.add(velWheel);
		velPan.add(velWheelPan);
		
		this.add(velPan);
		
		/*
		model = new PropertyTableModel();
		jt = new JTable(model);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		this.add(jt, BorderLayout.CENTER);
		*/
		setVisible(true);
	}
	
	public void targetEntity(Element e) throws Exception	{
		/*String [] cols = {"Property", "Value"};
		ArrayList<Field> props = new ArrayList<Field>();
		Field[] fields = e.getClass().getFields();
		for(Field f : fields)
			//System.out.println(f.getName() + ": " + f.isAnnotationPresent(Property.class) + " " + f.getAnnotations().length);
			if(f.getAnnotation(Property.class) != null)
				props.add(f);
		model.setTarget(e, props);
		jt.repaint();*/
	}

	//@Override
	public void editingCanceled(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		System.out.print("derp");
	}

	//@Override
	public void editingStopped(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		System.out.print("herp");
	}
}
