package excel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Bdd.Bdd;
import configuration.Configuration;

public class ExcelManager {
	
	public static HashMap<String, Long> getInfo(String info) throws IOException{
		HashMap<String, Long> res = new HashMap<String, Long>();
		Bdd tmp = new Bdd(info);
		
		res.put("nb_cds", tmp.get_nb_CDS());
		res.put("cds_non_traites",tmp.get_nb_CDS_non_traites());
		res.put("nb_dinucleotides",tmp.get_nb_dinucleotides());
		res.put("nb_trinucleotides",tmp.get_nb_trinucleotides());
		
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
								Bdd base = ExcelReader.reader(filepath);
								ExcelWriter.writer(filepath+Configuration.FOLDER_SEPARATOR+listelvl2[k], chemin, base);
							}
							
							
						}
						
						String[] chemin = new String[4];
						chemin [0] = listeFichiers[i];
						chemin [1] = listelvl1[j];
						chemin [2] = "";
						chemin [3] = "";
						
						String filepath = folder+Configuration.FOLDER_SEPARATOR+listeFichiers[i]+Configuration.FOLDER_SEPARATOR+listelvl1[j];
						Bdd base = ExcelReader.reader(filepath);
						ExcelWriter.writer(filepath+Configuration.FOLDER_SEPARATOR+listelvl1[j], chemin, base);
					}
				}
				String[] chemin = new String[4];
				chemin [0] = listeFichiers[i];
				chemin [1] = "";
				chemin [2] = "";
				chemin [3] = "";
				
				String filepath = folder+Configuration.FOLDER_SEPARATOR+listeFichiers[i];
				Bdd base = ExcelReader.reader(filepath);
				ExcelWriter.writer(filepath+Configuration.FOLDER_SEPARATOR+listeFichiers[i], chemin, base);
			}
		}
		String[] chemin = new String[4];
		chemin [0] = "Récapitulatif";
		chemin [1] = "";
		chemin [2] = "";
		chemin [3] = "";
		
		String filepath = folder;
		Bdd base = ExcelReader.reader(filepath);
		ExcelWriter.writer(filepath+Configuration.FOLDER_SEPARATOR+"Recapitulatif", chemin, base);
	}
}
