package ai.creyield.nonCoreMapping;

import java.io.*;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


//some code taken from: https://howtodoinjava.com/library/readingwriting-excel-files-in-java-poi-tutorial/#reading_excel_file


//Program that takes files with data arranged in different ways input from the command line, 
//extracts specific data, and exports to an excel sheet where the data can be viewed uniformly
public class nonCoreMapping {

    public static void main(String[] args) throws IOException {

        //name of the file to read from
        String filename = args[0];
        
        //name of state for file naming
        String state_name = args[1];
        
        //state ID
        int state_id = Integer.parseInt(args[2]);
        
        //sheet number if workbook contains multiple sheets [first sheet is 0]
        int sheet_num = Integer.parseInt(args[3]);
        
        //column in the specified sheet that details core or nonCore field [A = 0, B = 1, etc.]
        int coreNonCoreCell = Integer.parseInt(args[4]);
        
        //column number with field name seen in file [A = 0, B = 1, etc.]
        int fieldNameInFile = Integer.parseInt(args[5]);
        
        //column number for proposed field names [A = 0, B = 1, etc.]
        int proposedFieldName = Integer.parseInt(args[6]);
        
        //jurisdiction ID if applicable, -1 if not
        int jurID = Integer.parseInt(args[7]);
        
        //row that data starts, [on the excel sheet: row number on the left - 2 (i.e. row 7, input 5)]
        int dataStart = Integer.parseInt(args[8]);
        
        //create a workbook reference to file to read from
        FileInputStream file = new FileInputStream(new File(filename));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        //reference to desired sheet from file
        XSSFSheet sheet = workbook.getSheetAt(sheet_num);

        //file to write to named by the state
        String writeTo = state_name + "_nonCores.txt";
        BufferedWriter bw = new BufferedWriter(new FileWriter(writeTo));

        //iterate through rows of the sheet
        Iterator<Row> rowIterator = sheet.iterator();
        
        //parse to start of the data
        Row row = rowIterator.next();
        
        for (int i = 0; i < dataStart; i++) {
        	row = rowIterator.next();
        }
        
        
        //parse through the data, ending where core/non core values end
        while(rowIterator.hasNext()) {	
        	
            //reference to current row
            row = rowIterator.next();
            
            System.out.print(row.getRowNum());
            
            //gets cell at col indicating if field is core or not
            Cell coreCell = row.getCell(coreNonCoreCell);
            if (coreCell != null) {
            	String content = coreCell.getStringCellValue();
            	
            	System.out.println(" " + content);
            	if (content == null || content.isEmpty()) {
            		break;
            	}
                
            	//if cell's content begins with 'N', value is nonCore
                if (content.charAt(0) == 'N') {
                	
                    String ogField = row.getCell(fieldNameInFile).getStringCellValue();
                    String newField = row.getCell(proposedFieldName).getStringCellValue();
                    
                    System.out.println(" " + ogField + " " + newField);
                    
                    if (jurID != -1) {
                        bw.write("insert into name_type_lookup (state_id,"
                        + " jurisdiction_id, name, description)");

                        bw.write(" values (" + state_id + ", "
                        + jurID + ", '" + ogField + "', '"
                        + newField + "')");
                    }
                    else { //write to file with no jurisdiction ID
                        bw.write("insert into name_type_lookup (state_id,"
                        + " name, description)");
                       
                        bw.write(" values (" + state_id + ", '"
                        + ogField + "', '" + newField + "')");
                        //System.out.println("Written to file.");
                    }
                    bw.write("\n");
                }
           
            }


        }
        bw.close();
        workbook.close();
        file.close();
    }
}
