/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infatlan.hn.generador.prenotas.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author enajera
 */
public class ProcessXLS {

    public String fileName;

    public ProcessXLS(String name) {
        fileName = name;
    }

    public void readXLS() throws FileNotFoundException, IOException {
        File f = new File(fileName);
        FileInputStream fis = new FileInputStream(f);
        XSSFWorkbook excelWorkbook = new XSSFWorkbook(fis);
        XSSFSheet excelSheet = excelWorkbook.getSheetAt(0);
        
        int rows = excelSheet.getPhysicalNumberOfRows();//3
        int cols = excelSheet.getRow(1).getPhysicalNumberOfCells();//2
        
        String data[][] = new String[rows][cols];
        XSSFCell cell;
        
        DataFormatter formatter = new DataFormatter();
        
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                
                cell = excelSheet.getRow(i).getCell(j);
                String cellContents = formatter.formatCellValue(cell);
                if(cellContents.isEmpty()){
                    break;
                }
                data[i][j] = cellContents;
                System.out.println(data[i][j]);
            }
        }
        fis.close();
    }
}
