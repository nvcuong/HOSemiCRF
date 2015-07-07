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

import java.io.BufferedWriter;

/**
 * Class for a data sequence
 * @author Nguyen Viet Cuong
 * @author Sumit Bhagwani
 */
public class DataSequence {
	
    Object[] inputs; // Observation array
    int[] labels; // Label array
    int[] startPos; // Start of a segment
    int[] endPos; // End of a segment
    int[][][] observationMap; // [startPos, segLength] -> List of observation ID using obsMap
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

        // Compute start positions
        startPos = new int[labels.length];
        startPos[0] = 0;
        for (int i = 1; i < labels.length; i++) {
            if (labels[i] == labels[i - 1]) {
                startPos[i] = startPos[i - 1];
            } else {
                startPos[i] = i;
            }
        }

        // Compute end positions
        endPos = new int[labels.length];
        endPos[labels.length - 1] = labels.length - 1;
        for (int i = labels.length - 2; i >= 0; i--) {
            if (labels[i] == labels[i + 1]) {
                endPos[i] = endPos[i + 1];
            } else {
                endPos[i] = i;
            }
        }
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
     * Return the start position of the segment that includes a given position.
     * @param pos Input position
     * @return Start position of the segment that includes the input position
     */
    public int getSegmentStart(int pos) {
        return startPos[pos];
    }

    /**
     * Return the end position of the segment that includes a given position.
     * @param pos Input position
     * @return End position of the segment that includes the input position
     */
    public int getSegmentEnd(int pos) {
        return endPos[pos];
    }

    /**
     * Set the label of a segment.
     * @param startPos Start position of the segment
     * @param endPos End position of the segment
     * @param newY New label to be set for the segment
     */
    public void setSegment(int startPos, int endPos, int newY) {
        for (int i = startPos; i <= endPos; i++) {
            set_y(i, newY);
        }
    }

    /**
     * Check if a subsequence is a segment.
     * @param startPos Start position of the subsequence
     * @param endPos End position of the subsequence
     * @return true if the subsequence is a segment, false otherwise
     */
    public boolean isSegment(int startPos, int endPos) {
        int y = y(startPos);
        for (int i = startPos + 1; i <= endPos; i++) {
            if (y(i) != y) {
                return false;
            }
        }
        return true;
    }
	
    /**
     * Return the maximum length of any segment in the sequence.
     * @return Maximum segment length
     */
    public int getMaxSegLength() {
        int maxSeg = 0, segStart, segEnd;
        for (segStart = 0; segStart < length(); segStart = segEnd + 1) {
            segEnd = getSegmentEnd(segStart);
            if (segEnd - segStart + 1 > maxSeg) {
                maxSeg = segEnd - segStart + 1;
            }
        }
        return maxSeg;
    }
	
    /**
     * Return the observations for a subsequence.
     * @param segStart Start position of the subsequence
     * @param segEnd End position of the subsequence
     * @return Array of observation IDs
     */
    public int[] getObservation(int segStart, int segEnd) {
        return observationMap[segStart][segEnd-segStart];
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
