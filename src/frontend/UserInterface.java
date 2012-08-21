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
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import math.Vec;
import backend.HeightMap;
import backend.Simulation;
import backend.environment.Element;
import backend.environment.Property;

public class UserInterface extends JFrame {
	JPanel toolbar;
	JButton modePrey, modePredator, modeModifier, modeObstacle;
	
	Simulation sim;
	Canvas canv;
	
	StatusBar status;
	
	public UserInterface(Simulation sim) throws Exception	{
		super("Swarm AI");
		this.sim = sim;
		
		//set up things
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		canv = new Canvas(this);
		
		status = new StatusBar();
		
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
		
		toolbar = new JPanel();
		toolbar.setLayout(new FlowLayout());
		
		getContentPane().setLayout(new BorderLayout());
		
		//add things
		toolbar.add(modePrey);
		toolbar.add(modePredator);
		toolbar.add(modeModifier);
		toolbar.add(modeObstacle);
		
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
		mousePoint.setText("x: " + m.x + ", y: " + m.y);
	}
	
	public void setMode(String mode)	{
		this.mode.setText(mode);
	}
}


class PropertyDialog extends JDialog {
	JTable jt;
	JTextField propKey, propVal;
	
	public PropertyDialog(JFrame owner)	{
		super(owner, "Properties");
		
		setSize(400,400);
		setLocationRelativeTo(owner);
		
		getContentPane().setLayout(new BorderLayout());
		
		jt = new JTable(new DefaultTableModel());
		
		getContentPane().add(jt, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public void targetEntity(Element e) throws Exception	{
		String [] cols = {"Property", "Value"};
		ArrayList<Field> props = new ArrayList<Field>();
		Field[] fields = e.getClass().getFields();
		for(Field f : fields)	{
			System.out.println(f.getName() + ": " + f.isAnnotationPresent(Property.class) + " " + f.getAnnotations().length);
			if(f.getAnnotation(Property.class) != null)
				props.add(f);
		}
		String [][] rows = new String[props.size()][2];
		for(int i = 0; i < props.size(); i++)	{
			rows[i][0] = props.get(0).getName();
			rows[i][1] = props.get(0).get(e).toString();
			System.out.println(rows[i][0] + " " + rows[i][1]);
		}
		((DefaultTableModel)jt.getModel()).setDataVector(rows, cols);
		jt.repaint();
	}
}

class HeightMapCache	{
	HeightMap hm;
	public double width, height;
	BufferedImage img;
	
	public HeightMapCache(HeightMap hm)	{
		this.hm = hm;
		Rectangle2D.Double bounds = hm.getRenderBounds();
		System.out.println(bounds + " " + hm.topLeft + " " + hm.botRight);
		width = bounds.width;
		height = bounds.height;
		render();
	}
	
	private void render()	{
		//render this guy
		img = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
		double offX = (width - (hm.botRight.x - hm.topLeft.x))/2,
			   offY = (height -(hm.topLeft.y - hm.botRight.y))/2;
		for(int y = 0; y < img.getHeight(); y++)
			for(int x = 0; x < img.getWidth(); x++)	{
				double height = hm.getInterpolatedHeightAt(hm.topLeft.plus(new Vec(x-offX,-y+offY)));
				int h = (int)(height*255);
				img.setRGB(x, y, new Color(h,h,h).getRGB());//(((((255 << 8) | h) << 8) | h) << 8) | h);
			}
	}
	
	public BufferedImage getImage()	{
		Rectangle2D.Double bounds = hm.getRenderBounds();
		if(bounds.width != width || bounds.height != height)
			render();
		return img;
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
		hmc = new HeightMapCache(ui.sim.hm);
		
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
		
		Point o = originInLabelSpace().getPoint();
		
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		
		//draw the dots
		double dotDiffZoomed = Math.max(dotDiff*zoom, 2);
		//g2.setColor(Color.black);
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
		Point clampedTl = new Point(Math.max(tl.x, 0), Math.max(tl.y, 0)),
			  clampedBr = new Point(Math.min(br.x, getSize().width), Math.min(br.y, getSize().height));
		/*g2.drawImage(img, clampedTl.x, clampedTl.y, clampedBr.x, clampedBr.y,
						  clampedTl.x-tl.x, clampedTl.y-tl.y, (int)hmc.width-(br.x-clampedBr.x), (int)hmc.height-(br.y-clampedBr.y), null);*/
		
		if(dotDiffZoomed < 5)
			dotDiffZoomed *= 2;
		g2.setColor(Color.white);
		for(double y = dotYStart; y <= dotYEnd; y += dotDiffZoomed)
			for(double x = dotXStart; x <= dotXEnd; x += dotDiffZoomed)
				g2.fillRect((int)x, (int)y, 1, 1);
		
		//draw the axes
		g2.setColor(Color.white);
		g2.fillRect(0, o.y, getSize().width, 1);
		g2.fillRect(o.x, 0, 1, getSize().height);
		
		/*Vec mPointSpace = mPoint.minus(origin);
		g2.drawString((int)(mPointSpace.x) + ", " + (int)(mPointSpace.y), (int)(mPoint.x), (int)(mPoint.y));*/
	}
	
	public void mousePressed(MouseEvent me)	{
		mPoint = new Vec(me.getPoint());
	}

	public void mouseReleased(MouseEvent me) {}
	
	public void mouseDragged(MouseEvent me)	{
		Vec mp = new Vec(me.getPoint());
		origin = origin.minus(mPoint.minus(mp).mult(1/zoom));
		mPoint = mp;
		repaint();
	}
	
	public void mouseClicked(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseMoved(MouseEvent me)	{
		ui.status.setMousePoint(toWorldSpace(new Vec(me.getPoint())));
	}
	public void mouseWheelMoved(MouseWheelEvent mwe)	{
		zoom -= mwe.getWheelRotation()*(0.01+ (zoom-0.1)/10.01);
		zoom = Math.min(Math.max(0.1, zoom), 10);
		repaint();
	}
}