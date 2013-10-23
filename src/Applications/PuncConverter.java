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

package Applications;

import java.io.*;
import java.util.*;
import HOSemiCRF.*;

/**
 * Class for converting the punctuation labels
 * @author Nguyen Viet Cuong
 */
public class PuncConverter {
	
    /**
     * Convert punctuations of a dataset.
     * @param inFilename Name of the original dataset file
     * @param outFilename Name of the new dataset file
     */
    public static void convert(String inFilename, String outFilename) throws Exception {
        LabelMap labelmap = new LabelMap();
        DataSet data = readInFile(inFilename, labelmap);
        data.writeToFile(outFilename);
    }

    /**
     * Read the original file and convert labels.
     * @param filename Name of the input file
     * @param labelmap Label map
     * @return The training data with new labels
     */
    static DataSet readInFile(String filename, LabelMap labelmap) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        
        ArrayList<DataSequence> td = new ArrayList<DataSequence>();
        ArrayList<String> inps = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();
        String line;
		
        while ((line = in.readLine()) != null) {
            if (line.length() > 0) {
                StringTokenizer toks = new StringTokenizer(line);
                String word = toks.nextToken();
                String tagRel = toks.nextToken();
                inps.add(word);
                labels.add(tagRel);
            } else if (labels.size() > 0) {
                changeLabel(labels);
                td.add(new DataSequence(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
                inps = new ArrayList<String>();
                labels = new ArrayList<String>();
            }
        }
        if (labels.size() > 0) {
            changeLabel(labels);
            td.add(new DataSequence(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
        }

        in.close();
        return new DataSet(td);
    }

    /**
     * Change the labels of a sequence.
     * @param labels Label sequence
     */
    static void changeLabel(ArrayList<String> labels) {
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).matches("[,.!?]")) {
                String last = labels.get(i);
                int j = i;
                while (j > 0 && labels.get(j-1).equals("O")) {
                    j--;
                }
                labels.set(j, labels.get(j) + "-" + last);
            }
        }
    }
}
