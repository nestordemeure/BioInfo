package manager;

import java.util.ArrayList;

public class ParserManager {
	private static String FOLDER_SEPARATOR = "/";
	private static String BASE_FOLDER = "./results/";
	
	private ArrayList<String> ids;
	private ArrayList<String> path;
	
	public ParserManager(ArrayList<String> path){
		this.path = path;
	}
	
	public boolean createPath(){
		
		return true;
	}
}
