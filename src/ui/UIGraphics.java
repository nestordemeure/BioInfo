package ui;
import java.util.ArrayList;

import manager.ThreadManager;
import configuration.Configuration;
import excel.ExcelManager;
import tree.Tree;

public class UIGraphics {
	private PreLoaderFrame plFrame;
	private MainFrame mFrame;
	private Tree tree;

	public UIGraphics(){
		this.plFrame = null;
		this.mFrame = null;
	}

	public void startPreloader(){
		this.plFrame = new PreLoaderFrame();
	}

	public void startMainProcess(Tree t){
		this.tree = t;
		if(this.plFrame != null){
			this.plFrame.setVisible(false);
			this.plFrame.dispose();
			this.plFrame = null;
		}
		this.mFrame = new MainFrame(t);
	}

	public void log(String str){
		if(this.plFrame != null){
			this.plFrame.log(str);
		} else if(this.mFrame != null){
			this.mFrame.log(str);
		}
	}

	public void setProgress(double n){
		if(this.mFrame != null){
			this.mFrame.setProgress(n);
		}
	}

	public void launchProcess(final ArrayList<InfoNode> nodes){
		Thread thread = new Thread(){
			public void run(){
				ThreadManager.start(tree, nodes);

				// Merge excels files
				UIManager.log("Creating excel files...");
				ExcelManager.fusionExcels(Configuration.BASE_FOLDER);
			}
		};

		thread.start();
	}
}
