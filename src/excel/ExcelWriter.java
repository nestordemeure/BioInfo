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

	private static void writeTab(String cleft, content contenus, Bdd baseSum, Workbook wb, String[] chemin)
	{	
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
		
		
		String accession = contenus.organism.getAccession();
		String taxonomy = contenus.organism.getTaxonomy();
		String bioproject = contenus.organism.getBioproject();
		
		String new_cleft="Sum_"+cleft.split("_")[0];
		
		Organism empty_org=new Organism("","","","","","","");
		
		//-------------------------------------------------------------------------------------
		// build a 90x40 spreadsheet
		
		XSSFSheet worksheet = (XSSFSheet) wb.createSheet(cleft);
		List<XSSFRow> rowlist = new ArrayList<XSSFRow>();	
		
		// create the cells
		for (int row = 0; row <CircularCounter.imax+3; row++)
		{
			rowlist.add(worksheet.createRow(row));
			
			for(int col = 0; col<21; col++)
			{
				rowlist.get(row).createCell(col);
				rowlist.get(row).getCell(col).setCellStyle(default_type);
			}
		}

		//-------------------------------------------------------------------------------------
		// Description de l'organisme
		
		int descriptionCol = 18;
		
		// Name
		String filename = "";
		if (chemin[3] != null && chemin[3] != "" ) {
			filename = chemin[3];
			rowlist.get(2).getCell(descriptionCol).setCellValue("Organism Name");
		}
		else if (chemin[2] != null && chemin[2] != "" ) {
			filename = chemin[2];
			rowlist.get(2).getCell(descriptionCol).setCellValue("SubGroup Name");
		}
		else if (chemin[1] != null && chemin[1] != "") {
			filename = chemin[1];
			rowlist.get(2).getCell(descriptionCol).setCellValue("Group Name");
		}
		else {
			filename = chemin[0];
			rowlist.get(2).getCell(descriptionCol).setCellValue("Kingdom Name");
		}
		rowlist.get(2).getCell(descriptionCol+1).setCellValue(filename);

		// Nb CDS
		rowlist.get(4).getCell(descriptionCol).setCellValue("Number of valid cds sequences");
		rowlist.get(4).getCell(descriptionCol+1).setCellValue(contenus.nb_CDS);
		
		// Invalid CDS
		rowlist.get(6).getCell(descriptionCol).setCellValue("Number of invalid cds");
		rowlist.get(6).getCell(descriptionCol+1).setCellValue(contenus.nb_CDS_non_traites);
		
		//Modification date
		String mod_date=contenus.organism.getModificationDate();
		if(mod_date!=null && !mod_date.isEmpty()){
			rowlist.get(8).getCell(descriptionCol).setCellValue("Modification Date");
			rowlist.get(8).getCell(descriptionCol+1).setCellValue(mod_date);
		}
		
		if (accession!=null && !accession.isEmpty()){
			//Accession
			rowlist.get(10).getCell(descriptionCol).setCellValue("Accession");
			rowlist.get(10).getCell(descriptionCol+1).setCellValue(accession);
		}
		
		if (taxonomy!=null && !taxonomy.isEmpty()){
			//Taxonomy
			rowlist.get(12).getCell(descriptionCol).setCellValue("Taxonomy");
			rowlist.get(12).getCell(descriptionCol+1).setCellValue(taxonomy);
		}
		
		if (bioproject!=null && !bioproject.isEmpty()){
			rowlist.get(14).getCell(descriptionCol).setCellValue("Bioproject");
			rowlist.get(14).getCell(descriptionCol+1).setCellValue(bioproject);
		}
		
		// Sums : Nombre de Chromosomes, DNA, Mitochondrion, etc...
		Integer tmp_row=16;
		if ((bioproject==null || bioproject.isEmpty()) && (accession==null || accession.isEmpty()) && (taxonomy==null || taxonomy.isEmpty()) && (mod_date==null || mod_date.isEmpty()))
		{
			tmp_row=8;
		}
		else
		{
			tmp_row=16;
		}
		
		if (cleft.split("_")[0].equals("Sum"))
		{
			rowlist.get(tmp_row).getCell(descriptionCol).setCellValue("Nb of "+cleft.split("_")[1]);
		}
		else 
		{
			rowlist.get(tmp_row).getCell(descriptionCol).setCellValue("Nb of "+cleft.split("_")[0]);
		}
		
		rowlist.get(tmp_row).getCell(descriptionCol+1).setCellValue(contenus.nb_items);
		
		//-------------------------------------------------------------------------------------
		// Tableau
		
		// entete du tableau
		String codes[] = {"X", "X1", "X2", "Xp"};
		int enTeteRow = 0;
		int col = 0;
		
		// i
		rowlist.get(enTeteRow).getCell(col).setCellStyle(lblue);
		rowlist.get(enTeteRow).getCell(col).setCellValue("i");
		for (int i = 0; i <= CircularCounter.imax; i++)
		{
			if (i%2 == 0)
			{
				rowlist.get(i+1).getCell(col).setCellStyle(lgray_int);
			}
			else
			{
				rowlist.get(i+1).getCell(col).setCellStyle(int_type);
			}
			rowlist.get(i+1).getCell(col).setCellValue(i);
		}
		rowlist.get(CircularCounter.imax+2).getCell(col).setCellValue("Total");
		
		// A(X1, X2)
		for (int w1 = 0; w1 < 4; w1++)
		{
			for (int w2 = 0; w2 < 4; w2++)
			{
				col++;
				String codeName = String.format("A(%s, %s)", codes[w1], codes[w2]);
				rowlist.get(enTeteRow).getCell(col).setCellStyle(lblue);
				rowlist.get(enTeteRow).getCell(col).setCellValue(codeName);
				
				double total = 0;
				for (int i = 0; i <= CircularCounter.imax; i++)
				{
					if (i%2 == 0)
					{
						rowlist.get(i+1).getCell(col).setCellStyle(ngray_float);
					}
					else
					{
						rowlist.get(i+1).getCell(col).setCellStyle(float_type);
					}
					double Aiw1w2 = contenus.A(i, w1, w2);
					rowlist.get(i+1).getCell(col).setCellValue(Aiw1w2);
					total += Aiw1w2;
				}
				rowlist.get(CircularCounter.imax+2).getCell(col).setCellStyle(ngray_float);
				rowlist.get(CircularCounter.imax+2).getCell(col).setCellValue(total);
			}
		}
		
		//-------------------------------------------------------------------------------------
		// Clean-up
		
		//autosize column 
		for (int row = 0; row<CircularCounter.imax+3; row++)
		{
			for (int c = 0; c<rowlist.get(row).getLastCellNum();c++) 
			{
				worksheet.autoSizeColumn(c);
			}
		}
		
		baseSum.get_contenu(new_cleft, empty_org).fusionContent(contenus);
	}
	
	// deal with the style of cells according to their positions
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

	
