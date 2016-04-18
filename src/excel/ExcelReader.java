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

//Transforme plusieurs fichiers excels dans les sous dossiers, et les transforme en une Bdd
public class ExcelReader {
	public static Bdd reader(String folder){
		Bdd base = new Bdd();
		File currentFolder = new File(folder);
		String[] listeFichiers=currentFolder.list();
		List<String> listebdd = new ArrayList<String>();
		
		//on récupère dans listebdd les bdd des sous dossiers
		for(int i = 0; i<listeFichiers.length; i++ ){
			File tmp = new File(folder+Configuration.FOLDER_SEPARATOR+listeFichiers[i]);
			if (tmp.isDirectory() == true){
				String[] listeSousFichiers = tmp.list();
				for (int j = 0; j< listeSousFichiers.length; j++ ){
					if (listeSousFichiers[j].endsWith(".bdd")) {
						listebdd.add(folder+Configuration.FOLDER_SEPARATOR+listeFichiers[i]+Configuration.FOLDER_SEPARATOR+listeSousFichiers[j]);
					}
				}
			}
		}
		
		
		Bdd tmp;
		for (String fichier:  listebdd){
			try {
				
				tmp = new Bdd(fichier.substring(0,fichier.length()-4));
				base.fusionBase(tmp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		return base;
	}
}
