package ReferenceTagging;

import java.util.*;
import java.io.*;
import HighOrderSemiCRF.*;

/**
 * Class for a dataset
 * @author Nguyen Viet Cuong
 */
class DataSet {

    ArrayList<DataRecord> trainRecs; // List of all data sequences

	/**
	* Construct a dataset from a list of data sequences.
	* @param trs List of data sequences
	*/
    DataSet(ArrayList<DataRecord> trs) {
        trainRecs = trs;
    }

	/**
	* Get the list of data sequences in the dataset.
	* @return List of data sequences
	*/
    ArrayList<DataRecord> getRecordList() {
        return trainRecs;
    }

	/**
	* Write the dataset to a file.
	* @param filename Name of the output file
	*/
    void writeToFile(String filename) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename, false));
        for (int i = 0; i < trainRecs.size(); i++) {
            trainRecs.get(i).writeToBuffer(bw);
            if (i < trainRecs.size() - 1) {
                bw.write("\n");
            }
        }
        bw.close();
    }   
}
