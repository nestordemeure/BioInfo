package io;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import ui.UIManager;

public class Net {
	public static Scanner getUrl(String url){
		InputStream is = Net.getUrlIS(url);
		if(is == null){
			return null;
		} else {
			return new Scanner(is, "UTF-8").useDelimiter("\n");
		}
	}
	
	public static InputStream getUrlIS(String url){
		int nb_try = 0;
		while(nb_try < 10){
			try {
				return new URL(url).openStream();
			} catch (MalformedURLException e) {
				return null;
			} catch (IOException e) {
				UIManager.log("Error while downloading : "+url+" (Try "+(nb_try + 1)+"/10)");
				nb_try ++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
			}
		}
		return null;
	}
}
