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

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;

import Bdd.Bdd;
import configuration.Configuration;
import excel.ExcelWriter;
import tree.Organism;
import tree.Tree;
import ui.UIManager;

public class OrganismsFetcherService extends AbstractExecutionThreadService {
	private TreeWalker walker;
	private int id;
	
	public OrganismsFetcherService(TreeWalker walker, int id){
		this.walker = walker;
		this.id = id;
	}

	public Organism readOrganism(Organism o){
		String file = o.getPath()+Configuration.FOLDER_SEPARATOR+o.getName()+".org";
		
		Organism res = null;
		AccessManager.accessFile(file);
		ObjectInputStream inputstream;
		FileInputStream chan;
		try{
			chan = new FileInputStream(file);
			inputstream = new ObjectInputStream(chan);
			res = (Organism) inputstream.readObject();
			inputstream.close();
			chan.close();
		}catch(Exception e){
			UIManager.log("[Organism fetcher "+this.id+"] Unable to read "+o.getName()+" file.");
		}
		AccessManager.doneWithFile(file);
		return o;
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
		UIManager.log("[Organism fetcher "+this.id+"] Starting organism: "+o.getName());
		if(!o.createPath()){
			UIManager.log("[Organism fetcher "+this.id+"] Cannot create path for "+o.getName());
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
		
				
		for(String replicon : o.getReplicons().keySet()){
			RepliconParserManager manager = new RepliconParserManager(o, replicon, maindb);
			manager.run();
		}
	
		this.writeOrganism(o);
		
		try {
			maindb.exportBase(dbPath);
		} catch (IOException e) {
			UIManager.log("[Organism fetcher "+this.id+"] Cannot write db file : "+o.getName()+" : "+dbPath);
		}
		
		String[] chemin=new String[4];
		chemin[0]=o.getKingdom();
		chemin[1]=o.getGroup();
		chemin[2]=o.getSubgroup();
		chemin[3]=o.getName();
		
		UIManager.log("Writing Excel file for : "+o.getName());
		try{
			ExcelWriter.writer(o.getPath(),o.getPath()+Configuration.FOLDER_SEPARATOR+o.getName(), chemin, maindb, true);
		}catch(Exception e){
			UIManager.log("Error while writing excel file for : "+o.getName());
			e.printStackTrace();
		}
		
		UIManager.log("[Organism fetcher "+this.id+"] Done with organism : "+o.getName());
	}
	
	@Override
	protected void run() throws Exception {
		UIManager.log("[Organism fetcher "+this.id+"] Starting...");
		Organism cur = walker.next();
		while(cur != null){
			this.launchParser(cur);
			cur = walker.next();
		}
		UIManager.log("[Organism fetcher "+this.id+"] Ending !");
	}
	
	public static void launch(Tree t){
		TreeWalker walker = new TreeWalker(t);
		
		Set<Service> services =  new LinkedHashSet<Service>();
		for(int i = 0; i < Configuration.THREADS_NUMBER; i++){
			services.add(new OrganismsFetcherService(walker, i));
		}
		
		ServiceManager manager = new ServiceManager(services);
		manager.startAsync();
		manager.awaitStopped();
	}
	
}
