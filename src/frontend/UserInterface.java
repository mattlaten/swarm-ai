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
		Vec v = hm.botRight.minus(hm.topLeft);
		width = v.x;
		height = Math.abs(v.y);
		render();
	}
	
	private void render()	{
		//render this guy
		img = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
		for(int y = 0; y < img.getHeight(); y++)
			for(int x = 0; x < img.getWidth(); x++)	{
				double height = hm.getInterpolatedHeightAt(hm.topLeft.plus(new Vec(x,-y)));
				int h = (int)(height*255);
				img.setRGB(x, y, new Color(h,h,h).getRGB());//(((((255 << 8) | h) << 8) | h) << 8) | h);
			}
	}
	
	public BufferedImage getImage()	{
		Vec v = hm.botRight.minus(hm.topLeft);
		if(v.x != width || Math.abs(v.y) != height)
			render();
		return img;
	}
}

class Canvas extends JLabel implements MouseListener, MouseMotionListener	{
	UserInterface ui;
	Vec origin = new Vec();	//the origin relative to the center of the Canvas
	Vec mPoint = new Vec();	//the position of the mouse in labelSpace
	
	int dotDiff = 10;
	
	HeightMapCache hmc = null;
	
	public Canvas(UserInterface ui)	{
		this.ui = ui;
		origin = new Vec();
		hmc = new HeightMapCache(ui.sim.hm);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public Vec toLabelSpace(Vec v)	{	return v.invertY().plus(origin).plus(new Vec(getSize().width/2, getSize().height/2));	}
	public Vec toWorldSpace(Vec v)	{	return v.minus(originInLabelSpace()).invertY();	}
	public Vec originInLabelSpace()	{	return toLabelSpace(Vec.ZERO);	}
	public Vec mouseInWorldSpace()	{	return toWorldSpace(mPoint);	}
	
	public void paint(Graphics g)	{
		Graphics2D g2 = (Graphics2D)g;
		
		Point o = originInLabelSpace().getPoint();
		
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		
		//draw the dots
		//g2.setColor(Color.black);
		int dotXStart = o.x - dotDiff*(o.x/dotDiff) - dotDiff,
			dotYStart = o.y - dotDiff*(o.y/dotDiff) - dotDiff,
			dotXEnd   = o.x + dotDiff*((getSize().width-o.x)/dotDiff) + 2*dotDiff,
			dotYEnd   = o.y + dotDiff*((getSize().height-o.y)/dotDiff) + 2*dotDiff;
		
		//render the heightMap
		/*for(int y = dotYStart; y <= dotYEnd; y ++)
			for(int x = dotXStart; x <= dotXEnd; x ++)	{
				double h = ui.sim.hm.getInterpolatedHeightAt(toWorldSpace(new Vec(x,y)));
				g2.setColor(new Color((float)h,(float)h,(float)h));
				g2.fillRect(x,y,1,1);
			}*/
		Point tl = toLabelSpace(hmc.hm.topLeft).getPoint(), br = toLabelSpace(hmc.hm.botRight).getPoint();
		g2.drawImage(hmc.getImage(), tl.x, tl.y, br.x, br.y, 0, 0, (int)(hmc.width), (int)(hmc.height), null);
			
		g2.setColor(Color.white);
		for(int y = dotYStart; y <= dotYEnd; y += dotDiff)
			for(int x = dotXStart; x <= dotXEnd; x += dotDiff)
				g2.fillRect(x, y, 1, 1);
		
		//draw the axes
		g2.setColor(Color.white);
		g2.fillRect(dotXStart, o.y, dotXEnd, 1);
		g2.fillRect(o.x, dotYStart, 1, dotYEnd);
		
		/*Vec mPointSpace = mPoint.minus(origin);
		g2.drawString((int)(mPointSpace.x) + ", " + (int)(mPointSpace.y), (int)(mPoint.x), (int)(mPoint.y));*/
	}
	
	public void mousePressed(MouseEvent me)	{
		mPoint = new Vec(me.getPoint());
	}

	public void mouseReleased(MouseEvent me) {}
	
	public void mouseDragged(MouseEvent me)	{
		Vec mp = new Vec(me.getPoint());
		origin = origin.minus(mPoint.minus(mp));
		mPoint = mp;
		repaint();
	}
	
	public void mouseClicked(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseMoved(MouseEvent me)	{
		ui.status.setMousePoint(toWorldSpace(new Vec(me.getPoint())));
	}
}