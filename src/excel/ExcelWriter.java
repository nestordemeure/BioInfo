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
import Parser.*;
import Bdd.*;
import Bdd.Bdd.content;

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
	
	
	public static void writer(String filepath, String[] chemin, Bdd base) {
		try {
			
			
			Pattern regex1 = Pattern.compile(".*/");
			Matcher m = regex1.matcher(filepath);
			if (m.find()){
				String folderpath = m.group(0);
				File folders = new File(folderpath);
				folders.mkdirs();
			}
			
			String xlsfile = filepath+".xlsx";
			
			FileOutputStream fileOut = new FileOutputStream(xlsfile);
			Workbook workbook = new XSSFWorkbook();
			
			

			String cleft;
			content contenus;
			
			for (Entry<String, content> entry : base.getContenus())
			{
				cleft = entry.getKey(); //"mitochondrie", "chloroplaste", "general"
				contenus = entry.getValue(); //un objet content équipé de toute les fonction que vous appliquiez a la base avant
				
				XSSFSheet worksheet = (XSSFSheet) workbook.createSheet(cleft);
	
				List<XSSFRow> rowlist = new ArrayList<XSSFRow>();
				
				
				for (int i = 0; i <90; i++){
					rowlist.add(worksheet.createRow(i));
					
					for(int j = 0; j<20; j++){
						rowlist.get(i).createCell(j);
					}
				}
				
				XSSFDataFormat dataFormat = (XSSFDataFormat) workbook.createDataFormat();
				
				CellStyle intStyle = workbook.createCellStyle();
				intStyle.setDataFormat(dataFormat.getFormat("0"));
				
				CellStyle floatStyle = workbook.createCellStyle();
				floatStyle.setDataFormat(dataFormat.getFormat("0.00"));
				
				byte[] LIGHT_BLUE = hexStringToByteArray("a7c8fd");
				XSSFColor light_blue = new XSSFColor(LIGHT_BLUE);
				
				byte[] DARK_BLUE = hexStringToByteArray("3686ca");
				XSSFColor dark_blue = new XSSFColor(DARK_BLUE);
				
				byte[] LIGHT_GRAY = hexStringToByteArray("e6e6e6");
				XSSFColor light_gray = new XSSFColor(LIGHT_GRAY);
				
				byte[] GRAY = hexStringToByteArray("cecece");
				XSSFColor gray = new XSSFColor(GRAY);
				
				XSSFColor plop = new XSSFColor(new java.awt.Color(128, 0, 128));
				
				//En-tête
				// Name
				String filename = "";
				if (chemin[3] != null && chemin[3] != "" ) {
					filename = chemin[3];
					rowlist.get(2).getCell(11).setCellValue("Organism Name");
				}
				else if (chemin[2] != null && chemin[2] != "" ) {
					filename = chemin[2];
					rowlist.get(2).getCell(11).setCellValue("SubGroup Name");
				}
				else if (chemin[1] != null && chemin[1] != "") {
					filename = chemin[1];
					rowlist.get(2).getCell(11).setCellValue("Group Name");
				}
				else {
					filename = chemin[0];
					rowlist.get(2).getCell(11).setCellValue("Kingdom Name");
				}
				
				
				rowlist.get(2).getCell(12).setCellValue(filename);
				
				
				
//				//Inutile
//				rowlist.get(1).getCell(0).setCellValue("Chemin");			
//				rowlist.get(1).getCell(1).setCellValue(chemin[0]);			
//				rowlist.get(1).getCell(2).setCellValue(chemin[1]);
//				rowlist.get(1).getCell(3).setCellValue(chemin[2]);
//				rowlist.get(1).getCell(4).setCellValue(chemin[3]);
				
				
				//Nb Nucléotides
				rowlist.get(4).getCell(11).setCellValue("Number of nucleotides");
				rowlist.get(4).getCell(12).setCellStyle(intStyle);
				rowlist.get(4).getCell(12).setCellValue(contenus.get_nb_trinucleotides());
//				rowlist.get(3).getCell(8).setCellValue("Nb dinucleotides");
//				rowlist.get(3).getCell(9).setCellStyle(intStyle);
//				rowlist.get(3).getCell(9).setCellValue(contenus.get_nb_dinucleotides()/2);
			
				//Nb CDS
				rowlist.get(6).getCell(11).setCellValue("Number of cds sequences");
				rowlist.get(6).getCell(12).setCellStyle(intStyle);
				rowlist.get(6).getCell(12).setCellValue(contenus.get_nb_CDS());
				
				
				//Invalid CDS
				rowlist.get(8).getCell(11).setCellValue("Number of invalid cds");
				rowlist.get(8).getCell(12).setCellStyle(intStyle);
				rowlist.get(8).getCell(12).setCellValue(contenus.get_nb_CDS_non_traites());
				
	
				
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
				
				rowlist.get(67).getCell(0).setCellValue("Dinucléotides");			
				rowlist.get(67).getCell(1).setCellValue("Phase 0");			
				rowlist.get(67).getCell(2).setCellValue("Freq. Phase 0");			
				rowlist.get(67).getCell(3).setCellValue("Phase 1");
				rowlist.get(67).getCell(4).setCellValue("Freq. Phase 1");
				rowlist.get(67).getCell(5).setCellValue("Pref. Phase 0");
				rowlist.get(67).getCell(6).setCellValue("Pref. Phase 1");
				
				rowlist.get(65).getCell(0).setCellValue("Total");
				rowlist.get(84).getCell(0).setCellValue("Total");
				
				
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
								rowlist.get(trinucleotide).getCell(1+2*i).setCellStyle(intStyle);
								rowlist.get(trinucleotide).getCell(1+2*i).setCellValue((double)(contenus.get_tableautrinucleotides(i,j,k,l)));
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
						int dinucleotide = k+4*j+68;
						rowlist.get(dinucleotide).getCell(0).setCellValue(couple.toString()); //on remplit le nom des dinucléotides
						for (int i = 0; i<2; i++){
							rowlist.get(dinucleotide).getCell(1+2*i).setCellStyle(intStyle);
							rowlist.get(dinucleotide).getCell(1+2*i).setCellValue((double)(contenus.get_tableaudinucleotides(i,j,k)));
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
					rowlist.get(65).getCell(1+2*i).setCellStyle(intStyle);
					rowlist.get(65).getCell(1+2*i).setCellValue(tmp);
					
				}
				//dinucleotides
				for(int i = 0; i<2;i++){
					double tmp = 0;
					for (int j = 0; j<16;j++){
						tmp = tmp + (rowlist.get(j+68).getCell(1+2*i).getNumericCellValue());	
					}
					rowlist.get(84).getCell(1+2*i).setCellStyle(intStyle);
					rowlist.get(84).getCell(1+2*i).setCellValue(tmp);
					
				}
				
				
				//on remplit les phases probabilités
				//trinucleotides
				for (int i =0; i<3; i++){
					double total = rowlist.get(65).getCell(1+2*i).getNumericCellValue();
					if (total != 0){
						for (int j = 0; j<64; j++){
							rowlist.get(j+1).getCell(2+2*i).setCellStyle(floatStyle);
							double tmp = rowlist.get(j+1).getCell(1+2*i).getNumericCellValue();
							rowlist.get(j+1).getCell(2+2*i).setCellValue(100*tmp/total);
						}
					}
				}
				//dinucleotides
				for (int i =0; i<2; i++){
					double total = rowlist.get(84).getCell(1+2*i).getNumericCellValue();
					if (total != 0){
						for (int j = 0; j<16; j++){
							rowlist.get(j+68).getCell(2+2*i).setCellStyle(floatStyle);
							double tmp = rowlist.get(j+68).getCell(1+2*i).getNumericCellValue();
							rowlist.get(j+68).getCell(2+2*i).setCellValue(100*tmp/total);
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
					rowlist.get(65).getCell(2+2*i).setCellStyle(intStyle);
					rowlist.get(65).getCell(2+2*i).setCellValue(tmp);
					
				}
				//dinucleotides
				for(int i = 0; i<2;i++){
					double tmp = 0;
					for (int j = 0; j<16;j++){
						tmp = tmp + (rowlist.get(j+68).getCell(2+2*i).getNumericCellValue());	
					}
					rowlist.get(84).getCell(2+2*i).setCellStyle(intStyle);
					rowlist.get(84).getCell(2+2*i).setCellValue(tmp);
					
				}
				
				//autosize column 
				for (int i = 0; i<89; i++){
					for (int j = 0; j < rowlist.get(i).getLastCellNum();j++) {
						worksheet.autoSizeColumn(j);
					}
				}
				
				//peinture
				for (int i = 0; i<31; i++){
					for (int j = 0; j < 10;j++) {
						XSSFCellStyle tmp = (XSSFCellStyle) rowlist.get(2*i+2).getCell(j).getCellStyle();
						
						if (j<7){
							tmp.setFillForegroundColor(gray);
						}
						else{
							tmp.setFillForegroundColor(light_gray);
						}
						tmp.setFillPattern(CellStyle.SOLID_FOREGROUND);
						rowlist.get(2*i+2).getCell(j).setCellStyle(tmp);
					}
				}
			
			}
			
			
			
			workbook.write(fileOut);
			workbook.close();
			fileOut.flush();
			fileOut.close();

			
			base.exportBase(filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CharInvalideException e) {
			e.printStackTrace();
		}

	}
}
