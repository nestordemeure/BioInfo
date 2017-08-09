package tree;

import java.util.ArrayList;
import java.util.List;

import com.google.common.util.concurrent.ServiceManager;

import tree.TreeBuilderService.OrganismType;
import ui.UIManager;

public class TreeManager {
	
	public static Tree construct(){
		UIManager.setMaxProgress(37+72+60+10);
		List<TreeBuilderService> services = new ArrayList<>();
		TreeBuilderService eukaryotes = new TreeBuilderService(OrganismType.EUKARYOTES);
		TreeBuilderService prokaryotes = new TreeBuilderService(OrganismType.PROKARYOTES);
		TreeBuilderService viruses = new TreeBuilderService(OrganismType.VIRUSES);
		services.add(eukaryotes);
		services.add(prokaryotes);
		services.add(viruses);
		
		ServiceManager sm = new ServiceManager(services);
		sm.startAsync();
		UIManager.log("Tree builder services launched");
		sm.awaitStopped();
		
		UIManager.log("End of tree fetch !");
		UIManager.log("Starting tree build.");
		List<Organism> organisms = new ArrayList<>();
		organisms.addAll(eukaryotes.organisms());
		organisms.addAll(prokaryotes.organisms());
		organisms.addAll(viruses.organisms());
		
		Tree mainTree = new Tree<Tree>();
		
		for(Organism o : organisms){
			o.updateTree(mainTree);
		}
		
		UIManager.log("End of tree build !");
		return mainTree;
	}
}