package frontend.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import backend.environment.Predator;
import backend.environment.Prey;

import math.Vec;

import frontend.UserInterface;

public class ContextMenu extends JPopupMenu {
	
	JMenuItem placePrey, placePredator;
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
        add(placePrey);
        add(placePredator);
    }
}
