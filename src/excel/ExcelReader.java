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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;



import Bdd.Bdd;
import exceptions.CharInvalideException;

//Transforme plusieurs fichiers excels dans les sous dossiers, et les transforme en une Bdd
public class ExcelReader {
	public static Bdd reader(String folder){
		Bdd base = new Bdd("");
		base.open_tampon();
		File currentFolder = new File(folder);
		String[] listeFichiers=currentFolder.list();
		List<String> listeExcels = new ArrayList<String>();
		
		//on récupère dans listeExcels les excels des sous dossiers
		for(int i = 0; i<listeFichiers.length; i++ ){
			File tmp = new File(folder+"/"+listeFichiers[i]);
			if (tmp.isDirectory() == true){
				String[] listeSousFichiers = tmp.list();
				for (int j = 0; j< listeSousFichiers.length; j++ ){
					if (listeSousFichiers[j].endsWith(".xls")) {
						listeExcels.add(folder+"/"+listeFichiers[i]+"/"+listeSousFichiers[j]);
					}
				}
			}
		}
		
		
		for (String fichier:  listeExcels){
			try {
				InputStream ExcelFile = new FileInputStream(fichier);
				HSSFWorkbook wb = new HSSFWorkbook(ExcelFile);
				HSSFSheet sheet=wb.getSheetAt(0);
				List<HSSFRow> rowlist = new ArrayList<HSSFRow>();
				
				for (int i = 0; i <80; i++){
					rowlist.add(sheet.getRow(i));
				}
				
				//on remplit les trinucléotides dans la bdd
				for (int i = 0; i<3; i++){
					for (int j=0; j< 4; j++){
						for (int k=0; k< 4; k++){
							for (int l=0; l< 4; l++){
								int trinucleotide = l+4*k+16*j+7;
								int nuclcourant = (int) rowlist.get(trinucleotide).getCell(1+2*i).getNumericCellValue();
								base.ajoute_nucleotides_mult(i, j, k, l,nuclcourant);
							}
						}
					}
				}
				
				//on remplit les dinucléotides dans la bdd
				for (int i = 0; i<2; i++){
					for (int j=0; j< 4; j++){
						for (int k=0; k< 4; k++){
							int trinucleotide = k+4*j+7;
							int nuclcourant = (int) rowlist.get(trinucleotide).getCell(9+2*i).getNumericCellValue();
							base.ajoute_nucleotides_mult(i, j, k,nuclcourant);	
						}
					}
				}		
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CharInvalideException e) {
				e.printStackTrace();
			}
		}	
		base.close_tampon();
		return base;
	}
}
