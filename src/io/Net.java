package io;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import configuration.Configuration;

import ui.UIManager;

public class Net {
	
	@SuppressWarnings("resource")
	public static Scanner getUrl(String url){
		InputStream is = Net.getUrlIS(url);
		if(is == null){
			return null;
		} else {
			return new Scanner(is, "UTF-8").useDelimiter("\n");
		}
	}
	
	public static InputStream getUrlIS(String url)
	{
		int nb_try = 0;
		while(nb_try < Configuration.NET_MAX_DOWNLOAD_TRIES)
		{
			try
			{
				return new URL(url).openStream();
			}
			catch (MalformedURLException e)
			{
				return null;
			}
			catch (IOException e)
			{
				if(nb_try >= Configuration.NET_MAX_DOWNLOAD_TRIES - 2)
				{
					UIManager.log("Error while downloading : "+url+" (Try "+(nb_try + 1)+"/"+Configuration.NET_MAX_DOWNLOAD_TRIES+")");
				}
				nb_try ++;
				if(nb_try == Configuration.NET_MAX_DOWNLOAD_TRIES)
				{
					e.printStackTrace();
				}
				try
				{
					long sleep_time = (long)Math.floor(Math.random() * Configuration.NET_TIME_BETWEEN_TRIES);
					Thread.sleep(sleep_time);
				}
				catch (InterruptedException e1) {}
			}
		}
		return null;
	}
}
