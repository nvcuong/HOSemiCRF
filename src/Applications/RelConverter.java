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
 * Class for converting the relations into training file
 * @author Nguyen Viet Cuong
 */
public class RelConverter {

	/**
	 * Create the training file for relation extraction task.
	 * @param inFilename The original traning file
	 * @param outFilename The new training file
	 * @param relation The relation to be extracted
	 * @param BALANCE Balance the extracted training data
	 */
	public static void convert(String inFilename, String outFilename, int relation, boolean BALANCE) throws Exception {
        if (BALANCE) {
			ArrayList<DataSequence> posSeqs = new ArrayList<DataSequence>();
            ArrayList<DataSequence> negSeqs = new ArrayList<DataSequence>();
            readInFile(inFilename, relation, posSeqs, negSeqs);
            removeNegatives(posSeqs, negSeqs);
            posSeqs.addAll(negSeqs);
            DataSet finalData = new DataSet(posSeqs);
            finalData.writeToFile(outFilename);
        } else {
            removeRelations(inFilename, outFilename, relation);
        }
    }
	
	private static void readInFile(String filename, int relation, ArrayList<DataSequence> posSeqs, ArrayList<DataSequence> negSeqs) throws Exception {
		Dictionary dict = new Dictionary();
		LabelMap labelmap = new LabelMap();
		
        BufferedReader in = new BufferedReader(new FileReader(filename));
        ArrayList<WordDetails> inps = new ArrayList<WordDetails>();
        ArrayList<String> labels = new ArrayList<String>();

        String line;
        while ((line = in.readLine()) != null) {
            if (line.length() > 0) {
                String[] toks = line.split("[ \t]");
                inps.add(new WordDetails(toks[0], toks[1], toks[2], toks[3], dict));
                labels.add(toks[4 + relation]);
            } else if (labels.size() > 0) {
                boolean isPositive = changeLabel(inps, labels);
                if (isPositive) {
                    posSeqs.add(new DataSequence(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
                } else {
                    negSeqs.add(new DataSequence(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
                }
                inps = new ArrayList<WordDetails>();
                labels = new ArrayList<String>();
            }
        }
        if (labels.size() > 0) {
            boolean isPositive = changeLabel(inps, labels);
            if (isPositive) {
                posSeqs.add(new DataSequence(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
            } else {
                negSeqs.add(new DataSequence(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
            }
        }

        in.close();
    }
	
	private static void removeRelations(String filename, String outfile, int relation) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, false));
		
        String line;
        while ((line = in.readLine()) != null) {
            if (line.length() > 0) {
                String[] toks = line.split("[ \t]");
                bw.write(toks[0] + " " + toks[1] + " " + toks[2] + " " + toks[3] + " ");
				/*
				// Use this if set the labels in changeLabel(...)
				int index = toks[4 + relation].indexOf('-');
                bw.write(toks[4 + relation].substring(index + 1) + "\n");
				*/
				bw.write(toks[4 + relation] + "\n");
            } else {
                bw.write("\n");
            }
        }
		
        bw.close();
        in.close();
    }
	
	private static void removeNegatives(ArrayList<DataSequence> posSeqs, ArrayList<DataSequence> negSeqs) {
        Random rand = new Random(2147483646);
        int total = negSeqs.size();
        int removeNum = negSeqs.size() - posSeqs.size();
        for (int i = 0; i < removeNum; i++) {
            int rm = rand.nextInt(total);
            negSeqs.remove(rm);
            total--;
        }
    }
	
	private static boolean changeLabel(ArrayList<WordDetails> inps, ArrayList<String> labels) {
        String prevArg = null;
        String argNE = "";
        for (int i = 0; i < labels.size(); i++) {
            String tagRel = labels.get(i);
            if (tagRel.charAt(0) == 'O') {
                tagRel = "O" + argNE;
            } else if (tagRel.charAt(0) == 'R') {
                tagRel = tagRel.substring(tagRel.indexOf('-') + 1);
                String tagNE = inps.get(i).getNE();
                if (tagNE.contains("-")) {
                    tagNE = tagNE.substring(tagNE.indexOf('-'));
                }
                if (prevArg == null) {
                    prevArg = new String(tagRel);
                    argNE = tagNE;
                } else if (!prevArg.equals(tagRel)) {
                    argNE = "";
                }
                tagRel = tagRel + tagNE;
            }
			// Set this label to include NE tags to labels.
			// If set, need to change removeRelations(...) as well.
            // labels.set(i, tagRel);
        }
        return (prevArg != null);
    }
}
