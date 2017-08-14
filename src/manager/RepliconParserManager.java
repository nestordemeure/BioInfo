package manager;

import java.io.*;
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

public class RepliconParserManager
{
	private Retryer<Bdd> retryer;
	
	private Organism organism;
	private String replicon;
	private Bdd mainDb;

	// used to pass an exception from a runnable
	private volatile Exception storedException;

	public Callable<Bdd> parser = new Callable<Bdd>()
	{
		public Bdd call() throws Exception
		{
			Bdd db = new Bdd();
			storedException = null;

            TimeLimitedCodeBlock.runWithTimeout(new Runnable() { @Override public void run()
            {
                try
                {
                    String url = Configuration.GEN_DOWNLOAD_URL.replaceAll("<ID>", organism.getReplicons().get(replicon));
                    /*
                    Scanner sc = new Scanner(Resources.asByteSource(new URL(url)).openBufferedStream()); // produces UnknownHostException
                    sc.useDelimiter("\n");
                    */
                    UIManager.log("[Parser caller] getting stream for "+ organism.getName() + " replicon: " + replicon);
                    InputStream stream = new URL(url).openStream();

                    UIManager.log("[Parser caller] getting scanner for "+ organism.getName() + " replicon: " + replicon);
                    Scanner sc = new Scanner(stream).useDelimiter("\n");

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
                }
                catch (Exception e)
                {
                    storedException = e;
                }
            }}, Configuration.NET_MINUTES_BEFORE_TIMEOUT, TimeUnit.MINUTES);

            UIManager.log("[Parser caller] Finished with "+ organism.getName() + " replicon: " + replicon);
            if (storedException != null)
            {
                throw storedException;
            }
            else
            {
                return db;
            }
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
		catch (Exception e)
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
