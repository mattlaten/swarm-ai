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
import backend.Simulation;
import backend.environment.*;

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
		setSize(600, 400);
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

class Canvas extends JLabel implements MouseListener, MouseMotionListener	{
	UserInterface ui;
	Vec origin = new Vec();
	Vec mPoint = new Vec();
	boolean mouseDragging = false;
	int dotDiff = 10;
	
	public Canvas(UserInterface ui)	{
		this.ui = ui;
		origin = new Vec();
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public Vec getOriginPosition()	{
		return new Vec(getSize().width/2, getSize().height/2).plus(origin);
	}
	
	public Vec getMousePositionInSpace()	{
		return getPositionInSpace(mPoint);
	}
	
	public Vec getPositionInSpace(Vec v)	{
		return v.minus(getOriginPosition()).invertY();
	}
	
	public void paint(Graphics g)	{
		Graphics2D g2 = (Graphics2D)g;
		
		Point o = getOriginPosition().getPoint();
		
		//draw the dots
		g2.setColor(Color.black);
		int dotXStart = o.x - dotDiff*(o.x/dotDiff),
			dotYStart = o.y - dotDiff*(o.y/dotDiff),
			dotXEnd   = o.x + dotDiff*((getSize().width-o.x)/dotDiff),
			dotYEnd   = o.y + dotDiff*((getSize().height-o.y)/dotDiff);
		
		for(int y = dotYStart; y <= dotYEnd; y ++)
			for(int x = dotXStart; x <= dotXEnd; x ++)	{
				g2.setColor(new Color(ui.sim.hm.getInterpolatedHeight(getPositionInSpace(new Vec(x,y))),0f,0f));
				g2.fillRect(x,y,1,1);
			}
				
		
		for(int y = dotYStart; y <= dotYEnd; y += dotDiff)
			for(int x = dotXStart; x <= dotXEnd; x += dotDiff)
				g2.fillRect(x, y, 1, 1);
		
		//draw the axes
		g2.setColor(Color.black);
		g2.fillRect(dotXStart, o.y, dotXEnd, 1);
		g2.fillRect(o.x, dotYStart, 1, dotYEnd);
		
		/*Vec mPointSpace = mPoint.minus(origin);
		g2.drawString((int)(mPointSpace.x) + ", " + (int)(mPointSpace.y), (int)(mPoint.x), (int)(mPoint.y));*/
	}
	
	public void mousePressed(MouseEvent me)	{
		mPoint = new Vec(me.getPoint());
		mouseDragging = true;
	}

	public void mouseReleased(MouseEvent me) {
		mouseDragging = false;
	}
	
	public void mouseDragged(MouseEvent me)	{
		if(mouseDragging)	{
			Vec mp = new Vec(me.getPoint());
			origin = origin.minus(mPoint.minus(mp));
			mPoint = mp;
		}
		repaint();
	}
	
	public void mouseClicked(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseMoved(MouseEvent me)	{
		ui.status.setMousePoint(getPositionInSpace(new Vec(me.getPoint())));
	}
}