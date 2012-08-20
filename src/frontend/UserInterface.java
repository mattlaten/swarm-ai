package frontend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import math.Vec;
import backend.Simulation;

public class UserInterface extends JFrame {
	JPanel toolbar;
	JButton modePrey, modePredator, modeModifier, modeObstacle;
	
	Simulation sim;
	Canvas canv;
	
	StatusBar status;
	
	public UserInterface(Simulation sim)	{
		super("Swarm AI");
		this.sim = sim;
		
		//set up things
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		canv = new Canvas(this);
		
		modePrey = new JButton("Prey");
		modePredator = new JButton("Predator");
		modeModifier = new JButton("Modifier");
		modeObstacle = new JButton("Obstacle");
		
		toolbar = new JPanel();
		toolbar.setLayout(new FlowLayout());
		
		status = new StatusBar();
		
		getContentPane().setLayout(new BorderLayout());
		
		//add things
		toolbar.add(modePrey);
		toolbar.add(modePredator);
		toolbar.add(modeModifier);
		toolbar.add(modeObstacle);
		
		getContentPane().add(toolbar, BorderLayout.PAGE_START);
		getContentPane().add(status, BorderLayout.PAGE_END);
		getContentPane().add(canv, BorderLayout.CENTER);
		
		setVisible(true);
	}
}

class StatusBar extends JPanel	{
	JLabel mousePointLab = new JLabel();
	
	public StatusBar()	{
		super();
		setLayout(new BorderLayout());
		
		JPanel pane = new JPanel();
		//pane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		pane.add(mousePointLab);
		
		add(pane, BorderLayout.LINE_END);
	}
	
	public void setMousePoint(Vec m)	{
		mousePointLab.setText("x: " + m.x + ", y: " + m.y);
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