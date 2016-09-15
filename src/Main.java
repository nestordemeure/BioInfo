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
	
	public static void main(String[] args) throws Exception 
	{
		//parsing
		Bdd base = new Bdd();
		
		//mitochondrie et une séquence de type General vide
		Scanner scanneur = Net.getUrl("file:///home/micka/Bureau/BioInfo/sequence2.gb");
		Parser parseur = new Parser(base,scanneur);
		parseur.parse();
		//chloroplastes
		scanneur = Net.getUrl("file:///home/micka/Bureau/BioInfo/sequence1.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse();
		//chromosome1
		scanneur = Net.getUrl("file:///home/micka/Bureau/BioInfo/sequence0.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse();
		//chromosome2
		scanneur = Net.getUrl("file:///home/micka/Bureau/BioInfo/sequence4.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse();
		
		//affichage du contenus de la bdd
		//System.out.println(base.get_tableauxnucleotides_string());
		
		//test l'import/export
		//base.exportBase("adressetest");
		//Bdd base2 = new Bdd("adressetest");
		
		//System.out.println(base2.get_tableauxnucleotides_string());
		
		String[] chemin=new String[4];
		chemin[0]="Kingdom";
		chemin[1]="Groupe";
		chemin[2]="Sous-Groupe";
		chemin[3]="Organisme";
		
		ExcelWriter.writer("Results/patate/patate2/sous-grp/plop/plop", chemin, base);
		//ExcelWriter.writer("Results/patate/patate2/sous-grp/plop2/plop2", chemin, base);
		
		//ExcelManager.fusionExcels("Results");
		
	}

}
