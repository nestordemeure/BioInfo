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
		//Scanner scanneur = Net.getUrl("http://jonathancrabtree.github.io/Circleator/tutorials/gb_annotation/L42023.1.gb");
		Scanner scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence.gb");
		Parser parseur = new Parser(base,scanneur);
		parseur.parse(2);
		
		base.exportBase("adressetest");
		
		//affichage du contenus de la bdd
		System.out.println("nbr_tri "+base.get_nb_trinucleotides());
		System.out.println("nbr_di "+base.get_nb_dinucleotides());
		System.out.println("nbr_cds "+base.get_nb_CDS());
		System.out.println("nbr_cds_nt "+base.get_nb_CDS_non_traites());
		System.out.println(base.get_tableauxnucleotides_string());
		
		Bdd base2 = new Bdd("adressetest");
		
		System.out.println("nbr_tri "+base2.get_nb_trinucleotides());
		System.out.println("nbr_di "+base2.get_nb_dinucleotides());
		System.out.println("nbr_cds "+base2.get_nb_CDS());
		System.out.println("nbr_cds_nt "+base2.get_nb_CDS_non_traites());
		System.out.println(base2.get_tableauxnucleotides_string());
		
	}

}
