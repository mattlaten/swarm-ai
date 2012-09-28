package frontend.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JLabel;

import math.Vec;
import backend.environment.Animal;

public class VelocityWheel extends JLabel {
	Animal animal;
	double centerScale = 1.0/8,
			pinScale = 1.0/16;
	
	public VelocityWheel (Color foreground, Animal a)	{
		super();
		setForeground(foreground);
		animal = a;
		
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me)	{
				update(me.getPoint());
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter()	{
			public void mouseDragged(MouseEvent me)	{
				update(me.getPoint());
			}
		});
	}
	
	private void update(Point mousePoint)	{
		//find the vector from the center
		//we invert the y-coord because of the difference in spaces
		Vec v = new Vec(mousePoint.x-getSize().width/2,
					getSize().height/2-mousePoint.y);
		
		//truncate the vector to fit within the outer circle
		v.x /= getSize().width/2;
		v.y /= getSize().height/2;
		animal.setVelocity(v.mult(animal.getMaxSpeed()));
		
		//now paint the changes
		repaint();
	}
	
	public void paint(Graphics g)	{
		Graphics2D g2 = (Graphics2D)g;
		
		//draw the out circle
		g2.setColor(Color.black);
		g2.fillArc(0, 0, getSize().width, getSize().height, 0, 360);
		
		//draw the velocity line
		g2.setColor(Color.green);
		Vec vel = animal.getVelocity().mult(1.0/animal.getMaxSpeed());
		vel.x *= getSize().width/2;
		vel.y *= -getSize().height/2;
		Point cent = new Point(getSize().width/2+(int)vel.x, getSize().height/2+(int)vel.y);
		g2.drawLine(getSize().width/2, getSize().height/2,
				cent.x, cent.y);
		
		//draw the pin dot thing
		Dimension pinSize = new Dimension((int)(getSize().width*pinScale), (int)(getSize().height*pinScale));
		g2.fillArc(cent.x-pinSize.width/2,
				cent.y-pinSize.height/2,
				pinSize.width,
				pinSize.height,
				0, 360);
		
		//draw the central dot thing
		g2.setColor(getForeground());
		Dimension centralSize = new Dimension((int)(getSize().width*centerScale), (int)(getSize().height*centerScale));
		g2.fillArc((getSize().width-centralSize.width)/2,
				(getSize().height-centralSize.height)/2,
				centralSize.width,
				centralSize.height,
				0, 360);
	}
}
