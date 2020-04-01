//Liam Creedon, CREyield.ai

import java.io.*;
import java.util.Scanner;

//Customizable parser for breaking Alexandria assessment rolls into smaller files
//size of groups, n, is customizable by the command line
//file needs to be provided, and will be written to new files
public class Parser {

    //arg0 = filename
    //arg1 = n
    public static void main(String[] args) {

        //read in filename and n
        String filename = args[0];
        int numRecords = Integer.parseInt(args[1]);

        //error checking to ensure at least 1 record per file
        Scanner kb = new Scanner(System.in);
        while (numRecords < 1) {
            System.out.println("Please enter a number greater than 0.");
            numRecords = kb.nextInt();
        }
        //System.out.println("Filename: " + filename);
        //System.out.println("Records per: " + numRecords);

        try {
            //creates filereader and buffers it
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            //ensures unique filenames, updated at end of while loop
            int rec = 1;
            //tracks records per file
            int per = 0;
            //reads one line at a time
            String line = null;
            //value that indicates end of record
            String end = "},";
            //bool for last record indication
            boolean stop = false;

            //skips begin marker for sake of looping
            line = br.readLine();
            line = br.readLine();
            //line = br.readLine();

            //continue parse until the end of file
            while ((line = br.readLine()) != null) {

                //unique file name to write to
                String writeTo = "file" + Integer.toString(rec) + ".txt";

                //file writer
                BufferedWriter bw =
                    new BufferedWriter(new FileWriter(writeTo));

                //System.out.println("FILE MADE");

                //writes begin marker and first line for each file
                bw.write("{\n");
                bw.write("\"type\": \"FeatureCollection\",\n");
                bw.write("\"features\": [\n");
                bw.write(line + "\n");

                //System.out.println("BEGIN MARKER WRITTEN");

                //copies data from input file into new files
                do {
                    line = br.readLine();

                    //handles last record case and nullPointer
                    if (line == null) {
                        stop = true;
                        break;
                    }

                    //creates a string of last 3 chars of the line
                    int len = line.length();
                    String checkEnd = null;
                    if (len >= 2) {
                        checkEnd = line.substring(len-2, len);
                    } else {
                        checkEnd = line;
                    }

                    //System.out.println(checkEnd);

                    //checks for end marker
                    if (checkEnd.equals(end)) {
                        per++;

                        //last record reached
                        if (per == numRecords * 2) {
                            //creates the same line without a comma
                            String newLine = line.substring(0, len-1);
                            //System.out.println(newLine);
                            bw.write(newLine);
                        } else { //still more records to copy over
                            bw.write(line + "\n");
                        }

                    } else { //otherwise just copies info into file
                        bw.write(line + "\n");
                    }
                } while (per != numRecords * 2);

                //System.out.println("DATA WRITTEN");

                //writes end marker only when not the last record
                if (!stop) {
                    bw.write("]}");
                }

                //System.out.println("END MARKER WRITTEN");

                //resets records per file tracker
                per = 0;
                //updates number of parses for filenames
                rec++;
                //closes writer
                bw.close();
            }
        }

        //filename given in command line does not exist
        catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        }
        //some error
        catch (IOException ex) {
            System.out.println("Error reading file.");
        }
    }
}
