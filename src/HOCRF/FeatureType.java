package HOCRF;

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
     * Return the list of observations at a position.
     * @param seq Data sequence
     * @param pos Input position
     * @return List of observations
     */
    public abstract ArrayList<String> generateObsAt(DataSequence seq, int pos);

    /**
     * Generate the features activated at a position and a label pattern.
     * @param seq Data sequence
     * @param pos Input position
     * @param labelPat Label pattern of the features
     * @return List of activated features
     */
    public ArrayList<Feature> generateFeaturesAt(DataSequence seq, int pos, String labelPat) {
        ArrayList<Feature> features = new ArrayList<Feature>();
        if (Utility.getOrder(labelPat) == order()) {
            ArrayList<String> obs = generateObsAt(seq, pos);
            for (String o : obs) {
                features.add(new Feature(o, labelPat, 1.0));
            }
        }
        return features;
    }
}
