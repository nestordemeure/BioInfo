import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import manager.AccessManager;
import tree.Tree;
import java.util.Scanner;

import io.Net;

import ui.UIManager;


public class Main {

	public static void main(String[] args) throws Exception {
		
//		Scanner sc = new Scanner(new URL("http://www.ncbi.nlm.nih.gov/genome/browse/").openStream(), "UTF-8").useDelimiter("\n");
//		while(sc.hasNext()){
//			String cur = sc.next();
//			if(cur.startsWith("subgroup_sel_text = ")){
//				cur = cur.split("width:8.5em;\">")[1].split("</SELECT>")[0];
//				for(String str : cur.split("<OPTION")){
//					if(str.split(";\">").length > 1){
//						str = str.split(";\">")[1].split("</OPTION>")[0];
//						if(str.contains("All ") && ! str.equals("All")){
//							int level = str.length() - str.replaceAll("\\-", "").length();
//							str = str.split("All")[1].split("-")[0].trim();
//							System.out.println("GROUP ("+level/2+") : "+str);
//						} else if (! str.equals("All")){
//							System.out.println("Elem : "+ str.trim());
//						}
//					}
//				}
//			}
//		}
		//testAccessManager();
		
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
