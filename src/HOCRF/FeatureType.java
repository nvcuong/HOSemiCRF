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
