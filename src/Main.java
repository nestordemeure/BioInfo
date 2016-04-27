import java.util.ArrayList;
import java.util.Scanner;
import io.Net;
import Parser.Parser;

import Bdd.Bdd;
import manager.ThreadManager;
import tree.*;

//115.5 fich/heure

public class Main {
	public static void main(String[] args) throws Exception {
		/*
		Tree plop = new Tree();
		plop=TreeManager.constree();
		
		ThreadManager.start(plop, new ArrayList<String>());
		*/
		
		//parsing
		Bdd base = new Bdd();
		
		//mitochondrie
		Scanner scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence.gb");
		Parser parseur = new Parser(base,scanneur);
		parseur.parse();
		//et une séquence de type General vide
		
		//chloroplastes
		scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence1.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse();
		
		//chromosome="truc"
		scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence0.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse();
		
		//TODO rendre les objets cnotent serializables

		//affichage du contenus de la bdd
		System.out.println(base.get_tableauxnucleotides_string());
		
		Bdd base2 = new Bdd("adressetest");
		
		System.out.println(base2.get_tableauxnucleotides_string());
		
	}

}
