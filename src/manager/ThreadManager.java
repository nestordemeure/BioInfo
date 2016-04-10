package manager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import configuration.Configuration;

import tree.Tree;
import ui.UIManager;

public class ThreadManager {
	protected static int NB_THREADS = Configuration.THREADS_NUMBER;
	private static Lock mainLock = new ReentrantLock();
	
	public static void start(Tree t, ArrayList<String> path) throws InterruptedException {
		ThreadManager.startThreads(t, path);
		while(ThreadManager.nbThreads() != Configuration.THREADS_NUMBER) {
			Thread.sleep(1000);
		}
	}
	
	private static void startThreads(Tree t, ArrayList<String> path) {
		Object[] nodes = t.nodes();
		
		for (Object o : nodes) {
			String node = (String) o;
			// Path du nouveau noeud
			ArrayList<String> new_path = new ArrayList<String>(path);
			new_path.add(node);
			
			// Si le noeud est une feuille
			if (t.isLeaf(node)) {
				// Si il est possible de lancer un thread on le fait
				if (ThreadManager.threadAvailable()) {
					UIManager.addProgress(1);
					UIManager.log("Launching new thread ("+new_path.get(new_path.size() - 1)+")");
					new Thread(new ParserManager(new_path)).start();
					ThreadManager.minusThread();
				}
				// Sinon on attend qu'un thread finisse 
				else {
					boolean done = false;
					while (!done) {
						if (!ThreadManager.threadAvailable()) {
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (InterruptedException e) {
							}
						}
						else {
							done = true;
						}		
					}
					UIManager.addProgress(1);
					UIManager.log("Launching new thread ("+new_path.get(new_path.size() - 1)+")");
					new Thread(new ParserManager(new_path)).start();
					ThreadManager.minusThread();;
				}		
			}
			else {
				ThreadManager.startThreads((Tree) t.get(node), new_path);
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
	
	private static int nbThreads() {
		mainLock.lock();
		int nb = NB_THREADS;
		mainLock.unlock();
		return nb;
	}

}
