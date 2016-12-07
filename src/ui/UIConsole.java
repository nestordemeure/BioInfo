package ui;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import manager.ThreadManager;
import manager.TreeWalkerManager;
import tree.Tree;
import tree.TreeManager;
import configuration.Configuration;
import excel.ExcelManager;

public class UIConsole {
	private static Lock consoleLock = new ReentrantLock();
	
	public void log(String str){
		consoleLock.lock();
		System.out.println(str);
		consoleLock.unlock();
	}
	
	public void setProgress(double progress){
		consoleLock.lock();
		System.out.print("Progress : |");
		for(int i = 0; i < 100; i++){
			if(i <= progress){
				System.out.print("#");
			} else {
				System.out.print(" ");
			}
		}
		System.out.println("| "+progress+"%");
		consoleLock.unlock();
	}
	
	public void launchProcess(Tree t){
		// Creating species statistics
		UIManager.setMaxProgress(t.size());
		
		TreeWalkerManager.start(t);
		//ThreadManager.start(t);
		
		// Merge excels files
		UIManager.log("Creating excel files...");
		ExcelManager.fusionExcels(Configuration.BASE_FOLDER);
		UIManager.log("Done !");
	}

}
