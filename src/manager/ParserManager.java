package manager;

import io.IdFetcher;
import io.Net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import configuration.Configuration;
import excel.ExcelWriter;

import Bdd.Bdd;
import Parser.Parser;

import ui.UIManager;

public class ParserManager implements Runnable{
	
	private ArrayList<Integer> ids;
	private ArrayList<String> path;
	private String data_path = "";
	private String specy_name;
	private Bdd db;
	
	public ParserManager(ArrayList<String> path){
		this.path = path;
		this.specy_name = this.path.get(this.path.size() - 1);
	}
	
	// Creation du dossier sur le système de fichiers local
	public boolean createPath(){
		// Construction de la chaine de charactere
		String cur = Configuration.BASE_FOLDER;
		for(String s : path){
			cur += Configuration.FOLDER_SEPARATOR + s;
		}
	
		this.data_path = cur;
		
		// Création du dossier
		
		AccessManager.accessFile(this.data_path);
		File p = new File(this.data_path);
		
		if(p.exists() && p.isDirectory()) {
			// Si le dossier existe déjà
			AccessManager.doneWithFile(this.data_path);
			return true;
		}else{
			// Si le fichier n'existe pas
			boolean ok = p.mkdirs();
			AccessManager.doneWithFile(this.data_path);
			return ok;
		}
	}
	
	
	// Verifie si le fichier ids.txt correspond a ce qu'on a en mémoire
	public ArrayList<Integer> getAlreadyDone(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		String file = this.data_path+Configuration.FOLDER_SEPARATOR+"ids.txt";
		// On liste les Ids présents dans le fichier.
		AccessManager.accessFile(file);
		try{
			Scanner sc = new Scanner(new FileReader(file));
			// Pour tous les lignes du fichiers
			while(sc.hasNext()){
				try{
					int id = Integer.parseInt(sc.next());
					// On ajoute l'ID dans la liste d'ids
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
		return list;		
	}
	
	public void deleteIdFile(){
		String file = this.data_path+Configuration.FOLDER_SEPARATOR+"ids.txt";
		File f = new File(file);
		if(f.exists() && f.isFile()){
			f.delete();
		}
	}
	
	public Bdd createDB(){
		String dbFile = this.data_path+Configuration.FOLDER_SEPARATOR+"results";
		File f = new File(dbFile + ".bdd");
		Bdd db = null;
		if(f.exists() && f.isFile()){
			try {
				db = new Bdd(dbFile);
			} catch (IOException e) {
				db = null;
			}
		} else {
			UIManager.log("Cannot open db file.");
		}
		
		if(db == null){
			db = new Bdd();
			this.deleteIdFile();
		}
		
		return db;
	}

	// Ecriture des fichiers de résultats
	public void writeFiles(){
		// Construction des chemins de fichiers
		String file = this.data_path+Configuration.FOLDER_SEPARATOR+"ids.txt";
		String excelFile = this.data_path+Configuration.FOLDER_SEPARATOR+"results.xls";
		String dbFile = this.data_path+Configuration.FOLDER_SEPARATOR+"db";
		
		// Création du fichier d'identifiants
		//this.createOrResetFile();
		AccessManager.accessFile(file);
		
		try{
			// Ajout des identifiants dans le fichier ids.txt
			PrintWriter writer = new PrintWriter(new FileOutputStream(new File(file),true));
			for(int id : this.ids){
				writer.println(id);
			}
			writer.close();
		}catch(Exception e){
			UIManager.log("[ParserManager : "+this.specy_name+"] Cannot write to file : "+file);
		}
		AccessManager.doneWithFile(file);
		
		
		// Ecriture du fichier excel
		AccessManager.accessFile(excelFile);
		try{
			String[] str = new String[this.path.size()];
			ExcelWriter.writer(excelFile, this.path.toArray(str), this.db);
		}catch(Exception e){
			UIManager.log("[ParserManager : "+this.specy_name+"] Cannot write to excel file ...");
			e.printStackTrace();
		}
		
		AccessManager.doneWithFile(excelFile);
	}
	
	public void launchParser(String ids, int length){
		String url = Configuration.GEN_DOWNLOAD_URL.replaceAll("<ID>", ids);
		Scanner sc = Net.getUrl(url);
		try{
			Parser p = new Parser(this.db, Net.getUrl(url));
			p.parse(length);
			if(sc != null){
				sc.close();
			}
		}catch(Exception e){
			if(sc != null){
				sc.close();
			}
			UIManager.log("Error while parsing file "+url);
			e.printStackTrace();
		}catch(Error e){
			if(sc != null){
				sc.close();
			}
			UIManager.log("Error while parsing file "+url);
			e.printStackTrace();
		}
	}

	public void run() {
		UIManager.log("[ParserManager : "+this.specy_name+"] starting ...");
		// Création du repertoire
		if( ! this.createPath()){
			UIManager.log("[ParserManager : "+this.specy_name+"] Unable to create path : "+data_path+" stopping thread.");
		} else {
			// Recuperation des Ids
			ArrayList<Integer> done = this.getAlreadyDone();
			ids = IdFetcher.getIds(this.specy_name, done.size());
			this.ids.removeAll(done);
			// S'il n'y a aucune différence (avec le fichier ids.txt).
			if(this.ids.size() == 0){
				// On skip.
				UIManager.log("[ParserManager : "+this.specy_name+"] Already done ... Skipping ...");
			} else {
				UIManager.log("[ParserManager : "+this.specy_name+"] Starting with "+this.ids.size()+" ids.");
				// S'il y a des différences, on lance le calcul.
				this.db = this.createDB();
								
				int pos = 0;
				String id_list = "";
				for(int id : ids){
					pos ++;
					if(pos == Configuration.GEN_PER_DOWNLOAD){
						id_list += Integer.toString(id);
						this.launchParser(id_list, pos);
						pos = 0;
						id_list = "";
					} else {
						id_list += Integer.toString(id)+",";
					}
				}
				
				if(pos != 0){
					// On enleve la dernière virgule.
					id_list = id_list.substring(0, id_list.length() - 1);
					this.launchParser(id_list, pos);
				}
				
				// Ecriture des fichiers de résultats.
				this.writeFiles();
			}
		}
		// Arret de thread
		ThreadManager.threadFinished(); // On previens le threadmanager.
		UIManager.log("[ParserManager : "+this.specy_name+"] finished ...");
	}
}
