package manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import tree.Tree;

public class ThreadManager {
	
	protected static int NB_THREADS = 10;
	private static Lock mainLock = new ReentrantLock();
	
	public static void start(Tree t, ArrayList<String> path) {
		String[] nodes = (String[]) t.nodes();
		
		for (String node : nodes) {
			// Path du nouveau noeud
			ArrayList<String> new_path = new ArrayList<String>(path);
			new_path.add(node);
			
			// Si le noeud est une feuille
			if (t.isLeaf(node)) {
				// Si il est possible de lancer un thread on le fait
				if (ThreadManager.threadAvailable()) {
					new Thread(new ParserManager(new_path)).start();
					ThreadManager.minusThread();
				}
				// Sinon on attend qu'un thread finisse 
				else {
					boolean done = false;
					while (!done) {
						if (!ThreadManager.threadAvailable()) {
							TimeUnit.SECONDS.sleep(1);
						}
						else {
							done = true;
						}		
					}
					
					new Thread(new ParserManager(new_path)).start();
					ThreadManager.minusThread();;
				}		
			}
			else {
				ThreadManager.start((Tree) t.get(node), new_path);
			}
		}
	}
	
	// Informe qu'un thread s'est terminé
	public static void threadFinished() {
		ThreadManager.plusThread();
	}
	
	// Informe s'il est possible de créer un thread
	private static boolean threadAvailable() {
		mainLock.lock();
		boolean res = true;
		
		if (NB_THREADS == 0) {
			res = false;
		}
		
		mainLock.unlock();
		return res;
	}
	
	private static void plusThread() {
		mainLock.lock();
		NB_THREADS++;
		mainLock.unlock();
	}
	
	private static void minusThread() {
		mainLock.lock();
		NB_THREADS--;
		mainLock.unlock();
	}

}
