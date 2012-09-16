package frontend.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import backend.Simulation;

public class ControlBar extends JPanel implements Runnable, ActionListener {
	JProgressBar progress;
	JButton playPause;
	JLabel beginning, end;
	
	Simulation sim;
	
	public ControlBar(Simulation sim)	{
		super();
		
		this.sim = sim;
		
		setLayout(new BorderLayout());
		
		playPause = new JButton("Play");
		playPause.addActionListener(this);
		
		progress = new JProgressBar();
		progress.setMinimum(0);
		progress.setStringPainted(true);
		progress.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me)	{
				updateTime(me.getPoint().x);
			}
		});
		progress.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent me)	{
				updateTime(me.getPoint().x);
			}
		});
		progress.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ce) {
				updateProgressString();
			}
		});
		
		add(playPause, BorderLayout.LINE_START);
		add(progress, BorderLayout.CENTER);
		
		setVisible(true);
		
		new Thread(this).start();
	}
	
	public void updateTime(double mousePosition)	{
		sim.setTime((int)(sim.getTotalTime()*mousePosition/progress.getSize().width));
	}
	
	public void updateProgressString()	{
		int milli = sim.getTime();
		int t = milli/1000;
		progress.setString(t/60 + ":" + t % 60 + "." + (milli % 1000)/10);
	}
	
	public void actionPerformed(ActionEvent ae)	{
		sim.isRunning = !sim.isRunning;
		playPause.setText(sim.isRunning ? "Pause" : "Play");
	}
	
	public void flip()
	{
		sim.isRunning = !sim.isRunning;
		playPause.setText(sim.isRunning ? "Pause" : "Play");
	}
	
	public void run()	{
		try {
			while(true)	{
				Thread.sleep(20);
				progress.setMaximum(sim.getTotalTime());
				progress.setValue(sim.getTime());
				//progress.repaint();
			}
		}
		catch(InterruptedException ie)	{
			
		}
	}
}
