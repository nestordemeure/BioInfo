package excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import Bdd.Bdd;
import configuration.Configuration;
import exceptions.CharInvalideException;

//Transforme plusieurs fichiers bdd dans les sous dossiers, et les transforme en une Bdd
public class ExcelReader {
	public static Bdd reader(String folder,Boolean is_leaf){
		Bdd base = new Bdd();
		File currentFolder = new File(folder);
		String[] listeFichiers = currentFolder.list();
		List<String> listebdd = new ArrayList<String>();

		if (!is_leaf)
		{
			//on récupère dans listebdd les bdd des sous dossiers
			for (String fichier : listeFichiers)
			{
				File tmp = new File(folder + Configuration.FOLDER_SEPARATOR + fichier);
				if (tmp.isDirectory())
				{
					String[] listeSousFichiers = tmp.list();
					for (String sousFichier : listeSousFichiers)
					{
						if (sousFichier.equalsIgnoreCase("Sums.bdd"))
						{
							listebdd.add(folder + Configuration.FOLDER_SEPARATOR + fichier + Configuration.FOLDER_SEPARATOR + sousFichier);
						}

					}
				}
			}
		}
		else
		{
			for (String fichier : listeFichiers)
			{
				if (!fichier.equalsIgnoreCase("Sums.bdd") && fichier.endsWith(".bdd") && fichier.startsWith("Sums"))
				{
					listebdd.add(folder + Configuration.FOLDER_SEPARATOR + fichier);
				}
			}
		}
		
		
		Bdd tmp;
		for (String fichier : listebdd)
		{
			try 
			{
				tmp = new Bdd(fichier.substring(0,fichier.length()-4));
				base.fusionBase(tmp);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}	
		return base;
	}
}
