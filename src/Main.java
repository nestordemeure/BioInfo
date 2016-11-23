import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.io.Resources;

import configuration.Configuration;


import manager.AccessManager;
import Parser.*;
import Bdd.Bdd;

import excel.*;
import io.Net;
import manager.ThreadManager;
import tree.*;
import ui.UIManager;

public class Main {

	public static void main(String[] args) throws Exception {
		
		Callable<Boolean> callable =  new Callable<Boolean>(){
			public Boolean call() throws Exception{
				//Scanner sc = new Scanner(Resources.asByteSource(new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=NC_011594.1&rettype=gb")).openBufferedStream());
				
				URL url = new File("/home/nestor/Cours/2A/bioinformatique/sequence2.gb").toURI().toURL();
				Scanner sc = new Scanner(Resources.asByteSource(url).openBufferedStream());
				sc.useDelimiter("\n");
				Bdd db = new Bdd();
				Parser p = new Parser(db, sc);
				ByteArrayOutputStream stream = new ByteArrayOutputStream(); //TODO
				p.parse("lol",stream); //TODO
				System.out.println(stream.toString());
				db.exportBase("/tmp/lol2");
				System.out.println("********");
				System.out.println(db.get_tableauxnucleotides_string());
				System.out.println(db.getContenus().iterator().hasNext());
				long a = db.getContenus().iterator().next().getValue().get_nb_CDS();
				System.out.println(a);
				return true;
				
			}
		};
		
		Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
		        .retryIfExceptionOfType(IOException.class)
		        .retryIfRuntimeException()
		        .withWaitStrategy(WaitStrategies.fibonacciWait(100, 2, TimeUnit.MINUTES))
		        .withStopStrategy(StopStrategies.stopAfterAttempt(2))
		        .build();
		retryer.call(callable);
		
//		parseArgs(args);
//
//		UIManager.startPreloading();
//		Tree plop = new Tree();
//		plop=TreeManager.constree();
//		UIManager.startMainProcess(plop);
	}
	
	public static void parseArgs(String[] args){
		Option help = new Option("h","help",false, "print this message");
		Option nogui = new Option("g", "no-gui",false, "Text mode only");
		Option path = new Option("p","path", true, "Path to the ressources");
		Option windows = new Option("w", "windows", false, "Use windows style path");
		Option threads = new Option("t", "threads", true, "Specify how many threads will be launched");
		
		Options options = new Options();
		options.addOption(help);
		options.addOption(nogui);
		options.addOption(path);
		options.addOption(windows);
		options.addOption(threads);
		
		CommandLineParser parser = new DefaultParser();
		CommandLine line = null;
		boolean showHelp = false;
		try{
			line = parser.parse(options, args);
		}catch(ParseException exp){
			showHelp = true;
		}
		
		if(! showHelp && line.hasOption("help")){
			showHelp = true;
		}
		
		if(showHelp){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("BioInfo", options );
			System.exit(1);
		}
		
		if(line.hasOption("no-gui")){
			Configuration.USE_GUI = false;
		}
		
		if(line.hasOption("path")){
			if(line.getOptionValue("path") != null){
				Configuration.BASE_FOLDER = line.getOptionValue("path");
			}
		}
		
		if(line.hasOption("threads")){
			int a = Integer.parseInt(line.getOptionValue("threads"));
			if(a > 0){
				Configuration.THREADS_NUMBER = a;
			}
		}
		
		if(line.hasOption("windows")){
			Configuration.FOLDER_SEPARATOR = "\\";
		}
		
	}
}