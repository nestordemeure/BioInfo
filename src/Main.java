import java.util.ArrayList;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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
		
		
		//parsing
		Bdd base = new Bdd();
		//Scanner scanneur = Net.getUrl("http://jonathancrabtree.github.io/Circleator/tutorials/gb_annotation/L42023.1.gb");
		//Scanner scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence2.gb");
		Scanner scanneur = Net.getUrl("file:///home/micka/Bureau/Cours/3/BioInfo/sequence/sequence2.gb");
		Parser parseur = new Parser(base,scanneur);

		parseur.parse();
				
		
		String[] plop = new String[4];
		plop [0] = "kingdom";
		plop [1] = "groupe";
		plop [2] = "subgroup";
		plop [3] = "organisme";
		ExcelWriter.writer("arbo/patate/souspatate/plop2/tortue/tortue",plop, base);
		

		scanneur = Net.getUrl("file:///home/micka/Bureau/Cours/3/BioInfo/sequence/sequence3.gb");
		//scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence3.gb");

		parseur = new Parser(base,scanneur);
		parseur.parse();
		plop = new String[4];
		plop [0] = "kingdom";
		plop [1] = "groupe";
		plop [2] = "subgroup";
		plop [3] = "organisme";
		ExcelWriter.writer("arbo/patate/souspatate/plop2/tortue2/tortue2",plop, base);
		
		ExcelManager.fusionExcels("arbo");
		
		/*
		parseArgs(args);

		UIManager.startPreloading();
		Tree plop = new Tree();
		plop=TreeManager.constree();
		UIManager.startMainProcess(plop);*/
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
