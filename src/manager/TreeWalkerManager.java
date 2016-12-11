package manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import Bdd.Bdd;
import configuration.Configuration;
import tree.Organism;
import tree.Tree;
import ui.UIManager;

public class TreeWalkerManager {
	private Tree tree;
	
	public TreeWalkerManager(Tree tree){
		this.tree = tree;
	}
	
	public static void start(Tree tree){
		TreeWalkerManager manager = new TreeWalkerManager(tree);
		manager.start();
	}
	
	public void start(){
		this.startWalker(this.tree);
	}
	
	public void startWalker(Tree t){
		Object[] nodes = t.activatedNodes();
		for(Object o : nodes){
			String node = (String)o;
			if(t.isLeaf(node)){
				this.launchParser((Organism)t.get(node));
			} else {
				startWalker((Tree)t.get(node));
			}
		}
	}
	
	public Organism readOrganism(Organism o){
		String file = o.getPath()+Configuration.FOLDER_SEPARATOR+o.getName()+".org";
		AccessManager.accessFile(file);
		ObjectInputStream inputstream;
		FileInputStream chan;
		try{
			chan = new FileInputStream(file);
			inputstream = new ObjectInputStream(chan);
			inputstream.close();
			chan.close();
			return (Organism) inputstream.readObject();
		}catch(Exception e){
			UIManager.log("[TreeWalker] Unable to read "+o.getName()+" file.");
		}
		AccessManager.doneWithFile(file);
		return null;
	}
	
	public void writeOrganism(Organism o){
		String file = o.getPath()+Configuration.FOLDER_SEPARATOR+o.getName()+".org";
		AccessManager.accessFile(file);
		Path p = FileSystems.getDefault().getPath(file);
		try {
			Files.deleteIfExists(p);
		}catch(IOException e){
		}
		
		try{
			FileOutputStream chan = new FileOutputStream(file);
			ObjectOutputStream outputstream = new ObjectOutputStream(chan);
			outputstream.writeObject(o);
			outputstream.close();
		} catch (IOException e) {
		}
	}
	
	public void launchParser(Organism o){
		UIManager.log("[TreeWalker] Starting organism: "+o.getName());
		if(!o.createPath()){
			UIManager.log("[TreeWalker] Cannot create path for "+o.getName());
			return;
		}
		
		Bdd maindb;
		Organism oldOrganism = this.readOrganism(o);
		String dbPath = o.getPath()+Configuration.FOLDER_SEPARATOR+o.getName();
		if(oldOrganism == null) {
			maindb = new Bdd();
		} else {
			try {
				maindb = new Bdd(dbPath);
			} catch (IOException e) {
				maindb = new Bdd();
			}
			o.removeReplicons(oldOrganism.getProcessedReplicons());
		}
		
		Set<Service> services = new LinkedHashSet<Service>();
		
		for(String replicon : o.getReplicons().keySet()){
			RepliconParserManager manager = new RepliconParserManager(o, replicon, maindb);
			services.add(manager);
		}
		
		ServiceManager manager = new ServiceManager(services);
		
		manager.startAsync();
		manager.awaitStopped();
	
		this.writeOrganism(o);
		
		try {
			maindb.exportBase(dbPath);
		} catch (IOException e) {
			UIManager.log("[TreeWalker] Cannot write db file : "+o.getName()+" : "+dbPath);
		}
		
		UIManager.log("[ŦreeWalker] Done with organism : "+o.getName());
	}
}
