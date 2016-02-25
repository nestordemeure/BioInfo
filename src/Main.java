import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Scanner;

import manager.AccessManager;
import manager.ThreadManager;
import tree.*;
import Bdd.Bdd;
import Parser.Parser;
import exceptions.CharInvalideException;
import java.util.Scanner;
import io.Net;

import ui.UIManager;

public class Main {

	public static void main(String[] args) throws Exception {
		
		Tree plop = new Tree();
		plop=TreeManager.constree();
		
		ThreadManager.start(plop, new ArrayList<String>());
		
//		Tree<Tree> tree = new Tree<Tree>();
//		
//		if(! tree.contains("eukaryota")){
//			tree.add("eukaryota", new Tree<Tree>());
//		}
//		
//		Tree cur = tree.get("eukaryota");
		
	}
	
	public static void testAccessManager(){
		(new Thread() {
			public void run() {
				AccessManager.accessFile("test.txt");
				UIManager.log("TH1 : Lock");

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				AccessManager.doneWithFile("test.txt");
				UIManager.log("TH1 : Unlock");
			}
		}).start();
		(new Thread() {
			public void run() {
				AccessManager.accessFile("test2.txt");
				UIManager.log("TH2 : Lock");

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				AccessManager.doneWithFile("test2.txt");
				UIManager.log("TH2 : Unlock");

			}
		}).start();
	}

	public static void main2(String[] args) throws CharInvalideException {
		
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
		Scanner scanneur = Net.getUrl("http://jonathancrabtree.github.io/Circleator/tutorials/gb_annotation/L42023.1.gb");
		Parser parseur = new Parser(base,scanneur);
		parseur.parse();
		
		//affichage du contenus de la bdd
		System.out.println("nbr_tri "+base.get_nb_trinucleotides());
		System.out.println("nbr_cds "+base.get_nb_CDS());
		System.out.println("nbr_cds_nt "+base.get_nb_CDS_non_traites());
		System.out.println(base.get_tableautrinucleotides_string());
	}

}
