package frontend;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import backend.environment.Element;

import math.Vec;

class Canvas extends JLabel implements MouseListener, MouseMotionListener, MouseWheelListener, Runnable	{
	UserInterface ui;
	Vec origin = new Vec();	//the origin relative to the center of the Canvas
	Vec mPoint = new Vec();	//the position of the mouse in labelSpace
	Vec startPoint = new Vec();
	
	Rectangle selection = null;
	
	int dotDiff = 10;
	HeightMapCache hmc = null;
	double zoom = 1;
	
	public boolean renderGrid = true,
			renderAxes = true,
			renderHeightMap = false;
	
	public Canvas(UserInterface ui)	{
		this.ui = ui;
		origin = new Vec();
		hmc = new HeightMapCache(this, ui.sim.hm);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		new Thread(this).start();
	}
	
	//public Vec toLabelSpace(Vec v)	{	return v.invertY().plus(origin).plus(new Vec(getSize().width/2, getSize().height/2));	}
	public Vec toLabelSpace(Vec v)	{	return v.mult(zoom).invertY().plus(origin.mult(zoom)).plus(new Vec(getSize().width/2, getSize().height/2));	}
	//public Vec toWorldSpace(Vec v)	{	return v.minus(originInLabelSpace()).invertY();	}
	public Vec toWorldSpace(Vec v)	{	return v.minus(originInLabelSpace()).mult(1/zoom).invertY();	}
	public Vec originInLabelSpace()	{	return toLabelSpace(Vec.ZERO);	}
	public Vec mouseInWorldSpace()	{	return toWorldSpace(mPoint);	}
	
	public void run()	{
		try {
			while(true)	{
				Thread.sleep(40);
				repaint();
			}
		}
		catch(InterruptedException ie)	{}
	}
	
	public void paint(Graphics g)	{
		Graphics2D g2 = (Graphics2D)g;
		//System.out.println(hmc.completion);
		if(hmc.completion >= 1)	{
			Point o = originInLabelSpace().getPoint();
			
			g2.setColor(Color.black);
			g2.fillRect(0, 0, getSize().width, getSize().height);
			
			//calculate the drawing range
			double dotDiffZoomed = Math.max(dotDiff*zoom, 2);
			double dotXStart = (o.x - dotDiffZoomed*Math.floor(o.x/dotDiffZoomed) - dotDiffZoomed),
				dotYStart = (o.y - dotDiffZoomed*Math.floor(o.y/dotDiffZoomed) - dotDiffZoomed),
				dotXEnd   = (o.x + dotDiffZoomed*Math.floor((getSize().width-o.x)/dotDiffZoomed) + 2*dotDiffZoomed),
				dotYEnd   = (o.y + dotDiffZoomed*Math.floor((getSize().height-o.y)/dotDiffZoomed) + 2*dotDiffZoomed);
			
			//render the heightMap
			if (renderHeightMap)
			{
				BufferedImage img = hmc.getImage();
				Rectangle2D.Double bounds = hmc.hm.getRenderBounds();
				Point tl = toLabelSpace(new Vec(bounds.x, bounds.y)).getPoint(),
					  br = toLabelSpace(new Vec(bounds.x+bounds.width, bounds.y-bounds.height)).getPoint();
				g2.drawImage(hmc.getImage(), (int)tl.x, (int)tl.y, (int)br.x, (int)br.y, 0, 0, (int)(hmc.width), (int)(hmc.height), null);
				/*Point clampedTl = new Point(Math.max(tl.x, 0), Math.max(tl.y, 0)),
					  clampedBr = new Point(Math.min(br.x, getSize().width), Math.min(br.y, getSize().height));
				g2.drawImage(img, clampedTl.x, clampedTl.y, clampedBr.x, clampedBr.y,
								  clampedTl.x-tl.x, clampedTl.y-tl.y, (int)hmc.width-(br.x-clampedBr.x), (int)hmc.height-(br.y-clampedBr.y), null);*/
			}	
			//draw elements
			for(Element e: ui.sim.elements)	{
				g2.setColor(Color.blue);
				if (ui.selection.contains(e))
					g2.setColor(Color.green);	
				int size = (int)(e.getSize()*zoom);
				Point pos = toLabelSpace(e.getPosition()).getPoint();
				g2.fillArc(pos.x-size, pos.y-size, size*2, size*2, 0, 360);
				
				size = (int)(e.getRadius()*zoom);
				g2.setColor(Color.red);
				g2.drawArc(pos.x-size, pos.y-size, size*2, size*2, 0, 360);
				
				drawVector(g2, Color.green, e.getPosition(), e.getVelocity().mult(50));
			}
			
			//draw the grid
			if(renderGrid)	{
				if(dotDiffZoomed < 5)	dotDiffZoomed *= 2;
				g2.setColor(Color.white);
				for(double y = dotYStart; y <= dotYEnd; y += dotDiffZoomed)
					for(double x = dotXStart; x <= dotXEnd; x += dotDiffZoomed)
						g2.fillRect((int)x, (int)y, 1, 1);
			}
			
			//draw the axes
			if(renderAxes)	{
				g2.setColor(Color.white);
				g2.fillRect(0, o.y, getSize().width, 1);
				g2.fillRect(o.x, 0, 1, getSize().height);
			}
			
			if(selection != null)
			{
				g2.setColor(Color.gray);
				g2.draw(selection);
			}
			
		}
		else	{
			int width = getSize().width/3,
				height = 50;
			int x = (getSize().width-width)/2,
				y = (getSize().height-height)/2;
			
			g2.setColor(Color.green);
			g2.fillRect(x,y,(int)(width*hmc.completion),height);
			
			g2.setColor(Color.black);
			g2.drawRect(x,y,width,height);
		}
	}
	
