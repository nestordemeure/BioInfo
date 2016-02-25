package manager;

import io.IdFetcher;

import java.io.File;
import java.util.ArrayList;

import ui.UIManager;

public class ParserManager implements Runnable{
	private static String FOLDER_SEPARATOR = "/";
	private static String BASE_FOLDER = "/tmp/results/";
	
	private ArrayList<Integer> ids;
	private ArrayList<String> path;
	private String data_path = "";
	private String specy_name;
	
	public ParserManager(ArrayList<String> path){
		this.path = path;
		this.specy_name = this.path.get(this.path.size() - 1);
	}
	
	public boolean createPath(){
		String cur = BASE_FOLDER;
		for(String s : path){
			cur += ParserManager.FOLDER_SEPARATOR + s;
		}
	
		this.data_path = cur;
		AccessManager.accessFile(this.data_path);
		File p = new File(this.data_path);
		if(p.exists() && p.isDirectory()) {
			AccessManager.doneWithFile(this.data_path);
			return true;
		}else{
			boolean ok = p.mkdirs();
			AccessManager.doneWithFile(this.data_path);
			return ok;
		}
	}

	public void run() {
		UIManager.log("[ParserManager : "+this.specy_name+"] starting ...");
		if( ! this.createPath()){
			UIManager.log("[ParserManager : "+this.specy_name+"] Unable to create path : "+data_path+" stopping thread.");
		}
		
		ids = IdFetcher.getIds(this.specy_name);
		
		for(int i : ids){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		ThreadManager.threadFinished();
		UIManager.log("[ParserManager : "+this.specy_name+"] finished ...");

	}
}
