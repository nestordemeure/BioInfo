package excel;

import java.io.FileOutputStream;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;

import tree.Organism;
import ui.UIManager;
import configuration.Configuration;
import Bdd.*;
import Bdd.Bdd.content;

public class ExcelWriter
{
	// Colors
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
	static XSSFColor light_blue = new XSSFColor( hexStringToByteArray("a7c8fd") );
	static XSSFColor light_gray = new XSSFColor( hexStringToByteArray("e6e6e6") );
	static XSSFColor gray = new XSSFColor( hexStringToByteArray("cecece") );

	// Export a bdd tab by tab
	public static void writer(String folderpath, String filepath, String[] chemin, Bdd base, boolean is_leaf) 
	{
		try 
		{
			String cleft;
			content contenus;
			Bdd baseSum = new Bdd();
			Workbook workbook = new XSSFWorkbook();

			// Styles
			int floatFormat = workbook.createDataFormat().getFormat("0.00");

			XSSFCellStyle lblue = (XSSFCellStyle) workbook.createCellStyle();
				lblue.setFillForegroundColor(light_blue);
				lblue.setFillPattern(CellStyle.SOLID_FOREGROUND);

			XSSFCellStyle lgray = (XSSFCellStyle) workbook.createCellStyle();
				lgray.setFillForegroundColor(light_gray);
				lgray.setFillPattern(CellStyle.SOLID_FOREGROUND);

			XSSFCellStyle float_type = (XSSFCellStyle) workbook.createCellStyle();
				float_type.setDataFormat(floatFormat);

			XSSFCellStyle ngray_float = (XSSFCellStyle) workbook.createCellStyle();
				ngray_float.setFillForegroundColor(gray);
				ngray_float.setFillPattern(CellStyle.SOLID_FOREGROUND);
				ngray_float.setDataFormat(floatFormat);

			// Tab creation
			for (Entry<String, content> entry : base.getContenus())
			{
				cleft = entry.getKey(); //"mitochondrie", "chloroplaste", "general"
				contenus = entry.getValue(); //un objet content équipé de toute les fonction que vous appliquiez a la base avant
								
				if (!cleft.isEmpty())
				{
					writeTab(cleft, contenus, baseSum, chemin, workbook, lblue, lgray, float_type, ngray_float);
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
										
					if (!cleft.isEmpty())
					{
						writeTab(cleft, contenus, empty, chemin, workbook, lblue, lgray, float_type, ngray_float);
					}
				}

				base.exportBase(filepath);
			}
			else
			{
				base.exportBase(folderpath + Configuration.FOLDER_SEPARATOR + "Sums");
			}

			String xlsfile = filepath + ".xlsx";
			FileOutputStream fileOut = new FileOutputStream(xlsfile);
			workbook.write(fileOut);
			workbook.close();
			fileOut.close();
		}
		catch (Exception e)
		{
			UIManager.log("Error while writing excel file : " + e.getMessage() + " " + chemin.toString());
		}
	}

