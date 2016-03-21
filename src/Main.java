import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import manager.AccessManager;
import excel.*;
import java.util.Scanner;
import Parser.*;
import Bdd.*;

import io.Net;
import exceptions.CharInvalideException;
import exceptions.ScannerNullException;
import ui.UIManager;

public class Main {

	public static void main(String[] args) throws CharInvalideException, ScannerNullException {
		
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
		//Scanner scanneur = Net.getUrl("file:///home/nestor/Cours/2A/bioinformatique/sequence.gb");
		Scanner scanneur = Net.getUrl("file:///home/micka/Bureau/Cours/3/BioInfo/sequence/sequence2.gb");
		Parser parseur = new Parser(base,scanneur);
		parseur.parse();
		
		//affichage du contenus de la bdd
		//System.out.println("nbr_tri "+base.get_nb_trinucleotides());
		//System.out.println("nbr_di "+base.get_nb_dinucleotides());
		//System.out.println("nbr_cds "+base.get_nb_CDS());
		//System.out.println("nbr_cds_nt "+base.get_nb_CDS_non_traites());
		//System.out.println(base.get_tableauxnucleotides_string());
		
		String[] plop = new String[4];
		plop [0] = "kingdom";
		plop [1] = "groupe";
		plop [2] = "subgroup";
		plop [3] = "organisme";
		ExcelWriter.writer("arbo/patate/souspatate/plop2/tortue/tortue.xls",plop, base);
		
		scanneur = Net.getUrl("file:///home/micka/Bureau/Cours/3/BioInfo/sequence/sequence3.gb");
		parseur = new Parser(base,scanneur);
		parseur.parse();
		plop = new String[4];
		plop [0] = "kingdom";
		plop [1] = "groupe";
		plop [2] = "subgroup";
		plop [3] = "organisme";
		ExcelWriter.writer("arbo/patate/souspatate/plop2/tortue2/tortue2.xls",plop, base);
		
		ExcelManager.fusionExcels("arbo");

	}

}
