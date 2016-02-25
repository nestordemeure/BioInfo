package manager;

import io.IdFetcher;
import io.Net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import Bdd.Bdd;
import Parser.Parser;

import ui.UIManager;

public class ParserManager implements Runnable{
	private static String FOLDER_SEPARATOR = "/";
	private static String BASE_FOLDER = "/tmp/results";
	private static String CORE_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=<ID>&rettype=gb";
	
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
	
	private void createOrResetFile(){
		String file = this.data_path+FOLDER_SEPARATOR+"ids.txt";
		AccessManager.accessFile(file);
		File f = new File(file);
		if(f.exists() && f.isFile()){
			f.delete();
		}
		
		try {
			f.createNewFile();
		} catch (IOException e) {
			UIManager.log("[ParserManager : "+this.specy_name+"] Cannot create file : "+file);
		}
		
		AccessManager.doneWithFile(file);
	}
	
	
	// Verifie si le fichier ids.txt correspond a ce qu'on a en mémoire
	public boolean isDone(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		String file = this.data_path+FOLDER_SEPARATOR+"ids.txt";
		// On liste les Ids présents dans le fichier.
		AccessManager.accessFile(file);
		try{
			Scanner sc = new Scanner(new FileReader(file));
			while(sc.hasNext()){
				try{
					int id = Integer.parseInt(sc.next());
					if(! list.contains(id)){
						list.add(id);
					}
				}catch(NumberFormatException e){
					
				}
			}
			sc.close();
		}catch(Exception e){
			UIManager.log("[ParserManager : "+this.specy_name+"] Cannot read file : "+file);
		}
		AccessManager.doneWithFile(file);
		
		if(list.containsAll(this.ids) && this.ids.containsAll(list)){
			return true;
		} else {
			return false;
		}
	}

	public void writeFile(){
		String file = this.data_path+FOLDER_SEPARATOR+"ids.txt";
		this.createOrResetFile();
		AccessManager.accessFile(file);
		try{
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			for(int id : this.ids){
				writer.println(id);
			}
			writer.close();
		}catch(Exception e){
			UIManager.log("[ParserManager : "+this.specy_name+"] Cannot write to file : "+file);
		}
		AccessManager.doneWithFile(file);
	}

	public void run() {
		UIManager.log("[ParserManager : "+this.specy_name+"] starting ...");
		if( ! this.createPath()){
			UIManager.log("[ParserManager : "+this.specy_name+"] Unable to create path : "+data_path+" stopping thread.");
		} else {
			ids = IdFetcher.getIds(this.specy_name);
			if(this.isDone()){
				UIManager.log("[ParserManager : "+this.specy_name+"] Already done ... Skipping ...");
			} else {
				Bdd db = new Bdd(this.data_path);
				
				
				for(int id : ids){
					try{
						UIManager.log("[ParserManager : "+this.specy_name+"] Analysing "+id+"...");
						String url = ParserManager.CORE_URL.replaceAll("<ID>", Integer.toString(id));
						Parser p = new Parser(db, Net.getUrl(url));
						p.parse();
					}catch(Exception e){
						UIManager.log("Error while parsing file...");
						e.printStackTrace();
					}
				}
				
				this.writeFile();
				
				ThreadManager.threadFinished();
				UIManager.log("[ParserManager : "+this.specy_name+"] finished ...");
			}
		}
	}
}
