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

package HOSemiCRF;

import java.util.*;
import Parallel.*;

/**
 * Implementation of the Viterbi algorithm
 * @author Nguyen Viet Cuong
 * @author Sumit Bhagwani
 */
public class Viterbi implements Schedulable {

    int curID; // Current task ID (for parallelization)
    FeatureGenerator featureGen; // Feature generator
    double[] lambda; // Lambda vector
    ArrayList<DataSequence> data; // List of training sequences
    final int BASE = 1; // Base of the logAlpha array

    /**
     * Construct a Viterbi class.
     * @param featureGen Feature generator
     * @param lambda Lambda vector
     * @param data Training data
     */
    public Viterbi(FeatureGenerator featureGen, double[] lambda, ArrayList<DataSequence> data) {
        curID = -1;
        this.featureGen = featureGen;
        this.lambda = lambda;
        this.data = data;
    }
    
    /**
     * Run the Viterbi algorithm for a given sequence.
     * @param taskID Index of the sequence
     * @return The updated sequence
     */
    public Object compute(int taskID) {
        DataSequence seq =  data.get(taskID);
        double maxScore[][] = new double[seq.length() + 1][featureGen.forwardStateMap.size()];
        String trace[][] = new String[seq.length()][featureGen.forwardStateMap.size()];

        Arrays.fill(maxScore[0], Double.NEGATIVE_INFINITY);
        maxScore[0][0] = 0.0;
        for (int j = 0; j < seq.length(); j++) {
            Arrays.fill(maxScore[j + BASE], Double.NEGATIVE_INFINITY);
            for (int i = 0; i < featureGen.forwardStateMap.size(); i++) {
                int y = featureGen.lastForwardStateLabel[i];
                int maxmem = (y == -1) ? 0 : featureGen.maxMemory[y];
	               
                ArrayList<Integer> prevState1 = featureGen.forwardTransition1[i];
                ArrayList<Integer> prevState2 = featureGen.forwardTransition2[i];
                for (int d = 0; d < maxmem && j - d >= 0; d++) {
                    for (int k = 0; k < prevState1.size(); k++) {
                        int pkID = prevState1.get(k);
                        int pkyID = prevState2.get(k);
                        String pky = featureGen.backwardStateList.get(pkyID);
                        ArrayList<Feature> features = featureGen.generateFeatures(seq, j-d, j, pky);
                        ArrayList<Integer> feats = featureGen.getFeatureID(features);
                        double featuresScore = featureGen.computeFeatureScores(feats, lambda);
                        if (maxScore[j + BASE][i] < featuresScore + maxScore[j + BASE - d - 1][pkID]) {
                            maxScore[j + BASE][i] = featuresScore + maxScore[j + BASE - d - 1][pkID];
                            trace[j][i] = (j - d - 1) + " " + pkID + " " + y;
                        }
                    }
                }
            }
        }
        
        // Compute max score for last element
        double max = Double.NEGATIVE_INFINITY;
        String tracemax = "";
        for (int i = 0; i < featureGen.forwardStateMap.size(); i++) {
            if (max < maxScore[seq.length() + BASE - 1][i]) {
                max = maxScore[seq.length() + BASE - 1][i];
                tracemax = trace[seq.length() - 1][i];
            }
        }

        // Trace back
        int currPos = seq.length() - 1;
        while (currPos >= 0) {
            StringTokenizer toks = new StringTokenizer(tracemax);
            int prevPos = Integer.parseInt(toks.nextToken());
            int prevPat = Integer.parseInt(toks.nextToken());
            int currY = Integer.parseInt(toks.nextToken());
            seq.setSegment(prevPos + 1, currPos, currY);
            currPos = prevPos;
            if (currPos >= 0) {
                tracemax = trace[prevPos][prevPat];
            }
        }

        return seq;
    }
    
    /**
     * Return total number of tasks (for parallelization).
     * @return Training dataset size
     */
    public int getNumTasks() {
        return data.size();
    }

    /**
     * Return the next task ID (for parallelization).
     * @return The next sequence ID
     */
    public synchronized int fetchCurrTaskID() {
        if (curID < getNumTasks()) {
            curID++;
        }
        return curID;
    }

    /**
     * Update partial result (for parallelization).
     * Note that this method does nothing in this case.
     * @param partialResult Partial result
     */
    public void update(Object partialResult) {
        // Do nothing
    }
}
