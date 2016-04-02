package ui;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class MainFrame extends Frame {
	
	private static final long serialVersionUID = -122067383988169509L;
	
	private JProgressBar progress;
	private JTextArea logger;
	private JScrollPane scroll;
	
	public MainFrame(){
		this.setLayout(new BorderLayout());
		logger = new JTextArea();
		DefaultCaret c = (DefaultCaret) logger.getCaret();
		c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		scroll = new JScrollPane(logger);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		progress = new JProgressBar();
		progress.setDoubleBuffered(true);
		progress.setStringPainted(true);

		
		this.add(scroll,  BorderLayout.CENTER);
		this.add(progress,BorderLayout.PAGE_END);
		
		setTitle("BioInfo : Main");
		setSize(800,600);
		setVisible(true);
	}
	
	public void log(String msg){
		this.logger.insert(msg + "\n",this.logger.getText().length());
	}
	
	public void setMaxProgress(int n){
		this.progress.setMaximum(n);
	}
	
	public void setProgress(int n){
		this.progress.setValue(n);
	}

}
