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

import Parallel.*;

/**
 * Generator class for the features in each sequence
 * @author Nguyen Viet Cuong
 * @author Sumit Bhagwani
 */
public class SentenceFeatGenerator implements Schedulable {

    int curID; // Current task ID (for parallelization)
    ArrayList<DataSequence> trainData; // List of training sequences
    FeatureGenerator featGen; // Feature generator

    /**
     * Construct a generator for the features.
     * @param data Training data
     * @param fgen Feature generator
     */
    public SentenceFeatGenerator(ArrayList<DataSequence> data, FeatureGenerator fgen) {
        curID = -1;
        trainData = data;
        featGen = fgen;
    }

    /**
     * Compute the features for all the positions in a given sequence.
     * @param taskID Index of the training sequence
     * @return The updated sequence
     */
    @SuppressWarnings("unchecked")
	public Object compute(int taskID) {
        DataSequence seq = trainData.get(taskID);
        seq.features = new ArrayList[seq.length()][featGen.patternMap.size()];
        
        for (int pos = 0; pos < seq.length(); pos++) {
            for (int patID = 0; patID < featGen.patternMap.size(); patID++) {
                seq.features[pos][patID] = new ArrayList<Integer>();
                ArrayList<String> obs = featGen.generateObs(seq, pos);
                for (String o : obs) {
                    Integer oID = featGen.getObsIndex(o);
                    if (oID != null) {
                        Integer feat = (Integer) featGen.featureMap.get(new FeatureIndex(oID, patID));
                        if (feat != null) {
                            seq.features[pos][patID].add(feat);
                        }
                    }
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
