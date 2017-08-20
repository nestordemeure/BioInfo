package excel;

import java.io.FileOutputStream;
import java.util.Map.Entry;

import exceptions.CharInvalideException;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

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

	// Codes
    static String[] codes4 = new String[]{"X", "X1", "X2", "Xp"};
    static String[] codes64 = new String[64];
    static
    {
        try
        {
            for (int n1 = 0; n1 < 4; n1++)
            {
                char c1 = Bdd.charOfNucleotideInt(n1);

                for (int n2 = 0; n2 < 4; n2++)
                {
                    char c2 = Bdd.charOfNucleotideInt(n2);

                    for (int n3 = 0; n3 < 4; n3++)
                    {
                        char c3 = Bdd.charOfNucleotideInt(n3);
                        codes64[n1 * 16 + n2 * 4 + n3] = String.valueOf(new char[]{c1, c2, c3});
                    }
                }
            }
        }
        catch (CharInvalideException e)
        {
            // will not happend
        }
    }

	// Export a bdd tab by tab
	public static void writer(String folderpath, String filepath, String[] chemin, Bdd base, boolean is_leaf) 
	{
		try 
		{
			String cleft;
			content contenus;
			Bdd baseSum = new Bdd();

			Workbook workbook;
            if(Configuration.PARSER_USEFULLALPHABET)
            {
                // streaming workbook
                workbook = new SXSSFWorkbook(25/*Configuration.PARSER_IMAX+2*/);
            }
            else
            {
                workbook = new XSSFWorkbook();
            }


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

        // compute the number of columns
        int colNumber = Configuration.PARSER_CODENUMBER*Configuration.PARSER_CODENUMBER + 2 /*i col and sum col*/;

        // Description of the organisme
        writeDescription(cleft, contenus, chemin, worksheet, colNumber+1);

        // write all the matrixes
        int enTeteRow = 0;
        for (int modulo = 0; modulo <= 3; modulo++)
        {
            enTeteRow = writeMatrix(modulo, enTeteRow, contenus, worksheet, lblue, lgray, float_type, ngray_float);
        }

        // resize all the columns
        if (!Configuration.PARSER_USEFULLALPHABET)
        {
            for (int col = 0; col <= colNumber; col++)
            {
                worksheet.autoSizeColumn(col);
            }
        }

		String new_cleft = "Sum_" + cleft.split("_")[0];
		Organism empty_org = new Organism("","","","","","","");
		baseSum.get_contenu(new_cleft, empty_org).fusionContent(contenus);
	}

	// adds columns to describe the organism
    private static void writeDescription(String cleft, content contenus, String[] chemin, Sheet worksheet, int descriptionCol)
    {
        Row row;

        // Name
        String filename;
        row = worksheet.createRow(2);
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
        row = worksheet.createRow(4);
        row.createCell(descriptionCol).setCellValue("Number of valid cds sequences");
        row.createCell(descriptionCol+1).setCellValue(contenus.nb_CDS);

        // Nb Invalid CDS
        row = worksheet.createRow(6);
        row.createCell(descriptionCol).setCellValue("Number of invalid cds");
        row.createCell(descriptionCol+1).setCellValue(contenus.nb_CDS_non_traites);

        // Sums : Nombre de Chromosomes, DNA, Mitochondrion, etc...
        row = worksheet.createRow(8);
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
        row = worksheet.createRow(10);
        row.createCell(descriptionCol).setCellValue("Number of trinucleotides");
        row.createCell(descriptionCol+1).setCellValue(contenus.nb_trinucleotides);

        // Imax
        row = worksheet.createRow(12);
        row.createCell(descriptionCol).setCellValue("Imax");
        row.createCell(descriptionCol+1).setCellValue(Configuration.PARSER_IMAX);

        // Mingenelength
        row = worksheet.createRow(14);
        row.createCell(descriptionCol).setCellValue("Minimum gene length");
        row.createCell(descriptionCol+1).setCellValue(Configuration.PARSER_MINGENELENGTH);

        // Maxgenelength
        row = worksheet.createRow(16);
        row.createCell(descriptionCol).setCellValue("Maximum gene length");
        row.createCell(descriptionCol+1).setCellValue(Configuration.PARSER_MAXGENELENGTH);

        // we resize here to avoid overly long column caused by the following fields
        if (!Configuration.PARSER_USEFULLALPHABET)
        {
            worksheet.autoSizeColumn(descriptionCol);
            worksheet.autoSizeColumn(descriptionCol+1);
        }

        //Modification date
        row = worksheet.createRow(18);
        String mod_date = contenus.organism.getModificationDate();
        if(mod_date!=null && !mod_date.isEmpty())
        {
            row.createCell(descriptionCol).setCellValue("Modification Date");
            row.createCell(descriptionCol+1).setCellValue(mod_date);
        }

        //Accession
        row = worksheet.createRow(20);
        String accession = contenus.organism.getAccession();
        if (accession!=null && !accession.isEmpty())
        {
            row.createCell(descriptionCol).setCellValue("Accession");
            row.createCell(descriptionCol+1).setCellValue(accession);
        }

        //Taxonomy
        row = worksheet.createRow(22);
        String taxonomy = contenus.organism.getTaxonomy();
        if (taxonomy!=null && !taxonomy.isEmpty())
        {
            row.createCell(descriptionCol).setCellValue("Taxonomy");
            row.createCell(descriptionCol+1).setCellValue(taxonomy);
        }

        // Bioproject
        row = worksheet.createRow(24);
        String bioproject = contenus.organism.getBioproject();
        if (bioproject!=null && !bioproject.isEmpty())
        {
            row.createCell(descriptionCol).setCellValue("Bioproject");
            row.createCell(descriptionCol+1).setCellValue(bioproject);
        }
    }

    private static int writeMatrix(int modulo, int enTeteRow, content contenus, Sheet worksheet, XSSFCellStyle lblue, XSSFCellStyle lgray, XSSFCellStyle float_type, XSSFCellStyle ngray_float)
    {
        // A(X1, X2)
        String codes[];
        if (Configuration.PARSER_USEFULLALPHABET)
        {
            codes = codes64;
        }
        else
        {
            codes = codes4;
        }

        // First ligne : modulo
        String modName;
        if (modulo == 3)
        {
            modName = "Modulo 1";
        }
        else
        {
            modName = String.format("%d Modulo 3", modulo);
        }
        safeCreateRow(worksheet,enTeteRow).createCell(0).setCellValue(modName);
        enTeteRow++;

        // ----- header row -----

        Row headerRow = safeCreateRow(worksheet,enTeteRow);
        // i
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("i");
        headerCell.setCellStyle(lblue);
        // codes
        int colNumber = 1;
        for (int w1=0 ; w1<Configuration.PARSER_CODENUMBER ; w1++)
        {
            for (int w2 = 0; w2 < Configuration.PARSER_CODENUMBER; w2++)
            {
                String codeName = String.format("A(%s, %s)", codes[w1], codes[w2]);
                headerCell = headerRow.createCell(colNumber);
                headerCell.setCellValue(codeName);
                headerCell.setCellStyle(lblue);
                colNumber++;
            }
        }
        // sum
        headerCell = headerRow.createCell(colNumber);
        headerCell.setCellValue("Somme");
        headerCell.setCellStyle(lblue);

        // ----- matrix row -----

        Row matrixRow;
        Cell matrixCell;
        String lastColName = CellReference.convertNumToColString(colNumber-1);
        for (int i=0 ; i<Configuration.PARSER_IMAX ; i++)
        {
            int row = enTeteRow + i + 1;
            matrixRow = safeCreateRow(worksheet,row);
            // i
            matrixCell = matrixRow.createCell(0);
            matrixCell.setCellValue(i);
            if (i%2 == 0)
            {
                matrixCell.setCellStyle(lgray);
            }
            //codes
            int col = 1;
            for (int w1=0 ; w1<Configuration.PARSER_CODENUMBER ; w1++)
            {
                for (int w2 = 0; w2 < Configuration.PARSER_CODENUMBER; w2++)
                {
                    double Aiw1w2 = contenus.A(modulo, i, w1, w2);

                    matrixCell = matrixRow.createCell(col);
                    matrixCell.setCellValue(Aiw1w2);

                    if (i%2 == 0)
                    {
                        matrixCell.setCellStyle(ngray_float);
                    }
                    else
                    {
                        matrixCell.setCellStyle(float_type);
                    }

                    col++;
                }
            }
            // sum
            matrixCell = matrixRow.createCell(col);
            matrixCell.setCellStyle(ngray_float);
            matrixCell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
            String formula = String.format("SUM(B%d:%s%d)",row+1,lastColName,row+1);
            matrixCell.setCellFormula(formula);
        }

        // ----- footer row -----

        Row footerRow =  safeCreateRow(worksheet,enTeteRow+Configuration.PARSER_IMAX+1);
        // i
        Cell footerCell = footerRow.createCell(0);
        footerCell.setCellValue("Total");
        // codes
        for (int col = 1 ; col<colNumber; col++)
        {
            footerCell = footerRow.createCell(col);
            footerCell.setCellStyle(ngray_float);
            footerCell.setCellType(XSSFCell.CELL_TYPE_FORMULA);

            String colName = CellReference.convertNumToColString(col);
            String formula = String.format("SUM(%s%d:%s%d)",colName, enTeteRow+2, colName, enTeteRow+Configuration.PARSER_IMAX+1);
            footerCell.setCellFormula(formula);
        }

        // ----- empty rows -----

        enTeteRow += Configuration.PARSER_IMAX + 2;
        for (int row = enTeteRow; row <= enTeteRow + 20; row++)
        {
            safeCreateRow(worksheet,row);
        }
        enTeteRow += 20;

        return enTeteRow;
    }

    // safely acces a row
    // WARNING, we check only the 25 first lines to insure that we will not destroy some description. Afterward the fucntion becomes unsafe
    private static Row safeCreateRow(Sheet worksheet, int rowIndex)
    {
        if (rowIndex <= 25)
        {
            Row result = worksheet.getRow(rowIndex);

            if (result == null)
            {
                return worksheet.createRow(rowIndex);
            }
            else
            {
                return result;
            }
        }
        else
        {
            return worksheet.createRow(rowIndex);
        }
    }
}

	
