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
import java.text.*;

/**
 * Scorer class
 * @author Nguyen Viet Cuong
 * @author Ye Nan
 */
public class Scorer {

    String[][] labels; // True labels
    String[][] predicted; // Predicted labels

    /**
     * Construct a scorer with true data, predicted data, and label map.
     * @param trueData List of correct sequences
     * @param predictedData List of predicted sequences
     * @param labelmap Label map
     * @param RM_SUFFIX If set to true, suffixes of labels after '-' will be removed
     */
    public Scorer(ArrayList<DataSequence> trueData, ArrayList<DataSequence> predictedData, LabelMap labelmap, boolean RM_SUFFIX) {
        labels = new String[trueData.size()][];
        for (int i = 0; i < trueData.size(); i++) {
            DataSequence seq = trueData.get(i);
            labels[i] = labelmap.revArray(seq.labels);
        }
        
        predicted = new String[predictedData.size()][];
        for (int i = 0; i < predictedData.size(); i++) {
            DataSequence seq = predictedData.get(i);
            predicted[i] = labelmap.revArray(seq.labels);
        }
        
        if (RM_SUFFIX) {
            removeSuffix(labels);
            removeSuffix(predicted);
        }
    }
    
    /**
     * Print the scores based on the correct phrases.
     * @return F1 score
     */
    public double phraseScore() {
        HashMap<String, Integer> labelht = new HashMap<String, Integer>();
        ArrayList<String> labs = new ArrayList<String>();
        collectLabels(labels, predicted, labelht, labs);

        double nTokens = 0;
        double nMatched = 0;

        double[] nPhrase = new double[labelht.size()];
        double[] nPredicted = new double[labelht.size()];
        double[] nCorrect = new double[labelht.size()];
        for (int s = 0; s < labels.length; s++) {
            nTokens += labels[s].length;
            String prev = "";
            for (int t = 0; t < labels[s].length; t++) {
                if (labels[s][t].equals(predicted[s][t])) {
                    nMatched++;
                }

                if (!labels[s][t].equals(prev)) {
                    prev = labels[s][t];
                    if (!labels[s][t].equals("O")) {
                        nPhrase[labelht.get(labels[s][t])]++;
                    }
                }
            }
        }

        for (int s = 0; s < predicted.length; s++) {
            String prev = "O";
            int start = -1, end = -1;
            for (int t = 0; t < predicted[s].length; t++) {
                if (!predicted[s][t].equals(prev)) { //token changed
                    if (!prev.equals("O")) { //phrase ended
                        end = t;
                        if (!(start > 0 && labels[s][start - 1].equals(labels[s][start]))
                                && !(end < labels[s].length && labels[s][end - 1].equals(labels[s][end]))) {
                            int i = start;
                            for (; i < end; i++) {
                                if (!predicted[s][i].equals(labels[s][i])) {
                                    break;
                                }
                            }
                            if (i == end) {
                                nCorrect[labelht.get(predicted[s][start])]++;
                            }
                        }
                    }

                    prev = predicted[s][t];
                    if (!prev.equals("O")) { //mark beginning of phrase
                        start = t;
                        nPredicted[labelht.get(prev)]++;
                    }
                }

                if (t == predicted[s].length - 1 && !predicted[s][t].equals("O")) {
                    int i = start;
                    for (; i <= t; i++) {
                        if (!predicted[s][i].equals(labels[s][i])) {
                            break;
                        }
                    }
                    if (i == t + 1) {
                        nCorrect[labelht.get(predicted[s][start])]++;
                    }
                }
            }
        }

        double nTotCorrect = sum(nCorrect);
        double nTotPhrase = sum(nPhrase);
        double nTotPredicted = sum(nPredicted);
        System.out.println((int) nTokens + " tokens with " + (int) nTotPhrase + " phrases.");
        System.out.println((int) nTotPredicted + " predicted phrases with " + (int) nTotCorrect + " being correct.");

        DisplayTable table = new DisplayTable();
        Object[] row1 = {"", "Precision", "#Pred", "Recall", "#Phrase", "F1", "#Correct"};
        table.addRow(row1);

        String strP = strRatio(nTotCorrect, nTotPredicted);
        String strR = strRatio(nTotCorrect, nTotPhrase);
        String strF1 = strRatio(2 * nTotCorrect, nTotPredicted + nTotPhrase);
        Object[] row2 = {"Accuracy: " + strRatio(nMatched, nTokens), strP, (int) nTotPredicted, strR, (int) nTotPhrase, strF1, (int) nTotCorrect};
        table.addRow(row2);
        for (int c = 0; c < labelht.size(); c++) {
            strP = strRatio(nCorrect[c], nPredicted[c]);
            strR = strRatio(nCorrect[c], nPhrase[c]);
            strF1 = strRatio(2 * nCorrect[c], nPredicted[c] + nPhrase[c]);
            Object[] row = {labs.get(c), strP, (int) nPredicted[c], strR, (int) nPhrase[c], strF1, (int) nCorrect[c]};
            table.addRow(row);
        }
        System.out.println(table);
        return ratio(2 * nTotCorrect, nTotPredicted + nTotPhrase);
    }
	
