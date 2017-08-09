package manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.io.Resources;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

import Bdd.Bdd;
import Parser.Parser;
import configuration.Configuration;
import exceptions.ScannerNullException;
import tree.Organism;
import ui.UIManager;

public class RepliconParserManager {

	private Retryer<Bdd> retryer;
	
	private Organism organism;
	private String replicon;
	private Bdd mainDb;
	
	public Callable<Bdd> parser = new Callable<Bdd>()
	{
		public Bdd call() throws IOException, ScannerNullException
		{
			Bdd db = new Bdd();
			/*try
			{*/
			UIManager.log("[Parser caller] getting scanner and bdd for "+ organism.getName() + " replicon: " + replicon);
			String url = Configuration.GEN_DOWNLOAD_URL.replaceAll("<ID>", organism.getReplicons().get(replicon));
			Scanner sc = new Scanner(Resources.asByteSource(new URL(url)).openBufferedStream()); // TODO produces UnknownHostException
			sc.useDelimiter("\n");
			Parser parser = new Parser(db, sc);

			UIManager.log("[Parser caller] Starting to parse "+ organism.getName() + " replicon: " + replicon);
			if (Configuration.STORE_DATA)
			{
				File f = new File(organism.getPath());
				if (f.isDirectory())
				{
					parser.parse(replicon, organism, new FileOutputStream(organism.getPath()+Configuration.FOLDER_SEPARATOR+organism.getName()+"_"+replicon));
				}
				else
				{
					parser.parse(replicon, organism, null);
				}
			}
			else
			{
				parser.parse(replicon, organism, null);
			}
			UIManager.log("[Parser caller] Finished parsing "+ organism.getName() + " replicon: " + replicon);

			/*}
			catch(Exception e)
			{
				UIManager.log("[Parser caller] Unable to call "+ organism.getName() + " replicon: " + replicon);
			}*/

			return db;
		}
	};
	
	public RepliconParserManager(Organism o, String r, Bdd db)
	{
		this.organism = o;
		this.replicon = r;
		this.mainDb = db;
		
		this.retryer = RetryerBuilder.<Bdd>newBuilder()
				.retryIfException()
				.retryIfRuntimeException()
				.withWaitStrategy(WaitStrategies.fibonacciWait())
				.withStopStrategy(StopStrategies.stopAfterAttempt(Configuration.NET_MAX_DOWNLOAD_TRIES))
				//.withStopStrategy(StopStrategies.stopAfterDelay(Configuration.NET_MAX_DOWNLOAD_TIME, TimeUnit.MINUTES ) )
				//.withStopStrategy(new combinedStopStrategy(Configuration.NET_MAX_DOWNLOAD_TRIES, Configuration.NET_MAX_DOWNLOAD_TIME, TimeUnit.MINUTES) )
				.build();
	}
	
	public void run() 
	{
		UIManager.log("[RepliconParserManager] Start downloading "+this.organism.getName()+ " replicon: "+replicon);

		Bdd db = null;
		try 
		{
			db = retryer.call(this.parser);
		}
		catch (/*ExecutionException | RetryException*/ Exception e) // TODO modified to catch UnknownHostException
		{
			UIManager.log("[RepliconParserManager] Unable to download "+this.organism.getName()+ " replicon: "+replicon);
		}

		if(db == null)
		{
			UIManager.log("[RepliconParserManager] Unable to download "+this.organism.getName()+ " replicon: "+replicon);
		}
		else
		{
			this.mainDb.fusionBase(db);
			UIManager.log("[RepliconParserManager] Finished downloading "+this.organism.getName()+ " replicon: "+replicon);
		}

		organism.addProcessedReplicon(replicon);
		UIManager.addProgress(1);
	}
}
