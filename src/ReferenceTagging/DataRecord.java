package ReferenceTagging;

import java.io.*;
import HighOrderSemiCRF.*;

/**
 * Class for one data sequence
 * @author Nguyen Viet Cuong
 * @author Sumit Bhagwani
 */
class DataRecord extends DataSequence {

    LabelMap labelmap; // Map from labels to their IDs

	/**
	* Construct a data sequence from a sequence of observations, labels, and a label map.
	* @param ls Label sequence
	* @param inps Observation sequence
	* @param labelm Label map
	*/
    DataRecord(int[] ls, Object[] inps, LabelMap labelm) {
        super(ls, inps);
        labelmap = labelm;
    }
    
	/**
	* Write a data sequence to a buffered writer.
	* @param bw Buffered writer
	*/
    void writeToBuffer(BufferedWriter bw) throws Exception {
        for (int i = 0; i < labels.length; i++) {                
            bw.write(x(i) + " " + labelmap.revMap(labels[i]) + "\n");
        }
    }
}
