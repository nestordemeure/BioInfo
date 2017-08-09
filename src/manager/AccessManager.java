package manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccessManager {
	
	private static Map<String, Lock> lockers; // File lockers
	private static Lock mainLock = new ReentrantLock(); // Main class lock (To prevent lockers from being used from two different threads)

	
	public static void accessFile(String file){
		mainLock.lock();
		if(AccessManager.lockers == null){
			AccessManager.lockers = new HashMap<>(10);
		}
		
		if(! AccessManager.lockers.containsKey(file)){
			AccessManager.lockers.put(file, new ReentrantLock());
		}
		
		Lock l = AccessManager.lockers.get(file);
		AccessManager.mainLock.unlock();
		l.lock();
		
	}
	
	public static void doneWithFile(String file){
		AccessManager.mainLock.lock();
		if(AccessManager.lockers.containsKey(file)){
			AccessManager.lockers.get(file).unlock();
		}
		AccessManager.mainLock.unlock();
	}
}
