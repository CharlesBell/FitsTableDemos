import java.io.*;
import nom.tam.fits.*;

/** 
	Demo program to create a simple binary fits table 
	using the nom.tam.fits java package 
	Compiling and running generates  
		"DemoBinaryTable.fits"
	in the same directory as where the program is run from
	No package name was assigned to make this simple. 
	nom.tam FITS library 1.14.3-SNAPSHOT API
	Java docs:  https://heasarc.gsfc.nasa.gov/docs/heasarc/fits/java/v1.0/javadoc/
	
*/
public class WriteFitsTableDemo {


	public static void main(String[] args){
		try{
			// creating two empty double arrays 
			// which will become columns in a FitsBinaryTable
			double[] x = new double[1000];
			double[] y = new double[1000];
			
			String[] columnNames = {"x","sin(x)"};
			// create an empty BinaryTable
			BinaryTable table = new BinaryTable();
			// add columns 
			table.addColumn(x);
			table.addColumn(y);
			// fill the arrary for each column for a  
			// simple x , f(x) relation 
			for (int i=0;i<x.length;i++){
				// since index i is an int, must cast to double to define x as a double  
				double nextX = 0.01d * (double)i;
				x[i] = nextX;
				y[i] = Math.sin(nextX);
			}
			// if you want to be generated somewhere besides the current
			// working directory, then modify the file contructor.
			String tableName = "DemoBinaryTable.fits";
			File file = new File(tableName);
			// when creating a new Fits object use the emply constructor 
			Fits fits = new Fits();
			// The next data constructor takes arrays of primitives like x and y and 
			// creates an object array that FitsFactory uses to create the HDU 
			Object[] data = new Object[]{x,y};
			// if the next line is left out, you will get an AsciiTable
			FitsFactory.setUseAsciiTables(false);
			// The next constructor casts a BasicHDU as a BinaryTableHDU object
			// the java library inserts key, value/comments for TTYPE1, and TTYPE2
			// into the header that it creates for the data
			BinaryTableHDU tableHDU = (BinaryTableHDU) FitsFactory.hduFactory(data);
			// The java library does not create column names for the column labels
			// so use setColumnName() method to insert them into header
			for (int i = 0;i<columnNames.length;i++){			
				tableHDU.setColumnName(i, columnNames[i], String.valueOf("label for field " + i)) ;
			}
			// if you want to see what is in the header, uncomment the following line 
			// tableHDU.getHeader().dumpHeader(System.out);
			// the nom.tam.fits hava library creates a primary header if it has not already been done
			fits.addHDU(tableHDU);
			// now write the fits table to file 
			fits.write(file);
			// now use a FitsTable view application like fv or TopCat to view the new fits table
			// check out the primary header and 2nd header key/value/comment line
			// you should be able to easily plot sin(x) versus x using  fv 
			
		}catch(FitsException e){
			System.err.println("FitsException: " + e.getMessage());
		}catch(IOException e){
			System.err.println("IOException: " + e.getMessage());
		}
	}
}
