package HOCRF;

import java.util.*;
import java.io.*;

/**
 * Class for a dataset
 * @author Nguyen Viet Cuong
 */
public class DataSet {

    ArrayList<DataSequence> trainSeqs; // List of all data sequences

    /**
     * Construct a dataset from a list of data sequences.
     * @param trs List of data sequences
     */
    public DataSet(ArrayList<DataSequence> trs) {
        trainSeqs = trs;
    }

    /**
     * Get the list of data sequences in the dataset.
     * @return List of data sequences
     */
    public ArrayList<DataSequence> getSeqList() {
        return trainSeqs;
    }

    /**
     * Write the dataset to a file.
     * @param filename Name of the output file
     */
    public void writeToFile(String filename) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename, false));
        for (int i = 0; i < trainSeqs.size(); i++) {
            trainSeqs.get(i).writeToBuffer(bw);
            if (i < trainSeqs.size() - 1) {
                bw.write("\n");
            }
        }
        bw.close();
    }
}