    /**
     * Print the scores based on the correct tokens.
     * @return F1 score
     */
    public double tokenScore() {
        HashMap<String, Integer> labelht = new HashMap<String, Integer>();
        ArrayList<String> labs = new ArrayList<String>();
        collectLabels(labels, predicted, labelht, labs);

        double nTokens = 0;
        double nMatched = 0;
        
        double[] nToken = new double[labelht.size()];
        double[] nPredicted = new double[labelht.size()];
        double[] nCorrect = new double[labelht.size()];
        for (int s = 0; s < labels.length; s++) {
            for (int t = 0; t < labels[s].length; t++) {
                nTokens++;
                if (labels[s][t].equals(predicted[s][t])) {
                    nMatched++;
                    if (!labels[s][t].equals("O")) {
                        nCorrect[labelht.get(labels[s][t])]++;
                    }
                }
                if (!labels[s][t].equals("O")) {
                    nToken[labelht.get(labels[s][t])]++;
                }
                if (!predicted[s][t].equals("O")) {
                    nPredicted[labelht.get(predicted[s][t])]++;
                }
            }
        }

        double nTotCorrect = sum(nCorrect);
        double nTotToken = sum(nToken);
        double nTotPredicted = sum(nPredicted);
        System.out.println((int) nTokens + " tokens with " + (int) nTotToken + " relevant tokens (not equal to O).");
        System.out.println((int) nTotPredicted + " predicted tokens with " + (int) nTotCorrect + " being correct.");

        DisplayTable table = new DisplayTable();
        Object[] row1 = {"", "Precision", "#Pred", "Recall", "#Phrase", "F1", "#Correct"};
        table.addRow(row1);

        String strP = strRatio(nTotCorrect, nTotPredicted);
        String strR = strRatio(nTotCorrect, nTotToken);
        String strF1 = strRatio(2 * nTotCorrect, nTotPredicted + nTotToken);
        Object[] row2 = {"Accuracy: " + strRatio(nMatched, nTokens), strP, (int) nTotPredicted, strR, (int) nTotToken, strF1, (int) nTotCorrect};
        table.addRow(row2);
        for (int c = 0; c < labelht.size(); c++) {
            strP = strRatio(nCorrect[c], nPredicted[c]);
            strR = strRatio(nCorrect[c], nToken[c]);
            strF1 = strRatio(2 * nCorrect[c], nPredicted[c] + nToken[c]);
            Object[] row = {labs.get(c), strP, (int) nPredicted[c], strR, (int) nToken[c], strF1, (int) nCorrect[c]};
            table.addRow(row);
        }
        System.out.println(table);
        return ratio(2 * nTotCorrect, nTotPredicted + nTotToken);
    }
    
    /**
     * Score on sequence level.
     * @return Accuracy at sequence level
     */
    public double sentenceScore() {
        HashMap<String, Integer> labelht = new HashMap<String, Integer>();
        ArrayList<String> labs = new ArrayList<String>();
        collectLabels(labels, predicted, labelht, labs);
        double nMatched = 0;
        for (int s = 0; s < labels.length; s++) {
            boolean isMatched = true;
            for (int t = 0; t < labels[s].length; t++) {
                if (!labels[s][t].equals(predicted[s][t])) {
                    isMatched = false;
                    break;
                }
            }
            if (isMatched) {
                nMatched++;
            }
        }
        System.out.println("Sentence accuracy = " + strRatio(nMatched, labels.length));
        return ratio(nMatched, labels.length);
    }
    
