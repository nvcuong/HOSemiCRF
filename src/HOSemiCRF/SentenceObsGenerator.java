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
 * Generator class for the observations in each sequence
 * @author Nguyen Viet Cuong
 * @author Sumit Bhagwani
 */
public class SentenceObsGenerator implements Schedulable {

    int curID; // Current task ID (for parallelization)
    ArrayList<DataSequence> trainData; // List of training sequences
    FeatureGenerator featGen; // Feature generator

    /**
     * Construct a generator for the observations.
     * @param data Training data
     * @param fgen Feature generator
     */
    public SentenceObsGenerator(ArrayList<DataSequence> data, FeatureGenerator fgen) {
        curID = -1;
        trainData = data;
        featGen = fgen;
    }

    /**
     * Compute the observations for all the subsequences in a given sequence.
     * @param taskID Index of the training sequence
     * @return The updated sequence
     */
    public Object compute(int taskID) {
        DataSequence seq = (DataSequence) trainData.get(taskID);
        seq.observationMap = new int[seq.length()][][];
        
        for (int segStart = 0; segStart < seq.length(); segStart++) {
            int maxLength = Math.min(featGen.params.maxSegment, seq.length() - segStart);
            seq.observationMap[segStart] = new int[maxLength][];
            for (int segEnd = segStart; segEnd - segStart < maxLength; segEnd++) {
                ArrayList<Integer> obsIndices = new ArrayList<Integer>();
                ArrayList<String> obs = featGen.generateObs(seq, segStart, segEnd);
                for (String o : obs) {
                    Integer oID = featGen.getObsIndex(o);
                    if (oID != null) {
                        obsIndices.add(oID);
                    }
                }
                
                int d = segEnd-segStart;
                seq.observationMap[segStart][d] = new int[obsIndices.size()];
                for (int i = 0; i < obsIndices.size(); i++) {
                    seq.observationMap[segStart][d][i] = obsIndices.get(i);
                }
            }
        }
		
        return seq;
    }

    /**
     * Return the number of tasks (for parallelization).
     * @return Training data size
     */
    public int getNumTasks() {
        return trainData.size();
    }

    /**
     * Return the next task ID (for parallelization).
     * @return Index of the next sequence
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
    public synchronized void update(Object partialResult) {
        // Do nothing
    }
}
