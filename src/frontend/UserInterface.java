package frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import util.Logger;

import math.Vec;
import backend.HeightMap;
import backend.Simulation;
import backend.environment.Element;
import backend.environment.Property;

public class UserInterface extends JFrame {
	
	Logger log = new Logger(UserInterface.class, System.out, System.err);
	JPanel toolbar;
	JButton modePrey, modePredator, modeModifier, modeObstacle, modeLoad, modeRandom;
	JFileChooser fc;
	
	File file;
	
	Simulation sim;
	Canvas canv;
	
	StatusBar status;
	
	public UserInterface(final Simulation sim) throws Exception	{
		super("Swarm AI");
		this.sim = sim;
		
		//set up things
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		canv = new Canvas(this);
		
		status = new StatusBar();
		
		fc = new JFileChooser("./maps/");
		
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
		
		modeLoad = new JButton("Load Terrain");
		modeLoad.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				status.setMode("Select Terrain");
				int returnVal = fc.showOpenDialog(UserInterface.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            file = fc.getSelectedFile();
		            sim.loadHeightMap(file);
		            canv.hmc.setHeightMap(sim.hm);
		            canv.hmc.render();
		            log.info("Opening: " + file.getName());
		            
		        }
				status.setMode("");
			}
		});
		
		modeRandom = new JButton("Random Terrain");
		modeRandom.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				status.setMode("Generating Random Terrain");
				sim.setHeightMap(new HeightMap());
		        canv.hmc.setHeightMap(sim.hm);
		        canv.hmc.render();
				status.setMode("");
			}
		});
		
		toolbar = new JPanel();
		toolbar.setLayout(new FlowLayout());
		
		getContentPane().setLayout(new BorderLayout());
		
		//add things
		toolbar.add(modePrey);
		toolbar.add(modePredator);
		toolbar.add(modeModifier);
		toolbar.add(modeObstacle);
		toolbar.add(modeLoad);
		toolbar.add(modeRandom);
		
		getContentPane().add(toolbar, BorderLayout.PAGE_START);
		getContentPane().add(status, BorderLayout.PAGE_END);
		getContentPane().add(canv, BorderLayout.CENTER);
		
		/*PropertyDialog pd = new PropertyDialog(this);
		pd.targetEntity(sim.elements.get(0));*/
		
		setVisible(true);
	}
}

class StatusBar extends JPanel	{
	JLabel mousePoint = new JLabel(),
		   mode = new JLabel();
	
	public StatusBar()	{
		super();
		setLayout(new BorderLayout());
		
		JPanel pane = new JPanel();
		//pane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		pane.add(mousePoint);
		
		add(pane, BorderLayout.LINE_END);
		add(mode, BorderLayout.LINE_START);
	}
	
	public void setMousePoint(Vec m)	{
		mousePoint.setText("x: " + (int)(m.x) + ", y: " + (int)(m.y));
	}
	
	public void setMode(String mode)	{
		this.mode.setText(mode);
	}
}

@SuppressWarnings("serial")
class PropertyTableModel extends AbstractTableModel implements TableModelListener	{
	Element target;
	ArrayList<Field> props = new ArrayList<Field>();
	
	public PropertyTableModel()	{
		addTableModelListener(this);
	}
	
	public void setTarget(Element t, ArrayList<Field> f)	{
		target = t;
		props = f;
	}
	public int getColumnCount() { return 2; }
    public int getRowCount() { return props.size();}
    public Object getValueAt(int row, int col) {
    	try {
        	if(col == 0)
        		return props.get(row).getName();
        	else
        		return props.get(row).get(target).toString();
    	}
    	catch(Exception e)	{
    		e.printStackTrace();
    	}
    	return null;
    }
    public boolean isCellEditable(int row, int col)	{
    	return col == 1;
    }
    
	public void tableChanged(TableModelEvent e) {
		fireTableRowsUpdated(e.getFirstRow(), e.getFirstRow());
		Field f = props.get(e.getFirstRow());
		System.out.println(f.getType());
	}
}


class PropertyDialog extends JDialog implements CellEditorListener {
	JTable jt;
	JTextField propKey, propVal;
	
	PropertyTableModel model;
	
	public PropertyDialog(JFrame owner)	{
		super(owner, "Properties");
		
		setSize(400,400);
		setLocationRelativeTo(owner);
		
		getContentPane().setLayout(new BorderLayout());
		
		model = new PropertyTableModel();
		jt = new JTable(model);
		
		getContentPane().add(jt, BorderLayout.CENTER);
		
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

	public void editingStopped(ChangeEvent e) {
		System.out.println("here");
	}
	public void editingCanceled(ChangeEvent e) {}
}

class HeightMapCache implements Runnable	{
	HeightMap hm;
	public double width, height;
	BufferedImage img;
	volatile double completion = 0;
	Canvas master;
	
	Thread renderThread = new Thread(this);
	
	public HeightMapCache(Canvas master, HeightMap hm)	{
		this.hm = hm;
		this.master = master;
		Rectangle2D.Double bounds = hm.getRenderBounds();
		System.out.println(bounds + " " + hm.topLeft + " " + hm.botRight);
		width = bounds.width;
		height = bounds.height;
		render();
		System.out.println("done");
	}
	
	void setHeightMap(HeightMap hm)
	{
		this.hm = hm;
	}
	
	void render()	{
		if(renderThread.isAlive())
			renderThread.interrupt();
		renderThread = new Thread(this);
		renderThread.start();
	}
	
	public BufferedImage getImage()	{
		Rectangle2D.Double bounds = hm.getRenderBounds();
		if(bounds.width != width || bounds.height != height)
			render();
		return img;
	}
	