    /**
     * Macro-averaged accuracy score.
     * @return Macro-averaged accuracy
     */
    public double macroAccuracyScore() {
        HashMap<String, Integer> labelht = new HashMap<String, Integer>();
        ArrayList<String> labs = new ArrayList<String>();
        collectLabels(labels, predicted, labelht, labs);

        double[] nToken = new double[labelht.size()];
        double[] nCorrect = new double[labelht.size()];
        for (int s = 0; s < labels.length; s++) {
            for (int t = 0; t < labels[s].length; t++) {
                if (!labels[s][t].equals("O")) {
                    nToken[labelht.get(labels[s][t])]++;
                    if (labels[s][t].equals(predicted[s][t])) {
                        nCorrect[labelht.get(labels[s][t])]++;
                    }
                }
            }
        }

        DisplayTable table = new DisplayTable();
        Object[] row1 = {"", "Acc", "#Phrase", "#Correct"};
        table.addRow(row1);

        double totalAcc = 0.0;
        for (int c = 0; c < labelht.size(); c++) {
            String strAcc = strRatio(nCorrect[c], nToken[c]);
            totalAcc += ratio(nCorrect[c], nToken[c]);
            Object[] row = {labs.get(c), strAcc, (int) nToken[c], (int) nCorrect[c]};
            table.addRow(row);
        }
        System.out.println(table);
        System.out.println("Averaged accuracy = " + strRatio(totalAcc, labelht.size()));
        return ratio(totalAcc, labelht.size());
    }
    
    private void collectLabels(String[][] labels, String[][] predicted, HashMap<String,Integer> labelht, ArrayList<String> labs) {
        for (int s = 0; s < labels.length; s++) {
            for (int t = 0; t < labels[s].length; t++) {
                if (!labels[s][t].equals("O") && !labelht.containsKey(labels[s][t])) {
                    labelht.put(labels[s][t], labelht.size());
                    labs.add(labels[s][t]);
                }

                if (!predicted[s][t].equals("O") && !labelht.containsKey(predicted[s][t])) {
                    labelht.put(predicted[s][t], labelht.size());
                    labs.add(predicted[s][t]);
                }
            }
        }
        Collections.sort(labs);
        for (int i = 0; i < labs.size(); i++) {
            labelht.put(labs.get(i), i);
        }
    }
	
    private String strRatio(double a, double b) {
        double r = 0;
        if (b != 0) {
            r = 100 * a / b;
        }
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(r) + "%";
    }

    private double ratio(double a, double b) {
        if (b != 0) {
            return a / b;
        }
        return 0;
    }
	
    private double sum(double[] ar) {
        double s = 0;
        for (double d : ar) {
            s += d;
        }
        return s;
    }
	
    private void removeSuffix(String[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j].lastIndexOf('-') != -1) {
                    arr[i][j] = arr[i][j].substring(0, arr[i][j].lastIndexOf('-'));
                }
            }
        }
    }
}

/**
 * Class for displaying tables
 * @author Ye Nan
 */
class DisplayTable {

    ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>(); // Rows of the table

    DisplayTable() {
    }

    void addRow(Object[] entries) {
        ArrayList<String> row = new ArrayList<String>();
        for (int i = 0; i < entries.length; i++) {
            row.add(entries[i].toString());
        }
        rows.add(row);
    }
	
    String format(String s, int w, String align) {
        int n = w - s.length();
        if (align == "l") {//align to the left
            for (int i = 0; i < n; i++) {
                s += " ";
            }
        } else {//align to the right
            for (int i = 0; i < n; i++) {
                s = " " + s;
            }
        }
        return s;
    }

    @Override
    public String toString() {
        int ncols = 0;
        int nrows = rows.size();
        for (int r = 0; r < nrows; r++) {
            if (rows.get(r).size() > ncols) {
                ncols = rows.get(r).size();
            }
        }

        for (int r = 0; r < nrows; r++) {
            int s = rows.get(r).size();
            if (s != ncols) {
                for (int i = 0; i < ncols - s; i++) {
                    rows.get(r).add("");
                }
            }
        }
        int[] colWidths = new int[ncols];
        for (int c = 0; c < ncols; c++) {
            for (int r = 0; r < nrows; r++) {
                int w = rows.get(r).get(c).length();
                if (w > colWidths[c]) {
                    colWidths[c] = w;
                }
            }
        }

        StringBuffer sb = new StringBuffer();
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                String align = "r";
                if (c == ncols - 1) {
                    align = "l";
                }
                sb.append(format(rows.get(r).get(c), colWidths[c], align) + "\t  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
