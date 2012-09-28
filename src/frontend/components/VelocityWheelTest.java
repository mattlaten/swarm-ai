package frontend.components;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import math.Vec;
import backend.environment.Animal;
import backend.environment.Prey;

public class VelocityWheelTest extends JFrame {
	public VelocityWheelTest()	{
		super("Test");
		setSize(500, 500);
		setLocationRelativeTo(null);
		
		getContentPane().setLayout(new BorderLayout());
		
		Animal a = new Prey();
		a.setVelocity(new Vec(20, 20));
		
		getContentPane().add(new VelocityWheel(a),
				BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public static void main(String [] args)	{
		new VelocityWheelTest();
	}
}
