/*
Copyright (C) 2012 Nguyen Viet Cuong, Ye Nan, Sumit Bhagwani

This file is part of HOSemiCRF.

HOSemiCRF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

HOSemiCRF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with HOSemiCRF. If not, see <http://www.gnu.org/licenses/>.
*/

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
