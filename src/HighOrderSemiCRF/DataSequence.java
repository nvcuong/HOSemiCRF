package HighOrderSemiCRF;

import java.util.*;

/**
 * Class for a data sequence
 * @author Nguyen Viet Cuong
 * @author Sumit Bhagwani
 */
public class DataSequence {

	public Object[] inputs; // Observation array
    public int[] labels; // Label array
    public int[] startPos; // Start of a segment
	public int[] endPos; // End of a segment
    public ArrayList<Integer>[][] observationMap; // [startPos, endPos] -> List of observation ID using obsMap
    
	/**
	 * Construct a data sequence from labels and observations with default segmentation.
	 * @param ls Label array
	 * @param inps Observation array
	 */
	public DataSequence(int[] ls, Object[] inps) {
        labels = ls;
        inputs = inps;

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
	 * Return the list of observations for a subsequence.
	 * @param segStart Start position of the subsequence
	 * @param segEnd End position of the subsequence
	 * @return List of observation IDs
	 */
	public ArrayList<Integer> getObservation(int segStart, int segEnd) {
		return observationMap[segStart][segEnd];
	}
}
