package tree;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.AbstractService;

public class TreeBuilderService extends AbstractExecutionThreadService {
	
	public static enum OrganismType {
		EUKARYOTES,
		PROKARYOTES,
		VIRUSES,
	}
	public int count = 0;
	public int oCount = 0;
	private OrganismType type;
	private String baseURL;
	private int currentPage;
	
	private Retryer<Boolean> retryer;
	
	private Callable<Boolean> pageCallable = new Callable<Boolean>(){
		public Boolean call() throws MalformedURLException, IOException{
			return parseCurrentPage();
		}
	};
	
	public TreeBuilderService(OrganismType type){
		this.retryer = RetryerBuilder.<Boolean>newBuilder()
				.retryIfExceptionOfType(IOException.class)
				.retryIfResult(Predicates.<Boolean>isNull())
				.retryIfRuntimeException()
				.withStopStrategy(StopStrategies.stopAfterAttempt(3))
				.withWaitStrategy(WaitStrategies.fibonacciWait())
				.build();
		
		switch(type){
		case EUKARYOTES:
			this.baseURL = configuration.Configuration.TREE_EUKARYOTES_URL;
			break;
		case PROKARYOTES:
			this.baseURL = configuration.Configuration.TREE_PROKARYOTES_URL;
			break;
		case VIRUSES:
			this.baseURL = configuration.Configuration.TREE_VIRUSES_URL;
		}
		this.type = type;
	}
	
	public void readAllPages(){
		this.currentPage = 1;
		boolean cont = true;
		while(cont) {
			try{
				Boolean result = retryer.call(this.pageCallable);
				if(result == false){
					cont = false;
				}
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println(this.type.toString()+ " page : "+this.currentPage);
			System.out.println(this.type.toString() +" : "+ this.oCount + "/" + this.count);
			currentPage ++;
		}
	}
	
	public boolean parseCurrentPage() throws MalformedURLException, IOException{
		String webPage = new String(Resources.toByteArray(new URL(this.baseURL+this.currentPage)));
		
		if(webPage.split("-->")[1].trim().length() == 0){
			return false;
		}
		
		Document doc = Jsoup.parse("<table>"+webPage+"</table>");
		
		Elements organisms = doc.select(".Odd,.Even");
		
		for(Iterator<Element> it = organisms.iterator(); it.hasNext();){
			Element organism = it.next();
			Elements replicons = organism.select("table");
			
			if(replicons.size() != 0){
				Elements organismTDs = organism.select("td");
				Iterator<Element> tdIterator = organismTDs.iterator();
				String organismName = tdIterator.next().text();
				if(type == OrganismType.PROKARYOTES) {
					tdIterator.next(); // Skip CladeID
				}
				if(type != OrganismType.VIRUSES) {
					tdIterator.next(); // Skip Strain
					tdIterator.next(); // Skip BioSample
				}
				String organismBioProject = tdIterator.next().text();
				String organismGroup = tdIterator.next().text();
				String organismSubGroup = tdIterator.next().text();

				boolean validOrganism = false;
				
//				System.out.println("*************************");
//				System.out.println("Name      : "+organismName);
//				System.out.println("Project   : "+organismBioProject);
//				System.out.println("Group     : "+organismGroup);
//				System.out.println("SubGroup  : "+organismSubGroup);
//				System.out.println("Replicons : ");
//				 
				Elements repliconsTDs = replicons.iterator().next().select("td");
				for(Iterator<Element> it2 = repliconsTDs.iterator(); it2.hasNext();) {
					Element replicon = it2.next();
					if(replicon.id().length() != 0) {
						// Skip the "show more button"
						// The show more button is the only one with an id
						continue;
					}
					String repliconName = "";
					if(type == OrganismType.VIRUSES) {
						repliconName = replicon.text().split(":")[0];
					} else {
						repliconName = replicon.select("b").text();
					}
					String[] repliconIDs = replicon.select("a").text().split(" ");
					String repliconID = "";
					boolean validRepliconFound = false;
					for(String rID : repliconIDs) {
						if(rID.startsWith("NC") ||
						   rID.startsWith("MT") ||
						   rID.startsWith("CL") ||
						   rID.startsWith("CH")){
							repliconID = rID;
							validRepliconFound = true;
							break;
						}
					}
					if(validRepliconFound){
						validOrganism = true;
						count ++;
						//System.out.println(" - "+repliconName+" : "+repliconID);
					}
				}
				if(validOrganism){
					oCount ++;
				}
				//System.out.println("Valid : "+validOrganism);
			}
		}
		return true;
	}
	
	@Override
	protected void run() throws Exception {
		System.out.println(this.type.toString()+ " Starting");
		this.readAllPages();
		System.out.println(this.type.toString() +" : "+ this.oCount + "/" + this.count);
		System.out.println(this.type.toString() + "DONE !");
		
	}
}
