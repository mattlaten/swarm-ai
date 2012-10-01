package frontend.components;

import java.awt.BorderLayout;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import math.Vec;

/**
 * The status bar that updates at the bottom of the UI
 *
 */
public class StatusBar extends JPanel	{
	JLabel mousePoint = new JLabel(),
		   mode = new JLabel(),
		   zoom = new JLabel();
	DecimalFormat d = new DecimalFormat("0.00");
	
	public StatusBar()	{
		super();
		setLayout(new BorderLayout());
		
		JPanel pane = new JPanel();
		//pane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
		pane.add(mousePoint);
		pane.add(zoom);
		
		add(pane, BorderLayout.LINE_END);
		add(mode, BorderLayout.LINE_START);
	}
	
	public void setMousePoint(Vec m)	{
		mousePoint.setText("x: " + (int)(m.x) + ", y: " + (int)(m.y));
	}
	
	public void setZoom(double z)	{
		zoom.setText("zoom: " + d.format(z));
	}
	
	public void setMode(String mode)	{
		this.mode.setText(mode);
	}
}