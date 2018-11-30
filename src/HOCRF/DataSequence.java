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
