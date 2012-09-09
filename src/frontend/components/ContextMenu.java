package frontend.components;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class ContextMenu extends JPopupMenu {
	JMenuItem placePrey, placePredator;
    public ContextMenu(){
        placePrey = new JMenuItem("Place Prey Here");
        placePredator = new JMenuItem("Place Predator Here");
        add(placePrey);
        add(placePredator);
    }
}
