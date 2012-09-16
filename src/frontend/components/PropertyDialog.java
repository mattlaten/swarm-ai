package frontend.components;

import java.awt.BorderLayout;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import backend.environment.Element;
import backend.environment.Property;

@SuppressWarnings("serial")
class PropertyTableModel extends AbstractTableModel implements TableModelListener	{
	Element target;
	ArrayList<Field> props = new ArrayList<Field>();
	
	public PropertyTableModel()	{
		addTableModelListener(this);
	}
	
	public void setTarget(Element t, ArrayList<Field> f)	{
		target = t;
		props = f;
	}
	public int getColumnCount() { return 2; }
    public int getRowCount() { return props.size();}
    public Object getValueAt(int row, int col) {
    	try {
        	if(col == 0)
        		return props.get(row).getName();
        	else
        		return props.get(row).get(target).toString();
    	}
    	catch(Exception e)	{
    		e.printStackTrace();
    	}
    	return null;
    }
    public boolean isCellEditable(int row, int col)	{
    	return col == 1;
    }
    
	public void tableChanged(TableModelEvent e) {
		System.out.print("merp");
		fireTableRowsUpdated(e.getFirstRow(), e.getFirstRow());
		Field f = props.get(e.getFirstRow());
		System.out.println(f.getType());
	}
}


class PropertyDialog extends JDialog implements CellEditorListener {
	JTable jt;
	JTextField propKey, propVal;
	
	PropertyTableModel model;
	
	public PropertyDialog(JFrame owner)	{
		super(owner, "Properties");
		
		setSize(400,400);
		setLocationRelativeTo(owner);
		
		getContentPane().setLayout(new BorderLayout());
		
		model = new PropertyTableModel();
		jt = new JTable(model);
		
		getContentPane().add(jt, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public void targetEntity(Element e) throws Exception	{
		String [] cols = {"Property", "Value"};
		ArrayList<Field> props = new ArrayList<Field>();
		Field[] fields = e.getClass().getFields();
		for(Field f : fields)
			//System.out.println(f.getName() + ": " + f.isAnnotationPresent(Property.class) + " " + f.getAnnotations().length);
			if(f.getAnnotation(Property.class) != null)
				props.add(f);
		model.setTarget(e, props);
		jt.repaint();
	}

	public void editingStopped(ChangeEvent e) {
		System.out.println("here");
	}
	public void editingCanceled(ChangeEvent e) {}
}