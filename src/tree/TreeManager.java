package tree;

import java.util.ArrayList;
import java.util.List;

import com.google.common.util.concurrent.ServiceManager;

import tree.TreeBuilderService.OrganismType;

public class TreeManager {
	
	public static void construct(){
		List<TreeBuilderService> services = new ArrayList<TreeBuilderService>();
		TreeBuilderService eukaryotes = new TreeBuilderService(OrganismType.EUKARYOTES);
		TreeBuilderService prokaryotes = new TreeBuilderService(OrganismType.PROKARYOTES);
		TreeBuilderService viruses = new TreeBuilderService(OrganismType.VIRUSES);
		services.add(eukaryotes);
		services.add(prokaryotes);
		services.add(viruses);
		
		ServiceManager sm = new ServiceManager(services);
		sm.startAsync();
		System.out.println("Tree builder services launched");
		sm.awaitStopped();
		System.out.println("End of tree build !");
		
		int ocount = eukaryotes.oCount;
		ocount += prokaryotes.oCount;
		ocount += viruses.oCount;
		
		int count = eukaryotes.count;
		count += prokaryotes.count;
		count += viruses.count;
		
		System.out.println("FINAL : "+count+"/"+ocount);
	}
}