import java.io.*;
import nom.tam.fits.*;

/** 
	Demo program to read binary fits table 
	created with WriteFitsTableDemo
		"DemoAsciiTable.fits"
	It reads in the binaryfits table to the fits object 
	Dumps each header and reads the elements in the data table 
	No package name was assigned to make this simple. 
	nom.tam FITS library 1.14.3-SNAPSHOT API
	Java docs:  https://heasarc.gsfc.nasa.gov/docs/heasarc/fits/java/v1.0/javadoc/
*/

public class ReadFitsTableDemo2 {


	public static void main(String[] args){
		try{
			String tableName = "DemoAsciiTable.fits";
			File file = new File(tableName);
			Fits fits = new Fits(file);
			// the file has to be read into the fits object 
			fits.read();
			int	nHDUs = fits.getNumberOfHDUs(); 
			System.out.println(tableName + " has " + nHDUs + "HDUs or Header-Data Units");
			for (int i = 0; i < nHDUs; i++) {
				System.out.println("Reading HDU " + i);
				BasicHDU nextHDU = 	fits.getHDU(i) ;
				Header nextHeader = nextHDU.getHeader();

				if (i == 0){
					System.out.println("dumping the primary header to System.out");
					nextHeader.dumpHeader(System.out);
				}else{
					System.out.println("dumping the next header to System.out");
					nextHeader.dumpHeader(System.out);
					// checking of the  value of key XTENSION in the header
					String extension = nextHeader.getStringValue("XTENSION");
				    System.out.println("XTENSION = " + 	extension);
					if (extension.equals("TABLE")){
						System.out.println("Found ascii TABLE");
						TableHDU tableHDU = (TableHDU) fits.getHDU(1);
						int nCols = tableHDU.getNCols();
						int nRows = tableHDU.getNRows();
						System.out.println("Number of rows: " + 	nRows);
						System.out.println("Number of columns: " + 	nCols);
						// now looping through each row of data
						for (int n = 0;n < nRows;n++){
							// the fits library can read in a row of data of unknown types
							// and turn  it into an array of Objects 
							// the header contains the number of bytes of each row.
							// BITPIX  =                    8 / bits per data value                            
							// NAXIS   =                    2 / number of axes                                 
							// NAXIS1  =                   16 / size of the n'th axis                          
							// NAXIS2  =                 1000 / size of the n'th axis   
							// TFIELDS =                    2 / Number of table fields                         
							// data rows contain two fields  16 bytes wide since there are 
							// 8 bytes for each double 
							// there are 1000 rows each 16 bytes wide
							Object[] nextRow = tableHDU.getRow(n) ;
							for (int nextColumn =0;nextColumn < nextRow.length;nextColumn++){
								//Printing out the row as an object  would give strange characters
								// the format string of each column is the key to deciphering
								String columnFormat = tableHDU.getColumnFormat(nextColumn);
								//System.out.println("columnFormat: "  + columnFormat);
								if (columnFormat.contains("D")){
									// each object in the row is an array of length 1
									// the D format gives the key to deciphering the object at
									// next column index, so it can be cast as a double array. 
                                    // this seems unnecessary but it is per the fits standard									
									double[] element =  (double[]) nextRow[nextColumn];
									// D  format is for double 
									System.out.println("element: " + n + " " + nextColumn  + " " 
										+ tableHDU.getColumnName(nextColumn) + " =  "+ element[0]);

								}else if (columnFormat.contains("A")){
									// int formatLength = Integer.parseInt(columnFormat.substring(0, columnFormat.indexOf("A")));
									// System.out.println("formatLength: " + 	formatLength);
									//the A format gives the key to deciphering an ascii text string
									//String[] element =  (String[]) nextRow[p];
									String s = (String) nextRow[nextColumn];
									System.out.println("element: " + n + " " + nextColumn  + " " 
										+ tableHDU.getColumnName(nextColumn) + " =  "+ s);
									
								}else if (columnFormat.equals("E")){
									// E  format is for exponential
									float[] element =  (float[]) nextRow[nextColumn];
									System.out.println("element: " + n + " " + nextColumn  + " " 
										+ tableHDU.getColumnName(nextColumn) + " =  "+ element[0]);
								}else if (columnFormat.equals("F")){
									// F  format is for float 
									float[] element =  (float[]) nextRow[nextColumn];
									System.out.println("element: " + nextColumn + " " + nextColumn  + " " 
										+ tableHDU.getColumnName(nextColumn) + " =  "+ element[0]);
								}else {
									System.out.println("unknown columnFormat: "  + columnFormat);
									System.out.println(nextRow[nextColumn]);
								}

							}
						}
					}
				}
			}

		}catch(FitsException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println(e.getMessage());
		}

	}
}
