package manager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import configuration.Configuration;

import tree.Tree;
import ui.InfoNode;
import ui.UIManager;

public class ThreadManager {
	protected static int NB_THREADS = Configuration.THREADS_NUMBER;
	private static Lock mainLock = new ReentrantLock();
	
	public static void start(Tree t, ArrayList<InfoNode> s){
		ThreadManager.startThreads(t, new ArrayList<String>(), s);
		while(ThreadManager.nbThreads() != Configuration.THREADS_NUMBER) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	public static int getNodesCount(Tree t, ArrayList<String> path, ArrayList<InfoNode> selected){
		int sum = 0;
		Object[] nodes = t.nodes();
		for(Object o : nodes){
			String node = (String) o;
			ArrayList<String> new_path = new ArrayList<String>(path);
			new_path.add(node);
			if(ThreadManager.isInSelected(new_path, selected)){
				if(t.isLeaf(node)){
					sum += 1;
				} else {
					sum += ThreadManager.getNodesCount((Tree)t.get(node), new_path, selected);
				}
			}
		}
		
		return sum;
	}
	
	public static void start(Tree t){
		InfoNode n = new InfoNode("All", new ArrayList<String>());
		ArrayList<InfoNode> a = new ArrayList<InfoNode>();
		a.add(n);
		ThreadManager.start(t,a);
	}
	
	private static void startThreads(Tree t, ArrayList<String> path, ArrayList<InfoNode> selected) {
		Object[] nodes = t.nodes();
		
		for (Object o : nodes) {
			String node = (String) o;
			// Path du nouveau noeud
			ArrayList<String> new_path = new ArrayList<String>(path);
			new_path.add(node);
			
			// Si l'on a choisi le nouveau chemin
			if(ThreadManager.isInSelected(new_path, selected)){
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
					ThreadManager.startThreads((Tree) t.get(node), new_path, selected);
				}
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
	
	private static boolean isInSelected(ArrayList<String> path, ArrayList<InfoNode> selected){
		for(InfoNode n : selected){
			if(n.canBeInPath(path)){
				return true;
			}
		}
		return false;
	}

}
