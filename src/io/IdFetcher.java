package io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ui.UIManager;


public class IdFetcher {
	private static int PER_PAGE = 100;
	private static int MAX_TRY = 10;
	private static String SEARCH_URL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=nuccore&retmax=<PER_PAGE>&term=<TERM>[Organism]&retstart=<START>";
	public static ArrayList<Integer> getIds(String specie){
		ArrayList<Integer> list = new ArrayList<Integer>();
		int retstart = 0;
		int tries = 0;
		boolean done = false;
		
		while(tries < MAX_TRY  && !done){
			
			try{
				String url = IdFetcher.SEARCH_URL.replaceAll("<TERM>", URLEncoder.encode(specie,"UTF-8")).replaceAll("<PER_PAGE>", String.valueOf(PER_PAGE)).replaceAll("<START>", String.valueOf(retstart));
				InputStream is = Net.getUrlIS(url);
				DocumentBuilder builder;
				
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
				Document doc = builder.parse(is);

				int count = Integer.parseInt(doc.getElementsByTagName("Count").item(0).getTextContent());
				
				NodeList nl = doc.getElementsByTagName("Id");
				for(int i = 0 ; i < nl.getLength(); i++){
					int value = Integer.parseInt(nl.item(i).getTextContent());
					if(! list.contains(value)){
						list.add(value);
					}
				}
				if(retstart + IdFetcher.PER_PAGE < count){
					retstart += IdFetcher.PER_PAGE;
				} else {
					done = true;
				}
			}catch(Exception e){
				tries ++;
				UIManager.log("[IdFetcher] ERREUR : "+e.getMessage());
				e.printStackTrace();
			}
		}
		
		return list;
	}
}
