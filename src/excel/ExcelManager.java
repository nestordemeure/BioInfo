package excel;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Bdd.Bdd;

public class ExcelManager {
	
	//prends le dossier racine et créé dans chaque sous dossier un fichier excel récapitulatif
	public static void fusionExcels(String folder){
		File currentFolder = new File(folder);
		//Liste des Kingdoms
		String[] listeFichiers=currentFolder.list();
		
		for (int i =0; i< listeFichiers.length;i++){
			//Liste des Groupes
			File lvl1 = new File(folder+"/"+listeFichiers[i]);
			if(lvl1.isDirectory()==true){
				String[] listelvl1=lvl1.list();
				for(int j = 0;j<listelvl1.length;j++){
					
					//Liste des Sous-groupes
					File lvl2 = new File(folder+"/"+listeFichiers[i]+"/"+listelvl1[j]);
					if(lvl2.isDirectory()==true){
						String[] listelvl2=lvl2.list();
						
						for(int k = 0;k<listelvl2.length;k++){
							File lvl3 = new File(folder+"/"+listeFichiers[i]+"/"+listelvl1[j]+"/"+listelvl2[k]);
							if(lvl3.isDirectory()==true){
								String[] chemin = new String[4];
								chemin [0] = listeFichiers[i];
								chemin [1] = listelvl1[j];
								chemin [2] = listelvl2[k];
								chemin [3] = "";
								
								String filepath = folder+"/"+listeFichiers[i]+"/"+listelvl1[j]+"/"+listelvl2[k];
								Bdd base = ExcelReader.reader(filepath);
								ExcelWriter.writer(filepath+"/"+listelvl2[k]+".xls", chemin, base);
							}
							
							
						}
						
						String[] chemin = new String[4];
						chemin [0] = listeFichiers[i];
						chemin [1] = listelvl1[j];
						chemin [2] = "";
						chemin [3] = "";
						
						String filepath = folder+"/"+listeFichiers[i]+"/"+listelvl1[j];
						Bdd base = ExcelReader.reader(filepath);
						ExcelWriter.writer(filepath+"/"+listelvl1[j]+".xls", chemin, base);
					}
				}
				String[] chemin = new String[4];
				chemin [0] = listeFichiers[i];
				chemin [1] = "";
				chemin [2] = "";
				chemin [3] = "";
				
				String filepath = folder+"/"+listeFichiers[i];
				Bdd base = ExcelReader.reader(filepath);
				ExcelWriter.writer(filepath+"/"+listeFichiers[i]+".xls", chemin, base);
			}
		}
		String[] chemin = new String[4];
		chemin [0] = "";
		chemin [1] = "";
		chemin [2] = "";
		chemin [3] = "";
		
		String filepath = folder;
		Bdd base = ExcelReader.reader(filepath);
		ExcelWriter.writer(filepath+"/"+"Recapitulatif"+".xls", chemin, base);
	}
}