	public void run()	{
		try	{
			//render this guy
			img = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
			double offX = (width - (hm.botRight.x - hm.topLeft.x))/2,
				   offY = (height -(hm.topLeft.y - hm.botRight.y))/2;
			int yStep = Math.max(img.getHeight()/25, 1);
			for(int y = 0; y < img.getHeight(); y++)	{
				for(int x = 0; x < img.getWidth(); x++)	{
					double height = hm.getInterpolatedHeightAt(hm.topLeft.plus(new Vec(x-offX,-y+offY)));
					int h = (int)(height*255);
					img.setRGB(x, y, new Color(h,h,h).getRGB());//(((((255 << 8) | h) << 8) | h) << 8) | h);
				}
				if(y%yStep == 0)	{
					completion = (double)y/img.getHeight();
					master.repaint();
					//Thread.sleep(10);
				}
			}
			Thread.sleep(10);
		}
		catch(InterruptedException ie)	{
			
		}
		finally	{
			completion = 1;
			master.repaint();
		}
	}
}

class Canvas extends JLabel implements MouseListener, MouseMotionListener, MouseWheelListener	{
	UserInterface ui;
	Vec origin = new Vec();	//the origin relative to the center of the Canvas
	Vec mPoint = new Vec();	//the position of the mouse in labelSpace
	
	int dotDiff = 10;
	HeightMapCache hmc = null;
	double zoom = 2;
	
	public Canvas(UserInterface ui)	{
		this.ui = ui;
		origin = new Vec();
		hmc = new HeightMapCache(this, ui.sim.hm);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}
	
	//public Vec toLabelSpace(Vec v)	{	return v.invertY().plus(origin).plus(new Vec(getSize().width/2, getSize().height/2));	}
	public Vec toLabelSpace(Vec v)	{	return v.mult(zoom).invertY().plus(origin.mult(zoom)).plus(new Vec(getSize().width/2, getSize().height/2));	}
	//public Vec toWorldSpace(Vec v)	{	return v.minus(originInLabelSpace()).invertY();	}
	public Vec toWorldSpace(Vec v)	{	return v.minus(originInLabelSpace()).mult(1/zoom).invertY();	}
	public Vec originInLabelSpace()	{	return toLabelSpace(Vec.ZERO);	}
	public Vec mouseInWorldSpace()	{	return toWorldSpace(mPoint);	}
	
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
			BufferedImage img = hmc.getImage();
			Rectangle2D.Double bounds = hmc.hm.getRenderBounds();
			Point tl = toLabelSpace(new Vec(bounds.x, bounds.y)).getPoint(),
				  br = toLabelSpace(new Vec(bounds.x+bounds.width, bounds.y-bounds.height)).getPoint();
			g2.drawImage(hmc.getImage(), (int)tl.x, (int)tl.y, (int)br.x, (int)br.y, 0, 0, (int)(hmc.width), (int)(hmc.height), null);
			/*Point clampedTl = new Point(Math.max(tl.x, 0), Math.max(tl.y, 0)),
				  clampedBr = new Point(Math.min(br.x, getSize().width), Math.min(br.y, getSize().height));
			g2.drawImage(img, clampedTl.x, clampedTl.y, clampedBr.x, clampedBr.y,
							  clampedTl.x-tl.x, clampedTl.y-tl.y, (int)hmc.width-(br.x-clampedBr.x), (int)hmc.height-(br.y-clampedBr.y), null);*/
			
			//draw elements
			g2.setColor(Color.blue);
			for(Element e: ui.sim.elements)	{
				int size = (int)(e.getSize()*zoom);
				Point pos = toLabelSpace(e.getPosition()).getPoint();
				g2.drawArc(pos.x-size/2, pos.y-size/2, size, size, 0, 360);
				size = (int)(e.getSightRadius()*zoom);
				g2.setColor(Color.red);
				g2.drawArc(pos.x-size/2, pos.y-size/2, size, size, 0, 360);
			}
			
			//draw the grid
			if(dotDiffZoomed < 5)	dotDiffZoomed *= 2;
			g2.setColor(Color.white);
			for(double y = dotYStart; y <= dotYEnd; y += dotDiffZoomed)
				for(double x = dotXStart; x <= dotXEnd; x += dotDiffZoomed)
					g2.fillRect((int)x, (int)y, 1, 1);
			
			//draw the axes
			g2.setColor(Color.white);
			g2.fillRect(0, o.y, getSize().width, 1);
			g2.fillRect(o.x, 0, 1, getSize().height);
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
	
	public void mousePressed(MouseEvent me)	{
		if(hmc.completion >= 1)
			mPoint = new Vec(me.getPoint());
	}
	
	public void mouseDragged(MouseEvent me)	{
		if(hmc.completion >= 1)	{
			Vec mp = new Vec(me.getPoint());
			origin = origin.minus(mPoint.minus(mp).mult(1/zoom));
			mPoint = mp;
			repaint();
		}
	}
	
	public void mouseReleased(MouseEvent me) {}
	public void mouseClicked(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseMoved(MouseEvent me)	{
		if(hmc.completion >= 1)
			ui.status.setMousePoint(toWorldSpace(new Vec(me.getPoint())));
	}
	public void mouseWheelMoved(MouseWheelEvent mwe)	{
		if(hmc.completion >= 1) {
			zoom -= mwe.getWheelRotation()*(0.01+ (zoom-0.1)/9.99);
			zoom = Math.min(Math.max(0.01, zoom), 10);
			repaint();
		}
	}
}