	// export a single tab
	private static void writeTab(String cleft, content contenus, Bdd baseSum, String[] chemin, Workbook wb, XSSFCellStyle lblue, XSSFCellStyle lgray, XSSFCellStyle float_type, XSSFCellStyle ngray_float)
	{
		Sheet worksheet = wb.createSheet(cleft);

		//-------------------------------------------------------------------------------------
		// Tableau

		int rowNumber = Math.max(18, CircularCounter.imax+2);
		int enTeteRow = 0;
		int col = 0;
		
		// First col (i)
		Cell firstICell = worksheet.createRow(enTeteRow).createCell(col);
		firstICell.setCellValue("i");
		firstICell.setCellStyle(lblue);
		for (int i = 0; i < CircularCounter.imax; i++)
		{
			Cell cell = worksheet.createRow(i+1).createCell(col);
			cell.setCellValue(i);

			if (i%2 == 0)
			{
				cell.setCellStyle(lgray);
			}
		}
		worksheet.createRow(CircularCounter.imax+1).createCell(col).setCellValue("Total");
		worksheet.autoSizeColumn(col);
		
		// A(X1, X2)
		String codes[] = {"X", "X1", "X2", "Xp"};
		for (int w1 = 0; w1 < 4; w1++)
		{
			for (int w2 = 0; w2 < 4; w2++)
			{
				col++;
				String codeName = String.format("A(%s, %s)", codes[w1], codes[w2]);
				Cell headerCell = worksheet.getRow(enTeteRow).createCell(col);
				headerCell.setCellValue(codeName);
				headerCell.setCellStyle(lblue);

				double total = 0;
				for (int i = 0; i < CircularCounter.imax; i++)
				{
					Cell cell = worksheet.getRow(i+1).createCell(col);
					double Aiw1w2 = contenus.A(i, w1, w2);
					cell.setCellValue(Aiw1w2);

					if (i%2 == 0)
					{
						cell.setCellStyle(ngray_float);
					}
					else
					{
						cell.setCellStyle(float_type);
					}

					total += Aiw1w2;
				}

				Cell footerCell = worksheet.getRow(CircularCounter.imax+1).createCell(col);
				footerCell.setCellValue(total);
				footerCell.setCellStyle(ngray_float);
				worksheet.autoSizeColumn(col);
			}
		}

		// Sum column
		col++;
		Cell sumCell = worksheet.getRow(enTeteRow).createCell(col);
		sumCell.setCellValue("Somme");
		sumCell.setCellStyle(lblue);
		for (int i = 0; i < CircularCounter.imax; i++)
		{
			Cell cell = worksheet.getRow(i+1).createCell(col);
			cell.setCellStyle(ngray_float);
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(String.format("SUM(B%d:Q%d)",i+2,i+2));
		}
		worksheet.autoSizeColumn(col);

		//-------------------------------------------------------------------------------------
		// Description de l'organisme

		Row row;
		int descriptionCol = col+2;

		// create the missing rows if needed
		for (int rowId = CircularCounter.imax+2; rowId <= rowNumber; rowId++)
		{
			worksheet.createRow(rowId);
		}

		// Name
		String filename;
		row = worksheet.getRow(2);
		if (chemin[3] != null && !chemin[3].isEmpty() )
		{
			filename = chemin[3];
			row.createCell(descriptionCol).setCellValue("Organism Name");
		}
		else if (chemin[2] != null && !chemin[2].isEmpty() )
		{
			filename = chemin[2];
			row.createCell(descriptionCol).setCellValue("SubGroup Name");
		}
		else if (chemin[1] != null && !chemin[1].isEmpty())
		{
			filename = chemin[1];
			row.createCell(descriptionCol).setCellValue("Group Name");
		}
		else
		{
			filename = chemin[0];
			row.createCell(descriptionCol).setCellValue("Kingdom Name");
		}
		row.createCell(descriptionCol+1).setCellValue(filename);

		// Nb CDS
		row = worksheet.getRow(4);
		row.createCell(descriptionCol).setCellValue("Number of valid cds sequences");
		row.createCell(descriptionCol+1).setCellValue(contenus.nb_CDS);

		// Nb Invalid CDS
		row = worksheet.getRow(6);
		row.createCell(descriptionCol).setCellValue("Number of invalid cds");
		row.createCell(descriptionCol+1).setCellValue(contenus.nb_CDS_non_traites);

		// Sums : Nombre de Chromosomes, DNA, Mitochondrion, etc...
		row = worksheet.getRow(8);
		if (cleft.split("_")[0].equals("Sum"))
		{
			row.createCell(descriptionCol).setCellValue("Nb of "+cleft.split("_")[1]);
		}
		else
		{
			row.createCell(descriptionCol).setCellValue("Nb of "+cleft.split("_")[0]);
		}
		row.createCell(descriptionCol+1).setCellValue(contenus.nb_items);

		// Trinucleotides
		row = worksheet.getRow(10);
		row.createCell(descriptionCol).setCellValue("Number of trinucleotides");
		row.createCell(descriptionCol+1).setCellValue(contenus.nb_trinucleotides);

		//Modification date
		row = worksheet.getRow(12);
		String mod_date = contenus.organism.getModificationDate();
		if(mod_date!=null && !mod_date.isEmpty())
		{
			row.createCell(descriptionCol).setCellValue("Modification Date");
			row.createCell(descriptionCol+1).setCellValue(mod_date);
		}

		//Accession
		row = worksheet.getRow(14);
		String accession = contenus.organism.getAccession();
		if (accession!=null && !accession.isEmpty())
		{
			row.createCell(descriptionCol).setCellValue("Accession");
			row.createCell(descriptionCol+1).setCellValue(accession);
		}

		//Taxonomy
		row = worksheet.getRow(16);
		String taxonomy = contenus.organism.getTaxonomy();
		if (taxonomy!=null && !taxonomy.isEmpty())
		{
			row.createCell(descriptionCol).setCellValue("Taxonomy");
			row.createCell(descriptionCol+1).setCellValue(taxonomy);
		}

		// Bioproject
		row = worksheet.getRow(18);
		String bioproject = contenus.organism.getBioproject();
		if (bioproject!=null && !bioproject.isEmpty())
		{
			row.createCell(descriptionCol).setCellValue("Bioproject");
			row.createCell(descriptionCol+1).setCellValue(bioproject);
		}

		worksheet.autoSizeColumn(descriptionCol);
		worksheet.autoSizeColumn(descriptionCol+1);

		//-------------------------------------------------------------------------------------

		String new_cleft = "Sum_" + cleft.split("_")[0];
		Organism empty_org = new Organism("","","","","","","");
		baseSum.get_contenu(new_cleft, empty_org).fusionContent(contenus);
	}
}

	
