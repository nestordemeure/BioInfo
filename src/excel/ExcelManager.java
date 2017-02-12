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
	
	public static HashMap<String, Long> getInfo(String info) throws IOException{
		
		HashMap<String, Long> res = new HashMap<String, Long>();
		Bdd tmp = new Bdd(info);
		
		String cleft;
		content contenus;
		
		Long nb_CDS=(long) 0;
		Long CDS_non_traites=(long) 0;
				
		for (Entry<String, content> entry : tmp.getContenus())
		{
			cleft = entry.getKey(); //"mitochondire", "chloroplaste", "general"
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
		
		for (int i =0; i< listeFichiers.length;i++){
			//Liste des Groupes
			File lvl1 = new File(folder+Configuration.FOLDER_SEPARATOR+listeFichiers[i]);
			if(lvl1.isDirectory()==true){
				String[] listelvl1=lvl1.list();
				for(int j = 0;j<listelvl1.length;j++){
					
					//Liste des Sous-groupes
					File lvl2 = new File(folder+Configuration.FOLDER_SEPARATOR+listeFichiers[i]+Configuration.FOLDER_SEPARATOR+listelvl1[j]);
					if(lvl2.isDirectory()==true){
						String[] listelvl2=lvl2.list();
						
						for(int k = 0;k<listelvl2.length;k++){
							File lvl3 = new File(folder+Configuration.FOLDER_SEPARATOR+listeFichiers[i]+Configuration.FOLDER_SEPARATOR+listelvl1[j]+Configuration.FOLDER_SEPARATOR+listelvl2[k]);
							if(lvl3.isDirectory()==true){
								String[] chemin = new String[4];
								chemin [0] = listeFichiers[i];
								chemin [1] = listelvl1[j];
								chemin [2] = listelvl2[k];
								chemin [3] = "";
								
								String filepath = folder+Configuration.FOLDER_SEPARATOR+listeFichiers[i]+Configuration.FOLDER_SEPARATOR+listelvl1[j]+Configuration.FOLDER_SEPARATOR+listelvl2[k];
								//System.out.println(filepath);
								Bdd base = ExcelReader.reader(filepath,true);
								ExcelWriter.writer(filepath,filepath+Configuration.FOLDER_SEPARATOR+listelvl2[k], chemin, base, false);
							}
							
							
						}
						
						String[] chemin = new String[4];
						chemin [0] = listeFichiers[i];
						chemin [1] = listelvl1[j];
						chemin [2] = "";
						chemin [3] = "";
						
						String filepath = folder+Configuration.FOLDER_SEPARATOR+listeFichiers[i]+Configuration.FOLDER_SEPARATOR+listelvl1[j];
						Bdd base = ExcelReader.reader(filepath,false);
						ExcelWriter.writer(filepath,filepath+Configuration.FOLDER_SEPARATOR+listelvl1[j], chemin, base, false);
					}
				}
				String[] chemin = new String[4];
				chemin [0] = listeFichiers[i];
				chemin [1] = "";
				chemin [2] = "";
				chemin [3] = "";
				
				String filepath = folder+Configuration.FOLDER_SEPARATOR+listeFichiers[i];
				Bdd base = ExcelReader.reader(filepath,false);
				ExcelWriter.writer(filepath,filepath+Configuration.FOLDER_SEPARATOR+listeFichiers[i], chemin, base, false);
			}
		}
		String[] chemin = new String[4];
		chemin [0] = "Récapitulatif";
		chemin [1] = "";
		chemin [2] = "";
		chemin [3] = "";
		
		String filepath = folder;
		Bdd base = ExcelReader.reader(filepath,false);
		ExcelWriter.writer(filepath,filepath+Configuration.FOLDER_SEPARATOR+"Recapitulatif", chemin, base, false);
	}
}
