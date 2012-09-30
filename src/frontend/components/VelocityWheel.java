package frontend.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JLabel;

import frontend.UserInterface;

import math.Vec;
import backend.environment.Animal;
import backend.environment.Element;

public class VelocityWheel extends JLabel {
	LinkedList<Animal> animals;
	double centerScale = 1.0/8,
			pinScale = 1.0/16;
	final UserInterface ui;
	
	public Dimension getDim()	{
		int min = Math.min(getSize().width, getSize().height);
		return new Dimension(min, min);
	}
	
	public VelocityWheel (final UserInterface ui)	{
		super();
		this.ui = ui;
		setForeground(Color.black);
		animals = new LinkedList<Animal>();
		
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
		if(isEnabled())	{
			//find the vector from the center
			//we invert the y-coord because of the difference in spaces
			Vec v = new Vec(mousePoint.x-getSize().width/2,
						getSize().height/2-mousePoint.y);
			
			//truncate the vector to fit within the outer circle
			v.x /= getDim().width/2;
			v.y /= getDim().height/2;
			for(Animal a : animals)
				a.setVelocity(v.mult(a.getMaxSpeed()));
			
			ui.sim.elements.stuffChanged();
			
			//now paint the changes
			repaint();
		}
	}
	
	public void setAnimals(Collection<Element> animals)	{
		synchronized(this.animals)	{
			this.animals.clear();
			for(Element a : animals)
				if(a instanceof Animal)
				this.animals.add((Animal)a);
		}
		repaint();
	}
	
	public void clearAnimals()	{
		synchronized(animals)	{
			animals.clear();
		}
		repaint();
	}
	
	public void paint(Graphics g)	{
		synchronized(animals)	{
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//draw the out circle
			g2.setColor(Color.gray);
			if(isEnabled() && animals.size() > 0)
				g2.setColor(Color.black);
			g2.fillArc((getSize().width-getDim().width)/2, (getSize().height-getDim().height)/2, getDim().width, getDim().height, 0, 360);
			
			if(isEnabled() && animals.size() > 0)	{
				//draw the velocity line
				g2.setColor(Color.green);
				//get the avergae vel
				Vec vel = new Vec();//animal.getVelocity().mult(1.0/animal.getMaxSpeed());
				for(Animal a : animals)
					vel = vel.plus(a.getVelocity().mult(1.0/a.getMaxSpeed()));
				vel = vel.mult(1.0/animals.size());
				vel.x *= getDim().width/2;
				vel.y *= -getDim().height/2;
				Point cent = new Point(getSize().width/2+(int)vel.x, getSize().height/2+(int)vel.y);
				g2.drawLine(getSize().width/2, getSize().height/2,
						cent.x, cent.y);
				
				//draw the pin dot thing
				Dimension pinSize = new Dimension((int)(getDim().width*pinScale), (int)(getDim().height*pinScale));
				g2.fillArc(cent.x-pinSize.width/2,
						cent.y-pinSize.height/2,
						pinSize.width,
						pinSize.height,
						0, 360);
				
				//draw the central dot thing
				g2.setColor(getForeground());
				Dimension centralSize = new Dimension((int)(getDim().width*centerScale), (int)(getDim().height*centerScale));
				g2.fillArc((getSize().width-centralSize.width)/2,
						(getSize().height-centralSize.height)/2,
						centralSize.width,
						centralSize.height,
						0, 360);
			}
		}
	}
}
