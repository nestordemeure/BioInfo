package ui;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UIManager {
	
	private static int max_progress;
	private static int current_progress = 0;
	private static Lock progressLock = new ReentrantLock();

	
	private static UIConsole console;
	private static UIGraphics graphics;
	
	private static void check(){
		if(UIManager.console == null){
			UIManager.console = new UIConsole(); 
			UIManager.graphics = new UIGraphics();
		}
	}
	
	public static void setMaxProgress(int p){
		UIManager.max_progress = p;
	}
	
	public static void addProgress(int n){
		progressLock.lock();
		UIManager.current_progress += n;
		UIManager.setProgress(100.0f * (double) UIManager.current_progress / (double) UIManager.max_progress);
		progressLock.unlock();
	}
	
	public static void log(String message){
		UIManager.check();
		UIManager.console.log(message);
		UIManager.graphics.log(message);
	}
	
	public static void setProgress(double progress){
		UIManager.check();
		UIManager.console.setProgress(progress);
		UIManager.graphics.setProgress(UIManager.current_progress, UIManager.max_progress);
	}
	
	public static void startPreloading(){
		UIManager.check();
		UIManager.graphics.startPreloader();
	}
	
	public static void startMainProcess(){
		UIManager.check();
		UIManager.graphics.startMainProcess();
	}

}
