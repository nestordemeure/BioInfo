package excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

import exceptions.CharInvalideException;
import parser.*;



public class ExcelWriter {
	
	
	public static void writer(String filepath, String[] chemin, Bdd base) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filepath);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet worksheet = workbook.createSheet("POI Worksheet");

			List<HSSFRow> rowlist = new ArrayList<HSSFRow>();
			
			
			for (int i = 0; i <80; i++){
				rowlist.add(worksheet.createRow(i));
				
				for(int j = 0; j<20; j++){
					rowlist.get(i).createCell(j);
				}
			}
			
			HSSFDataFormat dataFormat = workbook.createDataFormat();
			
			CellStyle intStyle = workbook.createCellStyle();
			intStyle.setDataFormat(dataFormat.getBuiltinFormat("0"));
			
			CellStyle floatStyle = workbook.createCellStyle();
			floatStyle.setDataFormat(dataFormat.getBuiltinFormat("0.00"));
			
			
			
			// ligne 1 
			rowlist.get(0).getCell(0).setCellValue("Nom");
			rowlist.get(0).getCell(1).setCellValue(chemin[3]);
			
			//ligne 2
			rowlist.get(1).getCell(0).setCellValue("Chemin");			
			rowlist.get(1).getCell(1).setCellValue(chemin[0]);			
			rowlist.get(1).getCell(2).setCellValue(chemin[1]);
			rowlist.get(1).getCell(3).setCellValue(chemin[2]);
			rowlist.get(1).getCell(4).setCellValue(chemin[3]);
			
			//ligne 3
			rowlist.get(2).getCell(0).setCellValue("Nb CDS");
			rowlist.get(2).getCell(1).setCellStyle(intStyle);
			rowlist.get(2).getCell(1).setCellValue(base.get_nb_CDS());
			
			
			
			//ligne 4
			rowlist.get(3).getCell(0).setCellValue("Nb trinucleotides");
			rowlist.get(3).getCell(1).setCellStyle(intStyle);
			rowlist.get(3).getCell(1).setCellValue(base.get_nb_trinucleotides());
		
			
			//ligne 5
			rowlist.get(4).getCell(0).setCellValue("Nb CDS non traités");
			rowlist.get(4).getCell(1).setCellStyle(intStyle);
			rowlist.get(4).getCell(1).setCellValue(base.get_nb_CDS_non_traites());
			

			
			//ligne 7
			rowlist.get(6).getCell(0).setCellValue("Trinucléotides");			
			rowlist.get(6).getCell(1).setCellValue("Nb Ph0");			
			rowlist.get(6).getCell(2).setCellValue("Pb Ph0");		
			rowlist.get(6).getCell(3).setCellValue("Nb Ph1");		
			rowlist.get(6).getCell(4).setCellValue("Pb Ph1");			
			rowlist.get(6).getCell(5).setCellValue("Nb Ph2");			
			rowlist.get(6).getCell(6).setCellValue("Pb Ph2");			
			rowlist.get(6).getCell(8).setCellValue("Dinucléotides");			
			rowlist.get(6).getCell(9).setCellValue("Nb Ph0");			
			rowlist.get(6).getCell(10).setCellValue("Pb Ph0");			
			rowlist.get(6).getCell(11).setCellValue("Nb Ph1");
			rowlist.get(6).getCell(12).setCellValue("Pb Ph1");
			
			
			// declaration trinucléotides
			for (int i = 0; i< 64; i++){
				rowlist.get(i+7).getCell(0).setCellValue(base.int_to_trinucleotide(i));
			}
			
			rowlist.get(71).getCell(0).setCellValue("Total");
			
			//on remplit les phases nombres
			for (int i = 0; i<3; i++){
				for (int j=0; j< 4; j++){
					for (int k=0; k< 4; k++){
						for (int l=0; l< 4; l++){
							int trinucleotide = l+4*k+16*j+7;
							rowlist.get(trinucleotide).getCell(1+2*i).setCellStyle(intStyle);
							rowlist.get(trinucleotide).getCell(1+2*i).setCellValue((double)(base.get_tableautrinucleotides(i,j,k,l)));
							
						}
					}
				}
			}
			
			//on remplit les totaux entiers
			for(int i = 0; i<3;i++){
				double tmp = 0;
				for (int j = 0; j<64;j++){
					tmp = tmp + (rowlist.get(j+7).getCell(1+2*i).getNumericCellValue());	
				}
				rowlist.get(71).getCell(1+2*i).setCellStyle(intStyle);
				rowlist.get(71).getCell(1+2*i).setCellValue(tmp);
				
			}
			
			
			//on remplit les phases probabilités
			for (int i =0; i<3; i++){
				double total = rowlist.get(71).getCell(1+2*i).getNumericCellValue();
				for (int j = 0; j<64; j++){
					rowlist.get(j+7).getCell(2+2*i).setCellStyle(floatStyle);
					double tmp = rowlist.get(j+7).getCell(1+2*i).getNumericCellValue();
					rowlist.get(j+7).getCell(2+2*i).setCellValue(100*tmp/total);
				}
			}
			
			//on remplit les totaux flottants
			for(int i = 0; i<3;i++){
				double tmp = 0;
				for (int j = 0; j<64;j++){
					tmp = tmp + (rowlist.get(j+7).getCell(2+2*i).getNumericCellValue());	
				}
				rowlist.get(71).getCell(2+2*i).setCellStyle(intStyle);
				rowlist.get(71).getCell(2+2*i).setCellValue(tmp);
				
			}
			
			//autosize column 
			for (int i = 0; i<73; i++){
				for (int j = 0; j < rowlist.get(i).getLastCellNum();j++) {
					worksheet.autoSizeColumn(j);
				}
			}
			
			
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CharInvalideException e) {
			e.printStackTrace();
		}

	}
}
