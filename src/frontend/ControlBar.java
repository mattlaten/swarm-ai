package frontend;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ControlBar extends JPanel implements Runnable {
	JProgressBar progress;
	JButton playPause;
	JLabel beginning, end;
	
	public ControlBar()	{
		super();
		
		setLayout(new BorderLayout());
		
		playPause = new JButton("Play");
		progress = new JProgressBar();
		
		add(playPause, BorderLayout.LINE_START);
		add(progress, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public void run()	{
		try {
			while(true)	{
				Thread.sleep(20);
			}
		}
		catch(InterruptedException ie)	{
			
		}
	}
}
