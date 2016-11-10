package io;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import configuration.Configuration;

import ui.UIManager;


public class IdFetcher {
	public static ArrayList<Integer> getIds(String specie, int minObjects){
		ArrayList<Integer> list = new ArrayList<Integer>();
		int retstart = 0;
		int tries = 0;
		boolean done = false;
		
		while(tries < Configuration.IDS_MAX_TRIES  && !done){
			
			try{
				String url = Configuration.IDS_SEARCH_URL.replaceAll("<TERM>", URLEncoder.encode(specie,"UTF-8"))
						.replaceAll("<PER_PAGE>", String.valueOf(Configuration.IDS_PER_PAGE))
						.replaceAll("<START>", String.valueOf(retstart));
				InputStream is = Net.getUrlIS(url);
				DocumentBuilder builder;
				
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
				Document doc = builder.parse(is);
				is.close();

				int count = Integer.parseInt(doc.getElementsByTagName("Count").item(0).getTextContent());
				if(count <= minObjects){
					return new ArrayList<Integer>();
				}
				
				NodeList nl = doc.getElementsByTagName("Id");
				for(int i = 0 ; i < nl.getLength(); i++){
					int value = Integer.parseInt(nl.item(i).getTextContent());
					list.add(value);
				}
				
				tries = 0;
				
				if(retstart + Configuration.IDS_PER_PAGE < count){
					retstart += Configuration.IDS_PER_PAGE;
				} else {
					done = true;
				}
			}catch(Exception e){
				tries ++;
				if(tries >= Configuration.IDS_MAX_TRIES - 2){
					UIManager.log("[IdFetcher] ERREUR : "+e.getMessage()+" ("+tries+"/"+Configuration.IDS_MAX_TRIES+")");
				}
				if(tries == Configuration.IDS_MAX_TRIES){
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
}
