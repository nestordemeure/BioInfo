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
import exceptions.ScannerNullException;

import java.util.ArrayList;
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

}
