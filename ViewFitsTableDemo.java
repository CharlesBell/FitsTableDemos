import java.io.*;
import nom.tam.fits.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.GridLayout;
/** 
	Demo program to read binary fits table 
	created with WriteFitsTableDemo
		"DemoBinaryTable.fits"
	It reads in the binaryfits table to the fits object 
	Reads the column names in the fits header table 
	Reads the elements in the data table 
	Creates JFrame, JTable, and JScrollPane to view data in a scrollable viewer.
	The data can be copied from the viewer and pasted elsewhere.
	No package name was assigned to make this simple. 
	nom.tam FITS library 1.14.3-SNAPSHOT API
	Java docs:  https://heasarc.gsfc.nasa.gov/docs/heasarc/fits/java/v1.0/javadoc/
*/

public class ViewFitsTableDemo extends JPanel{

	ViewFitsTableDemo(){
		super(new GridLayout(1,0));

		try{
			String tableName = "DemoBinaryTable.fits";
			File file = new File(tableName);
			Fits fits = new Fits(file);
			// the file has to be read into the fits object 
			fits.read();
			int	nHDUs = fits.getNumberOfHDUs(); 
			//System.out.println(tableName + " has " + nHDUs + "HDUs or Header-Data Units");
			for (int i = 0; i < nHDUs; i++) {
				//System.out.println("Reading HDU " + i);
				BasicHDU nextHDU = 	fits.getHDU(i) ;
				Header nextHeader = nextHDU.getHeader();

				if (i == 0){
					//System.out.println("dumping the primary header to System.out");
					//nextHeader.dumpHeader(System.out);
				}else if (i == 1){
					String extension = nextHeader.getStringValue("XTENSION");
				    //System.out.println("XTENSION = " + 	extension);
					if (extension.equals("BINTABLE")){
						//System.out.println("Found BINTABLE");
						TableHDU tableHDU = (TableHDU) fits.getHDU(1);
						int nCols = tableHDU.getNCols();
						int nRows = tableHDU.getNRows();
						//System.out.println("Number of rows: " + 	nRows);
						//System.out.println("Number of columns: " + 	nCols);
						String[] columNames = new String[nCols];
						Object[] firstRow = tableHDU.getRow(0) ;
						for (int col = 0;col<nCols;col++){
							columNames[col] = tableHDU.getColumnName(col);
						}
						Object[][] data = new Object[nRows][nCols];
						// now looping through each row of data
						for (int n = 0;n < nRows;n++){
							Object[] row = tableHDU.getRow(n) ;
							for (int c =0;c < row.length;c++){
								String columnFormat = tableHDU.getColumnFormat(c);
								//System.out.println("columnFormat: "  + columnFormat);
								if (columnFormat.contains("D")){
									double[] element =  (double[]) row[c];
									// D  format is for double 
									data[n][c] = element[0];
								}else if (columnFormat.contains("A")){
									// A format  ascii text string
									String s = (String) row[c];
									data[n][c] = s;
									
								}else if (columnFormat.equals("E")){
									// E  format is for exponential
									float[] element =  (float[]) row[c];
									data[n][c] = element[0];
								}else if (columnFormat.equals("F")){
									// F  format is for float 
									float[] element =  (float[]) row[c];
									data[n][c] = element[0];
								}else {
									//System.out.println("unknown columnFormat: "  + columnFormat);
									//System.out.println(row[c]);
									Object element = row[c];
									data[n][c] = element;
								}

							}
						}
						
						JTable table = new JTable(data, columNames);
						table.setPreferredScrollableViewportSize(new Dimension(500, 500));
						table.setFillsViewportHeight(true);
						//Create the scroll pane and add the table to it.
						JScrollPane scrollPane = new JScrollPane(table);
				 
						//Add the scroll pane to this panel.
						add(scrollPane);

					}
				}
			}

		}catch(FitsException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println(e.getMessage());
		}

	}
	
	    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ViewFitsTableDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        ViewFitsTableDemo newContentPane = new ViewFitsTableDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
