package ui;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UIManager {
	
	private static int max_progress;
	private static int current_progress = 0;
	private static Lock progressLock = new ReentrantLock();

	
	private static UIConsole console;
	
	private static void check(){
		if(UIManager.console == null){
			UIManager.console = new UIConsole(); 
		}
	}
	
	public static void setMaxProgress(int p){
		UIManager.max_progress = p;
	}
	
	public static void addProgress(int n){
		progressLock.lock();
		UIManager.current_progress += n;
		UIManager.setProgress((double) UIManager.current_progress / (double) UIManager.max_progress);
		progressLock.unlock();
	}
	
	public static void log(String message){
		UIManager.check();
		UIManager.console.log(message);
	}
	
	public static void setProgress(double progress){
		UIManager.check();
		UIManager.console.setProgress(progress);
		
	}

}
