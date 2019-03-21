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
        
        ArrayList<DataSequence> td = new ArrayList<>();
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
