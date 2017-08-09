package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;

import javax.swing.JProgressBar;

public class PreLoaderFrame extends Frame{

	private static final long serialVersionUID = 4371905080346793316L;
	private Label info;
	private JProgressBar progress;
	
	public PreLoaderFrame(){
		this.setLayout(new BorderLayout());
		Label title = new Label("Construction de l'arbre...");
		title.setFont(new Font("Arial", Font.BOLD, 30));
		
		info = new Label();
		info.setText("Chargement ...");
		
		progress = new JProgressBar();
		progress.setDoubleBuffered(true);

		this.add(progress,BorderLayout.PAGE_START);
		this.add(title,   BorderLayout.CENTER);
		this.add(info,    BorderLayout.PAGE_END);
		
		this.setTitle("BioInfo : Chargement");
		this.setVisible(true);
		this.setSize(600,150);
	}
	
	public void log(String s){
		this.info.setText(s);
	}
	
	public void setProgress(double n, int cur, int max) {
		this.progress.setValue((int) n);
	}
}
