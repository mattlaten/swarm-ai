package frontend;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JLabel;

import math.Rect;
import math.Vec;
import backend.environment.Element;
import backend.environment.Obstacle;
import backend.environment.Predator;
import backend.environment.Prey;
import backend.environment.Waypoint;
import frontend.components.ContextMenu;

/**
 * The Canvas is used to paint Simulation on the screen. It runs an independent thread that
 * constantly pings the Simulation for new data to paint. It also takes care of the zooming
 * and panning effects. In order to paint the terrain height map, it stores a HeightMapCache
 * instance.
 */	

public class Canvas extends JLabel implements MouseListener, MouseMotionListener, MouseWheelListener, Runnable	{
	private UserInterface ui;
	private Vec origin = new Vec();	//the origin relative to the center of the Canvas
	private Vec mPoint = new Vec();	//the position of the mouse in labelSpace
	private Vec startPoint = new Vec();
	
	private ContextMenu cm;	
	private Rectangle selectRect = null;
	
	/**
	 * The distance, in pixels, between to dots on the grid when no zoom is applied
	 */
	int dotDiff = 10;
	
	/**
	 * The cached height map, used for painting the terrain efficiently
	 */
	HeightMapCache hmc = null;
	
	private double zoom = 1, defaultZoom = 1, minZoom = 0.01, maxZoom = 10,
		   trackingZoom = 0.8;
	private boolean draggingSelection = false;
	
	public boolean renderGrid = false,
			renderAxes = false,
			renderHeightMap = true,
			renderDirections = true,
			renderRadii = true,
			renderWaypoints = true,
			track = false,
			highQualityRender = true;
	
	public Canvas(UserInterface ui)	{
		this.ui = ui;
		origin = new Vec();
		hmc = new HeightMapCache(this, ui.sim.hm);
		
		cm = new ContextMenu(ui);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		new Thread(this, "Canvas").start();
	}
	
	/**
	 * Returns the position of the vector, relative to the top left corner of the
	 * Canvas, with positive Y going down (ie. Label Space).
	 * @param v The vector in world space
	 * @return The vector in label space
	 */
	public Vec toLabelSpace(Vec v)	{	return v.mult(zoom).invertY().plus(origin.mult(zoom)).plus(new Vec(getSize().width/2, getSize().height/2));	}
	
	/**
	 * Returns the position of the vector, relative to the origin of the world (ie. World Space).
	 * @param v The vector in label space
	 * @return The vector in world space
	 */
	public Vec toWorldSpace(Vec v)	{	return v.minus(originInLabelSpace()).mult(1/zoom).invertY();	}
	
	private Vec originInLabelSpace()	{	return toLabelSpace(Vec.ZERO);	}
	private  Vec mouseInWorldSpace()	{	return toWorldSpace(mPoint);	}
	
	public void run()	{
		try {
			while(true)	{
				Thread.sleep(40);
				if(ui.statusBar != null)
					ui.statusBar.setZoom(zoom);
				if(ui.properties != null)
					ui.properties.updateQuick();
				repaint();
			}
		}
		catch(InterruptedException ie)	{}
	}
	
	/**
	 * This focuses the "camera" on the currently selected Elements, following them as they
	 * move through the world.
	 */
	public void focusOnSelection()	{
		if(ui.selection.size() == 0)	return;
		//calculate bounding box of selection
		Rect r = new Rect(new Vec(Double.MAX_VALUE, -Double.MAX_VALUE),new Vec(-Double.MAX_VALUE, Double.MAX_VALUE));
		
		for(Element e: ui.selection)	{
			Vec p = e.getPosition();
			double size = e.getSize();
			r.topLeft.x = Math.min(r.topLeft.x, p.x-size);
			r.topLeft.y = Math.max(r.topLeft.y, p.y+size);
			r.botRight.x = Math.max(r.botRight.x, p.x+size);
			r.botRight.y = Math.min(r.botRight.y, p.y-size);
		}
		
		//center the origin on the centroid of the bounds
		Vec centroid = r.topLeft.minus(r.botRight).mult(0.5).plus(r.botRight);
		origin = centroid.neg().invertY();
		
		//zooming to fit everything in
		Vec diff = r.botRight.minus(r.topLeft).invertY();
		/*if(diff.x < diff.y)
			zoom = Math.max(0.1, Math.min(1, getSize().height*trackingZoom/diff.y));
		else
			zoom = Math.max(0.1, Math.min(1, getSize().width*trackingZoom/diff.x));*/
	}
	
