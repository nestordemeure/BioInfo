package excel;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;

import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

import exceptions.CharInvalideException;
import tree.Organism;
import Parser.*;
import configuration.Configuration;
import Bdd.*;
import Bdd.Bdd.content;
import configuration.Configuration;


public class ExcelWriter {
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	
	
	public static void writer(String folderpath, String filepath, String[] chemin, Bdd base, boolean is_leaf) {
		try {
			
			//Boolean is_leaf = filepath.length() - filepath.replace(Configuration.FOLDER_SEPARATOR, "").length()==4;
			//Boolean is_leaf=!chemin[3].equals("");
			
//			Pattern regex1 = Pattern.compile(".*"+Configuration.FOLDER_SEPARATOR);
//			Matcher m = regex1.matcher(filepath);
//			String folderpath ="";
//			if (m.find()){
//				folderpath = m.group(0);
//				File folders = new File(folderpath);
//				folders.mkdirs();
//			}
			
			String xlsfile = filepath+".xlsx";
			
			FileOutputStream fileOut = new FileOutputStream(xlsfile);
			Workbook workbook = new XSSFWorkbook();
						

			String cleft;
			content contenus;
			Bdd baseSum = new Bdd();
			
			for (Entry<String, content> entry : base.getContenus())
			{
				cleft = entry.getKey(); //"mitochondrie", "chloroplaste", "general"
				
				contenus = entry.getValue(); //un objet content équipé de toute les fonction que vous appliquiez a la base avant
				
				//System.out.println(":"+cleft+":");
				
				if (!cleft.equals("")){
					writeTab(cleft, contenus, baseSum, workbook, chemin);
				}
			}
			
			if (is_leaf){
				baseSum.exportBase(folderpath+Configuration.FOLDER_SEPARATOR+"Sums_"+chemin[3]);
				
				Bdd empty = new Bdd();
				
				for (Entry<String, content> entry : baseSum.getContenus())
				{
					cleft = entry.getKey(); //"Sum_Chromosomes", "Sum..."
					
					contenus = entry.getValue();
					
					//System.out.println(":"+cleft+":");
					
					if (!cleft.equals("")){
						writeTab(cleft, contenus, empty, workbook, chemin);
					}
				}
			}
			
			
			
			workbook.write(fileOut);
			workbook.close();
			fileOut.flush();
			fileOut.close();

			if (!is_leaf){
				base.exportBase(folderpath+Configuration.FOLDER_SEPARATOR+"Sums");
			}
			else{
				base.exportBase(filepath);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void writeTab(String cleft, content contenus, Bdd baseSum, Workbook wb, String[] chemin){
		
		try {
			
			XSSFDataFormat dataFormat = (XSSFDataFormat) wb.createDataFormat();
			
			byte[] LIGHT_BLUE = hexStringToByteArray("a7c8fd");
			XSSFColor light_blue = new XSSFColor(LIGHT_BLUE);
			
			byte[] DARK_BLUE = hexStringToByteArray("3686ca");
			XSSFColor dark_blue = new XSSFColor(DARK_BLUE);
			
			byte[] LIGHT_GRAY = hexStringToByteArray("e6e6e6");
			XSSFColor light_gray = new XSSFColor(LIGHT_GRAY);
			
			byte[] GRAY = hexStringToByteArray("cecece");
			XSSFColor gray = new XSSFColor(GRAY);
			
			XSSFCellStyle lblue = (XSSFCellStyle) wb.createCellStyle();
			lblue.setFillForegroundColor(light_blue);
			lblue.setFillPattern(CellStyle.SOLID_FOREGROUND);
			
			XSSFCellStyle dblue = (XSSFCellStyle) wb.createCellStyle();
			dblue.setFillForegroundColor(dark_blue);
			dblue.setFillPattern(CellStyle.SOLID_FOREGROUND);
			
			XSSFCellStyle lgray = (XSSFCellStyle) wb.createCellStyle();
			lgray.setFillForegroundColor(light_gray);
			lgray.setFillPattern(CellStyle.SOLID_FOREGROUND);
			
			XSSFCellStyle ngray = (XSSFCellStyle) wb.createCellStyle();
			ngray.setFillForegroundColor(gray);
			ngray.setFillPattern(CellStyle.SOLID_FOREGROUND);
			
			XSSFCellStyle float_type = (XSSFCellStyle) wb.createCellStyle();
			float_type.setDataFormat(dataFormat.getFormat("0.00"));
			
			XSSFCellStyle int_type = (XSSFCellStyle) wb.createCellStyle();
			int_type.setDataFormat(dataFormat.getFormat("0"));
			
			
			XSSFCellStyle ngray_float = (XSSFCellStyle) wb.createCellStyle();
			ngray_float.setFillForegroundColor(gray);
			ngray_float.setFillPattern(CellStyle.SOLID_FOREGROUND);
			ngray_float.setDataFormat(dataFormat.getFormat("0.00"));
			
			XSSFCellStyle ngray_int = (XSSFCellStyle) wb.createCellStyle();
			ngray_int.setFillForegroundColor(gray);
			ngray_int.setFillPattern(CellStyle.SOLID_FOREGROUND);
			ngray_int.setDataFormat(dataFormat.getFormat("0"));
			
			XSSFCellStyle lgray_int = (XSSFCellStyle) wb.createCellStyle();
			lgray_int.setFillForegroundColor(light_gray);
			lgray_int.setFillPattern(CellStyle.SOLID_FOREGROUND);
			lgray_int.setDataFormat(dataFormat.getFormat("0"));
			
			XSSFCellStyle default_type = (XSSFCellStyle) wb.createCellStyle();
			
			
			String accession = contenus.get_organism().getAccession();
			String taxonomy = contenus.get_organism().getTaxonomy();
			String bioproject = contenus.get_organism().getBioproject();
			
			String new_cleft="Sum_"+cleft.split("_")[0];
			
			Organism empty_org=new Organism("","","","","","","");
			
			XSSFSheet worksheet = (XSSFSheet) wb.createSheet(cleft);
	
			List<XSSFRow> rowlist = new ArrayList<XSSFRow>();
			
			
			for (int i = 0; i <90; i++){
				rowlist.add(worksheet.createRow(i));
				
				for(int j = 0; j<40; j++){
					rowlist.get(i).createCell(j);
				}
			}
						
			for(int i = 0; i<90;i++)
			{
				for (int j = 0; j<40; j++)
				{
					rowlist.get(i).getCell(j).setCellStyle(getCellStyle(i,j,wb,dblue, lblue, lgray, ngray, float_type, int_type, ngray_float, ngray_int, lgray_int, default_type));
				}
			}
			
			
			//En-tête
			// Name
			String filename = "";
			if (chemin[3] != null && chemin[3] != "" ) {
				filename = chemin[3];
				rowlist.get(2).getCell(17).setCellValue("Organism Name");
			}
			else if (chemin[2] != null && chemin[2] != "" ) {
				filename = chemin[2];
				rowlist.get(2).getCell(17).setCellValue("SubGroup Name");
			}
			else if (chemin[1] != null && chemin[1] != "") {
				filename = chemin[1];
				rowlist.get(2).getCell(17).setCellValue("Group Name");
			}
			else {
				filename = chemin[0];
				rowlist.get(2).getCell(17).setCellValue("Kingdom Name");
			}
			
			rowlist.get(2).getCell(18).setCellValue(filename);
			
			
			
			//Nb Nucléotides
			rowlist.get(4).getCell(17).setCellValue("Number of nucleotides");
			rowlist.get(4).getCell(18).setCellValue(contenus.get_nb_trinucleotides());
		
			//Nb CDS
			rowlist.get(6).getCell(17).setCellValue("Number of valid cds sequences");
			rowlist.get(6).getCell(18).setCellValue(contenus.get_nb_CDS());
			
			
			
			//Invalid CDS
			rowlist.get(8).getCell(17).setCellValue("Number of invalid cds");
			rowlist.get(8).getCell(18).setCellValue(contenus.get_nb_CDS_non_traites());
			
			//Modification date
			String mod_date=contenus.get_organism().getModificationDate();
			if(mod_date!=null && !mod_date.isEmpty()){
				rowlist.get(10).getCell(17).setCellValue("Modification Date");
				rowlist.get(10).getCell(18).setCellValue(mod_date);
			}
			
			if (accession!=null && !accession.isEmpty()){
				//Accession
				rowlist.get(12).getCell(17).setCellValue("Accession");
				rowlist.get(12).getCell(18).setCellValue(accession);
			}
			
			if (taxonomy!=null && !taxonomy.isEmpty()){
				//Taxonomy
				rowlist.get(14).getCell(17).setCellValue("Taxonomy");
				rowlist.get(14).getCell(18).setCellValue(taxonomy);
			}
			
			if (bioproject!=null && !bioproject.isEmpty()){
				rowlist.get(16).getCell(17).setCellValue("Bioproject");
				rowlist.get(16).getCell(18).setCellValue(bioproject);
			}
			
			//Nombre de Chromosomes, DNA, Mitochondrion, etc...
			Integer tmp_i=18;
			Integer tmp_j=17;
			
			if ((bioproject==null || bioproject.isEmpty()) && (accession==null || accession.isEmpty()) && (taxonomy==null || taxonomy.isEmpty()) && (mod_date==null || mod_date.isEmpty())){
				tmp_i=10;
			}
			
			if (cleft.split("_")[0].equals("Sum")){
				rowlist.get(tmp_i).getCell(tmp_j).setCellValue("Nb of "+cleft.split("_")[1]);
			}
			else {
				rowlist.get(tmp_i).getCell(tmp_j).setCellValue("Nb of "+cleft.split("_")[0]);
			}
			
			rowlist.get(tmp_i).getCell(tmp_j+1).setCellValue(contenus.get_nb_items());
			
			//Ligne 1
			rowlist.get(0).getCell(0).setCellValue("Trinucléotides");			
			rowlist.get(0).getCell(1).setCellValue("Phase 0");			
			rowlist.get(0).getCell(2).setCellValue("Freq. Phase 0");		
			rowlist.get(0).getCell(3).setCellValue("Phase 1");		
			rowlist.get(0).getCell(4).setCellValue("Freq. Phase 1");			
			rowlist.get(0).getCell(5).setCellValue("Phase 2");			
			rowlist.get(0).getCell(6).setCellValue("Freq. Phase 2");
			rowlist.get(0).getCell(7).setCellValue("Pref. Phase 0");
			rowlist.get(0).getCell(8).setCellValue("Pref. Phase 1");
			rowlist.get(0).getCell(9).setCellValue("Pref. Phase 3");
			
			rowlist.get(0).getCell(11).setCellValue("Dinucléotides");			
			rowlist.get(0).getCell(12).setCellValue("Phase 0");			
			rowlist.get(0).getCell(13).setCellValue("Freq. Phase 0");			
			rowlist.get(0).getCell(14).setCellValue("Phase 1");
			rowlist.get(0).getCell(15).setCellValue("Freq. Phase 1");
			
			rowlist.get(66).getCell(0).setCellValue("Total");
			rowlist.get(18).getCell(11).setCellValue("Total");
			
			
			//on remplit les phases nombres des trinucléotides
			StringBuilder triplet = new StringBuilder("---");
			for (int j=0; j< 4; j++){
				triplet.setCharAt(0, Bdd.charOfNucleotideInt(j));
				for (int k=0; k< 4; k++){
					triplet.setCharAt(1, Bdd.charOfNucleotideInt(k));
					for (int l=0; l< 4; l++){
						int trinucleotide = l+4*k+16*j+1;
						triplet.setCharAt(2, Bdd.charOfNucleotideInt(l));
						rowlist.get(trinucleotide).getCell(0).setCellValue(triplet.toString()); //on remplit le nom des trinucléotides
						for (int i = 0; i<3; i++){
							rowlist.get(trinucleotide).getCell(1+2*i).setCellValue((double)(contenus.get_tableautrinucleotides(i,j,k,l)));
							rowlist.get(trinucleotide).getCell(7+i).setCellValue((double)(contenus.get_tableauPhasePref(i,j,k,l)));
							baseSum.get_contenu(new_cleft, empty_org).ajoute_mult_nucleotides(i, j, k, l, contenus.get_tableautrinucleotides(i,j,k,l),new_cleft);
							baseSum.get_contenu(new_cleft, empty_org).ajout_mult_PhasePref(i, j, k, l, contenus.get_tableauPhasePref(i,j,k,l),new_cleft);
							//System.out.println(baseSum.get_contenu(new_cleft, "", filename));;
						}
					}
				}
			}
			
			
			//on remplit les phases nombres  des dinucléotides
			StringBuilder couple = new StringBuilder("--");
			for (int j=0; j< 4; j++){
				couple.setCharAt(0, Bdd.charOfNucleotideInt(j));
				for (int k=0; k< 4; k++){
					couple.setCharAt(1, Bdd.charOfNucleotideInt(k));
					int dinucleotide = k+4*j+1;
					rowlist.get(dinucleotide).getCell(11).setCellValue(couple.toString()); //on remplit le nom des dinucléotides
					for (int i = 0; i<2; i++){
						rowlist.get(dinucleotide).getCell(12+2*i).setCellValue((double)(contenus.get_tableaudinucleotides(i,j,k)));
						baseSum.get_contenu(new_cleft, empty_org).ajoute_mult_nucleotides(i, j, k,  contenus.get_tableaudinucleotides(i,j,k));
					}
				}
			}
			
			//on remplit les totaux entiers
			//trinucleotides
			for(int i = 0; i<3;i++){
				double tmp = 0;
				for (int j = 0; j<64;j++){
					tmp = tmp + (rowlist.get(j+1).getCell(1+2*i).getNumericCellValue());	
				}
				rowlist.get(66).getCell(1+2*i).setCellValue(tmp);
				
			}
			//dinucleotides
			for(int i = 0; i<2;i++){
				double tmp = 0;
				for (int j = 0; j<16;j++){
					tmp = tmp + (rowlist.get(j+1).getCell(12+2*i).getNumericCellValue());	
				}
				rowlist.get(18).getCell(12+2*i).setCellValue(tmp);
				
			}
			
			
			//on remplit les phases probabilités
			//trinucleotides
			for (int i =0; i<3; i++){
				double total = rowlist.get(66).getCell(1+2*i).getNumericCellValue();
				if (total != 0){
					for (int j = 0; j<64; j++){
						double tmp = rowlist.get(j+1).getCell(1+2*i).getNumericCellValue();
						rowlist.get(j+1).getCell(2+2*i).setCellValue(100*tmp/total);
					}
				}
			}
			//dinucleotides
			for (int i =0; i<2; i++){
				double total = rowlist.get(18).getCell(12+2*i).getNumericCellValue();
				if (total != 0){
					for (int j = 0; j<16; j++){
						double tmp = rowlist.get(j+1).getCell(12+2*i).getNumericCellValue();
						rowlist.get(j+1).getCell(13+2*i).setCellValue(100*tmp/total);
					}
				}
			}
			
			//on remplit les totaux flottants
			//trinucleotides
			for(int i = 0; i<3;i++){
				double tmp = 0;
				for (int j = 0; j<64;j++){
					tmp = tmp + (rowlist.get(j+1).getCell(2+2*i).getNumericCellValue());	
				}
				rowlist.get(66).getCell(2+2*i).setCellValue(tmp);
				
			}
			//dinucleotides
			for(int i = 0; i<2;i++){
				double tmp = 0;
				for (int j = 0; j<16;j++){
					tmp = tmp + (rowlist.get(j+1).getCell(13+2*i).getNumericCellValue());	
				}
				rowlist.get(18).getCell(13+2*i).setCellValue(tmp);
				
			}
			
			//autosize column 
			for (int i = 0; i<89; i++){
				for (int j = 0; j < rowlist.get(i).getLastCellNum();j++) {
					worksheet.autoSizeColumn(j);
				}
			}
						
			baseSum.incr_mult_nb_CDS_non_traites(new_cleft, empty_org, contenus.get_nb_CDS_non_traites());
			baseSum.incr_mult_nb_CDS_traites(new_cleft, empty_org, contenus.get_nb_CDS());
			baseSum.get_contenu(new_cleft, empty_org).add_nb_trinucleotides(0, contenus.get_nb_trinucleotides(0));
			baseSum.get_contenu(new_cleft, empty_org).add_nb_trinucleotides(1, contenus.get_nb_trinucleotides(1));
			baseSum.get_contenu(new_cleft, empty_org).add_nb_trinucleotides(2, contenus.get_nb_trinucleotides(2));
			baseSum.get_contenu(new_cleft, empty_org).add_nb_items(1);
			
			//baseSum.close_tampon();
			
		} catch (CharInvalideException e) {
			e.printStackTrace();
		}
		
	}
		
	private static XSSFCellStyle getCellStyle(int i, int j, Workbook wb, XSSFCellStyle dblue, XSSFCellStyle lblue, XSSFCellStyle lgray, XSSFCellStyle ngray, XSSFCellStyle float_type, XSSFCellStyle int_type, XSSFCellStyle ngray_float, XSSFCellStyle ngray_int, XSSFCellStyle lgray_int, XSSFCellStyle default_type){
		
		
		if (i == 0 && j == 0){
			//Couleur foncée chelou
			return dblue;
		}
		//Couleur et style pour trinucléotides et dinucléotides et entête
		else if (i%2 == 0 && i < 68){
			//trinucléotides sans pref phase ou première ligne
			if ((j<7)|| (i==0 && j<10)){
				//Couleur foncée
				//Style de chiffres
				if (i>0 && i<68){
					//trinucléotides sans pref de phase
					if (j%2==1 && j<6){
						return ngray_int;
					}
					else if(j%2==0 && j<7 && j>0){
						return ngray_float;
					}
					//tri. pref de phase
					else if(j>6 && j<10){
						return ngray_int;
					}
					
					//dinculéotides sans pref de phase
					else if (i<19 && j%2==0 && j<16){
						return ngray_int;
					}
					else if(i<19 && j%2==1 && j<16){
						return ngray_float;
					}
					else{
						return ngray;
					}
				}
				else{
					return ngray;
				}
			}
			//pref phase trinucléotides
			else if (j<10 || ((j==17 || j==18) && i>0 && i<19)){
				//Couleur Claire
				//Style de chiffres
				if (i>0 && i<68){
					//trinucléotides sans pref de phase
					if (j%2==1 && j<6){
						return lgray_int;
					}
					//tri. pref de phase
					else if(j>6 && j<10){
						return lgray_int;
					}
					
					//dinculéotides sans pref de phase
					else if (i<19 && j%2==0 && j<16){
						return lgray_int;
					}
					else{
						return lgray;
					}
				}
			}
			//dinuclotides sans pref phase
			else if (i<19 && ((j>10 && j <16)||(i==0 && j<16 && j>10))){
				//Couleur foncée
				//Style de chiffres
				if (i>0 && i<68){
					//trinucléotides sans pref de phase
					if (j%2==1 && j<6){
						return ngray_int;
					}
					else if(j%2==0 && j<7 && j>0){
						return ngray_float;
					}
					//tri. pref de phase
					else if(j>6 && j<10){
						return ngray_int;
					}
					
					//dinculéotides sans pref de phase
					else if (i<19 && j%2==0 && j<16){
						return ngray_int;
					}
					else if(i<19 && j%2==1 && j<16){
						return ngray_float;
					}
					else{
						return ngray;
					}
				}
				else{
					return ngray;
				}
			}
			
			//Entête
			else if(i<17 && j>19){
				if (i>1 && i<17){
					if (j==18){
						//Très CLair
						return lblue;
					}
					else if(j==19){
						//Intermédiaire
						//Style de chiffres
						if (i>0 && i<68){
							//trinucléotides sans pref de phase
							if (j%2==1 && j<6){
								return lgray_int;
							}
							//tri. pref de phase
							else if(j>6 && j<10){
								return lgray_int;
							}
							
							//dinculéotides sans pref de phase
							else if (i<19 && j%2==0 && j<16){
								return lgray_int;
							}
							else{
								return lgray;
							}
						}
					}
				}
			}
		}
		
		//Style de chiffres
		if (i>0 && i<68){
			//trinucléotides sans pref de phase
			if (j%2==1 && j<6){
				return int_type;
			}
			else if(j%2==0 && j<7 && j>0){
				return float_type;
			}
			//tri. pref de phase
			else if(j>6 && j<10){
				return int_type;
			}
			
			//dinculéotides sans pref de phase
			else if (i<19 && j%2==0 && j<16){
				return int_type;
			}
			else if(i<19 && j%2==1 && j<16){
				return float_type;
			}
		}
		
		
		
		return default_type;
	}

}

	