	public void drawVector(Graphics2D g2, Color c, Vec pos, Vec vec)	{
		Color old = g2.getColor();
		g2.setColor(c);
		Point posP = toLabelSpace(pos).getPoint();
		Point vecP = (toLabelSpace(pos.plus(vec))).getPoint();
		g2.drawLine(posP.x, posP.y, vecP.x, vecP.y);
		g2.fillArc(vecP.x - 2, vecP.y - 2, 4, 4, 0, 360);
		g2.setColor(old);
	}
	
	public void mousePressed(MouseEvent me)	{
		if(hmc.completion >= 1)
		{
			mPoint = new Vec(me.getPoint());
			startPoint = new Vec(me.getPoint());
		}
	}
	
	public void mouseDragged(MouseEvent me)	{
		if(hmc.completion >= 1)	{
			switch(me.getModifiers()) {
			case InputEvent.BUTTON1_MASK: {
				//select prey box
				Vec current = new Vec(me.getPoint());
				int startx = (int) Math.min(startPoint.x, current.x);
				int starty = (int) Math.min(startPoint.y, current.y);
				int width = (int) Math.abs(startPoint.x - current.x);
				int height = (int) Math.abs(startPoint.y - current.y);
				selection = new Rectangle(startx, starty, width, height);
				repaint();
				break;
			}
			case InputEvent.BUTTON2_MASK: {    
				break;
			}
			case InputEvent.BUTTON3_MASK: {   
				//drag canvas around
				Vec mp = new Vec(me.getPoint());
				origin = origin.minus(mPoint.minus(mp).mult(1/zoom));
				mPoint = mp;
				repaint();
				break;
			}
			}
		}
	}
	
	public void mouseReleased(MouseEvent me) {
		switch(me.getModifiers()) {
		case InputEvent.BUTTON1_MASK: {
			//select prey box
			Vec endPoint = new Vec(me.getPoint());
			ui.selectBox(toWorldSpace(startPoint), toWorldSpace(endPoint));
			selection = null;
			repaint();
			break;
		}
		case InputEvent.BUTTON2_MASK: {   
			break;
		}
		case InputEvent.BUTTON3_MASK: {
			break;
		}
		}
	}
	
	public String binary(int i)	{
		return Integer.toString(i, 2);
	}
	
	public void mouseClicked(MouseEvent me) {
		if ((me.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
			if (me.isControlDown())
				ui.selectPrey(toWorldSpace(mPoint), true);	
			else
				ui.selectPrey(toWorldSpace(mPoint), false);
		else if ((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
			if (ui.selection.isEmpty())
				ui.placePrey(toWorldSpace(mPoint));
			else	
				ui.setPreyDirection(toWorldSpace(mPoint));
		else
			System.out.println("wat");
	}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseMoved(MouseEvent me)	{
		if(hmc.completion >= 1)
			ui.status.setMousePoint(toWorldSpace(new Vec(me.getPoint())));
	}
	public void mouseWheelMoved(MouseWheelEvent mwe)	{
		if(hmc.completion >= 1) {
			Vec m1 = toWorldSpace(new Vec(mwe.getPoint())); //mouse point in worldSpace before zoom
			zoom -= mwe.getWheelRotation()*(0.01+ (zoom-0.1)/9.99);
			zoom = Math.min(Math.max(0.01, zoom), 10);
			Vec m2 = toWorldSpace(new Vec(mwe.getPoint())); //mouse point in worldSpace after zoom
			origin = origin.plus(m2.minus(m1).invertY());
			repaint();
		}
	}
}
