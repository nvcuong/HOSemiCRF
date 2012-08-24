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
