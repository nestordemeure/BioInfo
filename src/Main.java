import Bdd.Bdd;
import Parser.Parser;
import exceptions.CharInvalideException;
import exceptions.ScannerNullException;

import java.util.ArrayList;
import java.util.Scanner;
import io.Net;
import ui.UIManager;

public class Main {
	public static void main(String[] args) throws CharInvalideException, ScannerNullException {
		
		/*
		//hello world
		UIManager.log("Hello World");
		UIManager.setProgress(0.);
		UIManager.setProgress(50.2);
		UIManager.setProgress(100);
		UIManager.log("DONE !");
		*/
		
		/*
		//exemple d'utilisation du scanneur
		Scanner sc = Net.getUrl("http://jonathancrabtree.github.io/Circleator/tutorials/gb_annotation/L42023.1.gb");
		while(sc.hasNext())
		{
			UIManager.log(sc.next());
		}
		*/

		//parsing
		Bdd base = new Bdd("path");
		//Scanner scanneur = Net.getUrl("http://jonathancrabtree.github.io/Circleator/tutorials/gb_annotation/L42023.1.gb");
		Scanner scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence.gb");
		Parser parseur = new Parser(base,scanneur);
		parseur.parse(2);
		
		//affichage du contenus de la bdd
		System.out.println("nbr_tri "+base.get_nb_trinucleotides());
		System.out.println("nbr_di "+base.get_nb_dinucleotides());
		System.out.println("nbr_cds "+base.get_nb_CDS());
		System.out.println("nbr_cds_nt "+base.get_nb_CDS_non_traites());
		System.out.println(base.get_tableauxnucleotides_string());
		
	}

}
