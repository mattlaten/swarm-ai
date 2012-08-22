package frontend;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import math.Vec;

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