package ui;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;

public class MainFrame extends Frame {
	
	private static final long serialVersionUID = -122067383988169509L;
	
	private JProgressBar progress;
	private JTextArea logger;
	
	public MainFrame(){
		this.setLayout(new BorderLayout());
		logger = new JTextArea();
		
		progress = new JProgressBar();
		progress.setDoubleBuffered(true);
		progress.setStringPainted(true);

		
		this.add(logger,  BorderLayout.CENTER);
		this.add(progress,BorderLayout.PAGE_END);
		
		setTitle("BioInfo : Main");
		setSize(800,600);
		setVisible(true);
	}
	
	public void log(String msg){
		this.logger.insert(msg + "\n",0);
	}
	
	public void setMaxProgress(int n){
		this.progress.setMaximum(n);
	}
	
	public void setProgress(int n){
		this.progress.setValue(n);
	}

}
