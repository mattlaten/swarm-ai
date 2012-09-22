package frontend.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import backend.environment.Element;
import backend.environment.Predator;
import backend.environment.Prey;
import backend.environment.Waypoint;

import math.Vec;

import frontend.UserInterface;

public class ContextMenu extends JPopupMenu {
	
	JMenuItem placePrey, placePredator, placeWaypoint, setDirection;
	public Vec position;
	
	public ContextMenu(final UserInterface ui){
        placePrey = new JMenuItem("Place Prey Here");
        placePrey.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				try {
					ui.placeElement(position, Prey.class);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
        placePredator = new JMenuItem("Place Predator Here");
        placePredator.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				try {
					ui.placeElement(position, Predator.class);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
        placeWaypoint = new JMenuItem("Place Waypoint Here");
        placeWaypoint.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				try {
					Waypoint w = new Waypoint(position);
					ui.sim.elements.add(w);
					for (Element e : ui.selection)
						e.setTarget(w);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
        setDirection = new JMenuItem("Set Selection Direction");
        setDirection.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)	{
				try {
					ui.setSelectionDirection(position);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
        add(placePrey);
        add(placePredator);
        add(placeWaypoint);
        addSeparator();
        add(setDirection);
    }
}
