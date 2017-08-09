package excel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Bdd.Bdd;
import Bdd.Bdd.content;
import configuration.Configuration;

public class ExcelManager {
	
	public static HashMap<String, Long> getInfo(String info) throws IOException
	{
		HashMap<String, Long> res = new HashMap<>();
		Bdd tmp = new Bdd(info);
		
		content contenus;
		Long nb_CDS=(long) 0;
		Long CDS_non_traites=(long) 0;
				
		for (Entry<String, content> entry : tmp.getContenus())
		{
			// String cleft = entry.getKey(); //"mitochondire", "chloroplaste", "general"
			contenus = entry.getValue(); //un objet content équipé de toute les fonction que vous appliquiez a la base avant
			
			nb_CDS+=contenus.nb_CDS;
			CDS_non_traites+=contenus.nb_CDS_non_traites;
		}
		
		res.put("nb_cds", nb_CDS);
		res.put("cds_non_traites",CDS_non_traites);
		
		return res;
	}
	
	//prends le dossier racine et créé dans chaque sous dossier un fichier excel récapitulatif
	public static void fusionExcels(String folder){
		
		File currentFolder = new File(folder);
		//Liste des Kingdoms
		String[] listeFichiers=currentFolder.list();

		for (String listeFichier : listeFichiers)
		{
			//Liste des Groupes
			File lvl1 = new File(folder + Configuration.FOLDER_SEPARATOR + listeFichier);
			if (lvl1.isDirectory())
			{
				String[] listelvl1 = lvl1.list();
				for (String aListelvl1 : listelvl1)
				{
					//Liste des Sous-groupes
					File lvl2 = new File(folder + Configuration.FOLDER_SEPARATOR + listeFichier + Configuration.FOLDER_SEPARATOR + aListelvl1);
					if (lvl2.isDirectory()) {
						String[] listelvl2 = lvl2.list();

						for (String aListelvl2 : listelvl2)
						{
							File lvl3 = new File(folder + Configuration.FOLDER_SEPARATOR + listeFichier + Configuration.FOLDER_SEPARATOR + aListelvl1 + Configuration.FOLDER_SEPARATOR + aListelvl2);
							if (lvl3.isDirectory())
							{
								String[] chemin = new String[4];
								chemin[0] = listeFichier;
								chemin[1] = aListelvl1;
								chemin[2] = aListelvl2;
								chemin[3] = "";

								String filepath = folder + Configuration.FOLDER_SEPARATOR + listeFichier + Configuration.FOLDER_SEPARATOR + aListelvl1 + Configuration.FOLDER_SEPARATOR + aListelvl2;
								//System.out.println(filepath);
								Bdd base = ExcelReader.reader(filepath, true);
								ExcelWriter.writer(filepath, filepath + Configuration.FOLDER_SEPARATOR + aListelvl2, chemin, base, false);
							}
						}

						String[] chemin = new String[4];
						chemin[0] = listeFichier;
						chemin[1] = aListelvl1;
						chemin[2] = "";
						chemin[3] = "";

						String filepath = folder + Configuration.FOLDER_SEPARATOR + listeFichier + Configuration.FOLDER_SEPARATOR + aListelvl1;
						Bdd base = ExcelReader.reader(filepath, false);
						ExcelWriter.writer(filepath, filepath + Configuration.FOLDER_SEPARATOR + aListelvl1, chemin, base, false);
					}
				}
				String[] chemin = new String[4];
				chemin[0] = listeFichier;
				chemin[1] = "";
				chemin[2] = "";
				chemin[3] = "";

				String filepath = folder + Configuration.FOLDER_SEPARATOR + listeFichier;
				Bdd base = ExcelReader.reader(filepath, false);
				ExcelWriter.writer(filepath, filepath + Configuration.FOLDER_SEPARATOR + listeFichier, chemin, base, false);
			}
		}
		String[] chemin = new String[4];
		chemin [0] = "Récapitulatif";
		chemin [1] = "";
		chemin [2] = "";
		chemin [3] = "";
		
		Bdd base = ExcelReader.reader(folder,false);
		ExcelWriter.writer(folder,folder + Configuration.FOLDER_SEPARATOR + "Recapitulatif", chemin, base, false);
	}
}
