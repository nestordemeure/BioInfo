package io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import ui.UIManager;

public class Net {
	public static Scanner getUrl(String url){
		int nb_try = 0;
		while(nb_try < 10){
			try {
				return new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\n");
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
