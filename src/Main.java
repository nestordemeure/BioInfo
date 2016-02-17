import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import manager.AccessManager;
import tree.*;
import java.util.Scanner;

import io.Net;

import ui.UIManager;


public class Main {

	public static void main(String[] args) throws Exception {
		
		Tree plop = new Tree();
		plop=TreeManager.constree();
		
		Tree<Tree> tree = new Tree<Tree>();
		
		if(! tree.contains("eukaryota")){
			tree.add("eukaryota", new Tree<Tree>());
		}
		
		Tree cur = tree.get("eukaryota");
		
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

}
