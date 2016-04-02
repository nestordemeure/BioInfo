package ui;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

}
