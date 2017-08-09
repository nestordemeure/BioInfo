package excel;

import Bdd.Bdd;
import Bdd.Bdd.content;
import Bdd.CircularCounter;
import configuration.Configuration;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import tree.Organism;
import ui.UIManager;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class ExcelWriter_deprecated
{
	public static byte[] hexStringToByteArray(String s) 
	{
	    int len = s.length();
	    byte[] data = new byte[len / 2];

	    for (int i = 0; i < len; i += 2) 
	    {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }

	    return data;
	}
	
	public static void writer(String folderpath, String filepath, String[] chemin, Bdd base, boolean is_leaf) 
	{
		try 
		{
			String xlsfile = filepath + ".xlsx";
			
			FileOutputStream fileOut = new FileOutputStream(xlsfile);
			Workbook workbook = new XSSFWorkbook();

			String cleft;
			content contenus;
			Bdd baseSum = new Bdd();
			
			for (Entry<String, content> entry : base.getContenus())
			{
				cleft = entry.getKey(); //"mitochondrie", "chloroplaste", "general"
				
				contenus = entry.getValue(); //un objet content équipé de toute les fonction que vous appliquiez a la base avant
								
				if (!cleft.equals(""))
				{
					writeTab(cleft, contenus, baseSum, workbook, chemin);
				}
			}
			
			if (is_leaf)
			{
				baseSum.exportBase(folderpath+Configuration.FOLDER_SEPARATOR+"Sums_"+chemin[3]);
				
				Bdd empty = new Bdd();
				
				for (Entry<String, content> entry : baseSum.getContenus())
				{
					cleft = entry.getKey(); //"Sum_Chromosomes", "Sum..."
					
					contenus = entry.getValue();
										
					if (!cleft.equals(""))
					{
						writeTab(cleft, contenus, empty, workbook, chemin);
					}
				}
			}

			workbook.write(fileOut);
			workbook.close();
			fileOut.flush();
			fileOut.close();

			if (is_leaf)
			{
				base.exportBase(filepath);
			}
			else
			{
				base.exportBase(folderpath+Configuration.FOLDER_SEPARATOR+"Sums");
			}
		}
		catch (Exception e) // TODO modified to catch all excel exceptions
		{
			UIManager.log("Error while writing excel file : " + e.getMessage() + " " + chemin.toString());
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
		List<XSSFRow> rowlist = new ArrayList<>();
		
		// create the cells (at least one per line of description)
		for (int row = 0; row <= Math.max(22, CircularCounter.imax+2); row++)
		{
			rowlist.add(worksheet.createRow(row));
			
			for(int col = 0; col<22; col++)
			{
				rowlist.get(row).createCell(col);
				rowlist.get(row).getCell(col).setCellStyle(default_type);
			}
		}

		//-------------------------------------------------------------------------------------
		// Description de l'organisme
		
		int descriptionCol = 19;
		
		// Name
		String filename;
		if (chemin[3] != null && ! chemin[3].isEmpty() )
		{
			filename = chemin[3];
			rowlist.get(2).getCell(descriptionCol).setCellValue("Organism Name");
		}
		else if (chemin[2] != null && ! chemin[2].isEmpty() )
		{
			filename = chemin[2];
			rowlist.get(2).getCell(descriptionCol).setCellValue("SubGroup Name");
		}
		else if (chemin[1] != null && ! chemin[1].isEmpty())
		{
			filename = chemin[1];
			rowlist.get(2).getCell(descriptionCol).setCellValue("Group Name");
		}
		else
		{
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
		Integer tmp_row;
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
		
		// trinucleotides
		rowlist.get(tmp_row+2).getCell(descriptionCol).setCellValue("Number of trinucleotides");
		rowlist.get(tmp_row+2).getCell(descriptionCol+1).setCellValue(contenus.nb_trinucleotides);
		
		//-------------------------------------------------------------------------------------
		// Tableau
		
		// entete du tableau
		String codes[] = {"X", "X1", "X2", "Xp"};
		int enTeteRow = 0;
		int col = 0;
		
		// i
		rowlist.get(enTeteRow).getCell(col).setCellStyle(lblue);
		rowlist.get(enTeteRow).getCell(col).setCellValue("i");
		for (int i = 0; i < CircularCounter.imax; i++)
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
		rowlist.get(CircularCounter.imax+1).getCell(col).setCellValue("Total");
		
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
				for (int i = 0; i < CircularCounter.imax; i++)
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
				rowlist.get(CircularCounter.imax+1).getCell(col).setCellStyle(ngray_float);
				rowlist.get(CircularCounter.imax+1).getCell(col).setCellValue(total);
			}
		}

		col++;
		rowlist.get(enTeteRow).getCell(col).setCellStyle(lblue);
		rowlist.get(enTeteRow).getCell(col).setCellValue("Somme");
		for (int i = 0; i < CircularCounter.imax; i++)
		{
			rowlist.get(i+1).getCell(col).setCellStyle(ngray_float);
			rowlist.get(i+1).getCell(col).setCellType(XSSFCell.CELL_TYPE_FORMULA);
			rowlist.get(i+1).getCell(col).setCellFormula(String.format("SUM(B%d:Q%d)",i+2,i+2));
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
}

	
