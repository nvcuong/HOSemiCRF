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

import java.io.*;
import java.util.*;

/**
 * Class for a data sequence
 * @author Nguyen Viet Cuong
 */
public class DataSequence {
	
    Object[] inputs; // Observation array
    int[] labels; // Label array
    ArrayList<Integer>[][] features; // Map from [pos,patID] to list of feature IDs
    LabelMap labelmap; // Map from label strings to their IDs
    
    /**
     * Construct a data sequence from a label map, labels and observations with default segmentation.
     * @param ls Label array
     * @param inps Observation array
     * @param labelm Label map
     */
    public DataSequence(int[] ls, Object[] inps, LabelMap labelm) {
        labels = ls;
        inputs = inps;
        labelmap = labelm;
    }

    /**
     * Return length of the current data sequence.
     * @return Length of the current sequence
     */
    public int length() {
        return labels.length;
    }

    /**
     * Return label at a position.
     * @param pos Input position
     * @return Label at the input position
     */
    public int y(int pos) {
        return labels[pos];
    }

    /**
     * Return observation at a position.
     * @param pos Input position
     * @return Observation at the input position
     */
    public Object x(int pos) {
        if (pos < 0 || pos >= inputs.length) {
            return "";
        }
        return inputs[pos];
    }

    /**
     * Set the label at an input position.
     * @param pos Input position
     * @param newY New label to be set at the input position
     */
    public void set_y(int pos, int newY) {
        labels[pos] = newY;
    }
    
    /**
     * Return the label map of this sequence.
     * @return The label map.
     */
    public LabelMap getLabelMap() {
        return labelmap;
    }
	
    /**
     * Return the list of features at a position and a label pattern.
     * @param pos Input position
     * @param patID Pattern ID
     * @return List of features
     */
    public ArrayList<Integer> getFeatures(int pos, int patID) {
        return features[pos][patID];
    }
	
    /**
     * Write a data sequence to a buffered writer.
     * @param bw Buffered writer
     */
    public void writeToBuffer(BufferedWriter bw) throws Exception {
        for (int i = 0; i < labels.length; i++) {                
            bw.write(x(i) + " " + labelmap.revMap(labels[i]) + "\n");
        }
    }
}
