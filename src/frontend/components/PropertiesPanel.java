package frontend.components;

import java.awt.BorderLayout;
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

import backend.environment.Element;
import backend.environment.Property;

public class PropertiesPanel extends JPanel implements CellEditorListener  {
	
	JTable jt;
	JTextField propKey, propVal, x, y;
	JLabel heading;
	JSlider size, maxSpeed;
	PropertyTableModel model;
	public PropertiesPanel()	{
		//heading = new JLabel("Properties");
		//heading.setFont(new Font("Arial", 1, 14));
		this.setLayout(new GridLayout(8,1));
		this.setPreferredSize(new Dimension(200,0));
		
		size = new JSlider();		
		maxSpeed = new JSlider();
		
		x = new JTextField(3);
		new JLabel("y");
		y = new JTextField(3);
//		y.setAlignmentY(RIGHT_ALIGNMENT);
	
		
		
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
		
		JPanel velPan = new JPanel(new FlowLayout());
		
		velPan.add(new JLabel("Velocity"));
		
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
		String [] cols = {"Property", "Value"};
		ArrayList<Field> props = new ArrayList<Field>();
		Field[] fields = e.getClass().getFields();
		for(Field f : fields)
			//System.out.println(f.getName() + ": " + f.isAnnotationPresent(Property.class) + " " + f.getAnnotations().length);
			if(f.getAnnotation(Property.class) != null)
				props.add(f);
		model.setTarget(e, props);
		jt.repaint();
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
