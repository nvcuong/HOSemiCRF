package HighOrderSemiCRF;

import java.util.*;

/**
 * Abstract class for feature types
 * @author Nguyen Viet Cuong
 */
public abstract class FeatureType {

	/**
	 * Return the order of the feature type.
	 */
	public abstract int order();
	
	/**
	 * Return the list of observations in a subsequence.
	 * @param seq Data sequence
	 * @param segStart Start position of the subsequence
	 * @param segEnd End position of the subsequence
	 * @return List of observations
	 */
	public abstract ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd);

	/**
	 * Generate the features activated at a segment and a label pattern.
	 * @param seq Data sequence
	 * @param segStart Start position of the segment
	 * @param segEnd End position of the segment
	 * @param labelPat Label pattern of the features
	 * @return List of activated features
	 */
    public ArrayList<Feature> generateFeaturesAt(DataSequence seq, int segStart, int segEnd, String labelPat) {
		ArrayList<Feature> features = new ArrayList<Feature>();
        if (Utility.getOrder(labelPat) == order()) {
			ArrayList<String> obs = generateObsAt(seq, segStart, segEnd);
			for (String o : obs) {
				features.add(new Feature(o, labelPat, 1.0));
			}
        }
        return features;
	}
}