	/**
	 * Paints the world onto the canvas, taking into account the zoom and position
	 * of the origin as well as the different rendering options as set by the UserInterface.
	 */
	public void paint(Graphics g)	{
		Graphics2D g2 = (Graphics2D)g;
		if(highQualityRender)
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		else
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		if (track)
			focusOnSelection();
		//System.out.println(hmc.completion);
		if(hmc.completion >= 1)	{
			Point o = originInLabelSpace().getPoint();
			
			g2.setColor(Color.black);
			g2.fillRect(0, 0, getSize().width, getSize().height);
			
			//calculate the drawing range
			double dotDiffZoomed = dotDiff*zoom;//Math.max(dotDiff*zoom, 2);
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
			synchronized(ui.sim.elements)	{
				for(Element e: ui.sim.elements)	{
					if(e instanceof Obstacle)
						continue;
					if (e instanceof Prey)	{
						g2.setColor(Color.blue);
						if (ui.selection.contains(e))
							g2.setColor(new Color(0.2f,0.2f,1.0f));
					}
					//if (e.getClass() == Predator.class)	{
					else if (e instanceof Predator)	{
						g2.setColor(Color.red);
						if (ui.selection.contains(e))
							g2.setColor(new Color(1.0f,0.2f,0.2f));
					}
					else if(e instanceof Waypoint)	{
						g2.setColor(Color.orange);
						if (ui.selection.contains(e))
							g2.setColor(new Color(1.0f,0.2f,0.2f));
						if(!renderWaypoints)
							continue;
						else if(e.getTarget() != null)	{	//if we're here then we know we're going to render this waypoint. so also render the path to the next waypoint
							Point epos = toLabelSpace(e.getPosition()).getPoint(),
								  tpos = toLabelSpace(e.getTarget().getPosition()).getPoint();
							g2.drawLine(epos.x, epos.y, tpos.x, tpos.y);
						}
					}
					int size = (int)(e.getSize()*zoom);
					Point pos = toLabelSpace(e.getPosition()).getPoint();
					g2.fillArc(pos.x-size, pos.y-size, size*2, size*2, 0, 360);
					
					if(renderRadii)	{
						size = (int)(e.getRadius()*zoom);
						g2.setColor(Color.red);
						g2.drawArc(pos.x-size, pos.y-size, size*2, size*2, 0, 360);
					}
					
					if(!(e instanceof Waypoint) && ui.selection.contains(e) && e.getTarget() != null)	{
						g2.setColor(Color.red);
						Point epos = toLabelSpace(e.getPosition()).getPoint(),
								  tpos = toLabelSpace(e.getTarget().getPosition()).getPoint();
						g2.drawLine(epos.x, epos.y, tpos.x, tpos.y);
					}
					
					if(renderDirections && !(e instanceof Waypoint))
						drawVector(g2, Color.green, e.getPosition(), e.getVelocity().mult(Math.min(10*zoom, 10)));
				}
			}
			
			//draw obstacles
			g2.setColor(Color.red);
			synchronized(ui.sim.elements)	{
				for(Element e : ui.sim.elements)	{
					if(e instanceof Obstacle)	{
						Waypoint s = ((Obstacle)e).start;
						LinkedList<Vec> points = new LinkedList<Vec>();
						points.add(s.getPosition());
						Waypoint cur = s.getTarget();
						while(cur != s)	{
							points.add(cur.getPosition());
							cur = cur.getTarget();
						}
						int [] xs = new int[points.size()], ys = new int[points.size()];
						for(int i = 0; i < xs.length; i++)	{
							Point p = toLabelSpace(points.remove(0)).getPoint();
							xs[i] = p.x;
							ys[i] = p.y;
						}
						g2.fillPolygon(xs, ys, xs.length);
					}
				}
			}
			
			//draw the grid
			if(renderGrid && dotDiffZoomed >= 2)	{
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
			
			if(selectRect != null)
			{
				g2.setColor(Color.gray);
				g2.draw(selectRect);
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
	
	private void drawVector(Graphics2D g2, Color c, Vec pos, Vec vec)	{
		Color old = g2.getColor();
		g2.setColor(c);
		Point posP = toLabelSpace(pos).getPoint();
		Point vecP = (toLabelSpace(pos.plus(vec))).getPoint();
		g2.drawLine(posP.x, posP.y, vecP.x, vecP.y);
		int size = (int)(zoom);
		if(size > 0)
			g2.fillArc(vecP.x - size, vecP.y - size, 2*size, 2*size, 0, 360);
		g2.setColor(old);
	}
	
	public void mousePressed(MouseEvent me)	{
		if(hmc.completion >= 1)	{
			mPoint = new Vec(me.getPoint());
			startPoint = new Vec(me.getPoint());
			
			if (ui.selection.isEmpty() && (me.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
				ui.selectPrey(toWorldSpace(mPoint), me.isControlDown());
			
			draggingSelection = false;
			if((me.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)	{
				Vec mPointWorld = toWorldSpace(mPoint);
				for(Element e : ui.selection)
					if(mPointWorld.minus(e.getPosition()).size() < e.getSize())	{
						draggingSelection = true;
						break;
					}
			}
		}
	}
	
	public void mouseDragged(MouseEvent me)	{
		if(hmc.completion >= 1)	{
			Vec current = new Vec(me.getPoint());
			if ((me.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)	{
				if(!draggingSelection)	{
					//select prey box
					int startx = (int) Math.min(startPoint.x, current.x);
					int starty = (int) Math.min(startPoint.y, current.y);
					int width = (int) Math.abs(startPoint.x - current.x);
					int height = (int) Math.abs(startPoint.y - current.y);
					selectRect = new Rectangle(startx, starty, width, height);
				}
				else	{
					//now we're dragging the selection
					for(Element e : ui.selection)
						e.setPosition(e.getPosition().minus(mPoint.minus(current).invertY().mult(1/zoom)));
					ui.sim.elements.stuffChanged();
					mPoint = current;
				}
			}
			else if ((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
				//drag canvas around
				origin = origin.minus(mPoint.minus(current).mult(1/zoom));
				mPoint = current;
			}
			repaint();
		}
	}
	
	public void mouseReleased(MouseEvent me) {
		Vec endPoint = new Vec(me.getPoint());
		if ((me.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)	{
			if(!draggingSelection)	{
				ui.selectBox(toWorldSpace(startPoint), toWorldSpace(endPoint), me.isControlDown());
				selectRect = null;
			}
		}
		/*
		else if ((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
			if (ui.selection.isEmpty())
				ui.placePrey(toWorldSpace(mPoint));
			else	
				ui.setPreyDirection(toWorldSpace(mPoint));
		else
			System.out.println("wat");
		*/
		repaint();
	}
	
	public String binary(int i)	{
		return Integer.toString(i, 2);
	}
	
	public void mouseClicked(MouseEvent me) {
		if ((me.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
			ui.selectPrey(toWorldSpace(mPoint), me.isControlDown());
		else if ((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)	{
			boolean waypointClickedOn = false;
			if(!ui.selection.isEmpty())	{
				Vec mPointWorld = toWorldSpace(mPoint);
				synchronized(ui.sim.elements)	{
					for(Element e : ui.sim.elements)
						if(e instanceof Waypoint && mPointWorld.minus(e.getPosition()).size() < e.getSize())	{
							for(Element s : ui.selection)
								s.setTarget((Waypoint)e);
							waypointClickedOn = true;
							break;
						}
				}
			}
			if (!waypointClickedOn)	{	
				try {
					switch(ui.mode)
					{
						case PAINT_PREY:
							ui.placeElement(toWorldSpace(mPoint), Prey.class);
							break;
						case PAINT_PREDATOR:
							ui.placeElement(toWorldSpace(mPoint), Predator.class);
							break;
						case SELECT:
							showPopup(me);
							break;
						case PAINT_WAYPOINT:
							//ui.placeElement(toWorldSpace(mPoint), Waypoint.class);
							ui.placeElement(new Waypoint(toWorldSpace(mPoint)));
							break;

					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void showPopup(MouseEvent e) {
        cm.position = toWorldSpace(new Vec(e.getX(), e.getY()));
		cm.show(e.getComponent(),
                       e.getX(), e.getY());
        
    }
	
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseMoved(MouseEvent me)	{
		if(hmc.completion >= 1)
			ui.statusBar.setMousePoint(toWorldSpace(new Vec(me.getPoint())));
	}
	public void mouseWheelMoved(MouseWheelEvent mwe)	{
		if(hmc.completion >= 1) {
			Vec m1 = toWorldSpace(new Vec(mwe.getPoint())); //mouse point in worldSpace before zoom
			if(true || ui.selection.size() == 0)	{
				zoom -= mwe.getWheelRotation()*(minZoom + (zoom-minZoom)/(maxZoom-minZoom));
				zoom = Math.min(Math.max(minZoom, zoom), maxZoom);	//clamp
			}
			Vec m2 = toWorldSpace(new Vec(mwe.getPoint())); //mouse point in worldSpace after zoom
			//adjust origin to ensure mouse is where it was (in the world) when the zoom began
			origin = origin.plus(m2.minus(m1).invertY());
			repaint();
		}
	}
}
