package main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import Bdd.Bdd;
import Parser.Parser;
import configuration.Configuration;
import excel.ExcelWriter;
import io.Net;
import tree.*;
import ui.UIManager;
public class Main {

	// bdd tests
	// TODO the constants in circularcounter have been altered to allow for very short CDS
	public static void main(String[] args) throws Exception 
	{
		String[] inputFiles = {"s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10"};
		Bdd fullDatabase = new Bdd();
		
		//String inputFile = "s1";
		for(String inputFile : inputFiles)
		{
			// init
			Bdd database = new Bdd();
			Scanner fileSource = Net.getUrl("file:///home/nestor/Cours/stage/bioinfo/testFiles/" + inputFile + ".gb");
			Organism testOrganism = new Organism("kingdom","group","subgroup","name","bioproj","creadate","moddate");
			Parser parser = new Parser(database,fileSource);
			
			// parse
			//OutputStream stream = null;
			File file = new File(inputFile + ".txt");
			if (!file.exists()) 
			{
				file.createNewFile();
			}
			OutputStream stream = new FileOutputStream(file);
			parser.parse("testKey",testOrganism,stream);
			database.exportBase("Sums_" + inputFile);
						
			fullDatabase.fusionBase(database);
			
			// display
			System.out.println(database.toString());
			// write excel
			String[] chemin = {"kingdom","group","subgroup",inputFile,"bioproj","creadate","moddate"};
			ExcelWriter.writer(".", inputFile, chemin, database, true);	
		}
		/*
		System.out.println(fullDatabase.toString());
		String[] chemin = {"kingdom","group","subgroup","allSfuse","bioproj","creadate","moddate"};
		ExcelWriter.writer(".", "fullDatabase", chemin, fullDatabase, true);*/	
	}
	
	/*
	public static void main(String[] args) throws Exception {
		
		parseArgs(args);

		UIManager.startPreloading();
		Tree tree = TreeManager.construct();
		UIManager.startMainProcess(tree);
	}
	*/
	
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