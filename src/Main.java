import java.util.ArrayList;
import java.util.Scanner;
import io.Net;
import Parser.Parser;

import Bdd.Bdd;
import manager.ThreadManager;
import tree.*;

//115.5 fich/heure

public class Main {
	public static void main(String[] args) throws Exception 
	{
		//parsing
		Bdd base = new Bdd();
		
		//mitochondrie et une séquence de type General vide
		Scanner scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence2.gb");
		Parser parseur = new Parser(base,scanneur);
		parseur.parse();
		//chloroplastes
		scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence1.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse();
		//chromosome1
		scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence0.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse();
		//chromosome2
		scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence4.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse();
		
		//affichage du contenus de la bdd
		//System.out.println(base.get_tableauxnucleotides_string());
		
		//test l'import/export
		base.exportBase("adressetest");
		/*
		Bdd base2 = new Bdd();
		Bdd base3 = new Bdd("adressetest");
		base2.fusionBase(base3);
		System.out.println(base2.get_tableauxnucleotides_string());
		*/
		
		Bdd tmp = new Bdd();
		tmp.fusionBase(new Bdd("adressetest"));
		tmp.fusionBase(new Bdd("adressetest"));
		System.out.println(tmp.get_tableauxnucleotides_string());
		
	}

}
