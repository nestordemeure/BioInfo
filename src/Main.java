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
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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
import Bdd.Bdd.content;
import excel.*;
import io.Net;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.io.Resources;

import Bdd.Bdd;
import Parser.Parser;
import configuration.Configuration;

import tree.*;
import ui.UIManager;
public class Main {
	
	public static void main(String[] args) throws Exception 
	{
		//parsing
		Bdd base = new Bdd();
		//Bdd base = new Bdd("Homo_sapiens");
		
//		String cleft;
//		content contenus;
//		
//		for (Entry<String, content> entry : base.getContenus())
//		{
//			cleft = entry.getKey(); //"mitochondrie", "chloroplaste", "general"
//			
//			contenus = entry.getValue(); //un objet content équipé de toute les fonction que vous appliquiez a la base avant
//			
//			System.out.println(contenus.get_nb_CDS());
//			System.out.println(contenus.get_nb_trinucleotides());
//			System.out.println(contenus.get_tableautrinucleotides(0, 0, 0, 0));
//		}
		
		//mitochondrie et une séquence de type General vide
		Scanner scanneur = Net.getUrl("file:///home/adrien/Bureau/BioInfo/sequence2.gb");
		Parser parseur = new Parser(base,scanneur);
		Organism test=new Organism("Kingdom", "Group", "Subgroup", "Organism", "BP_Project","10/10/2010","11/10/2010");
		parseur.parse("Chromosome_NC_2516354",test,null);
		//chloroplastes
		scanneur = Net.getUrl("file:///home/adrien/Bureau/BioInfo/sequence1.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse("Chromosome_NC_2516355",test,null);
		//chromosome1
		scanneur = Net.getUrl("file:///home/adrien/Bureau/BioInfo/sequence0.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse("Mitochondrion_NC_6846",test,null);
		//chromosome2
		scanneur = Net.getUrl("file:///home/adrien/Bureau/BioInfo/sequence4.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse("Chloroplast_NC_56464",test,null);
		//chromosome2
		scanneur = Net.getUrl("file:///home/adrien/Bureau/BioInfo/sequence4.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse("DNA_NC_skeutuveuu",test,null);
		
		//affichage du contenus de la bdd
		//System.out.println(base.get_tableauxnucleotides_string());
		
		//test l'import/export
		//base.exportBase("adressetest");
		//Bdd base2 = new Bdd("adressetest");
		
		//System.out.println(base2.get_tableauxnucleotides_string());
		
		String[] chemin1=new String[4];
		chemin1[0]="Kingdom";
		chemin1[1]="Groupe";
		chemin1[2]="Sous-Groupe";
		chemin1[3]="Organisme1";
		
		String[] chemin2=new String[4];
		chemin2[0]="Kingdom";
		chemin2[1]="Groupe";
		chemin2[2]="Sous-Groupe";
		chemin2[3]="Organisme2";
		
		ExcelWriter.writer("Results/patate/patate2/sous-grp/plop", chemin1, base,true);
		ExcelWriter.writer("Results/patate/patate2/sous-grp/plop2", chemin2, base,true);
		
		ExcelManager.fusionExcels("Results");
		
	}
}