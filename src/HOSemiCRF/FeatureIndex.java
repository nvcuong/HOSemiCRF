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

/**
 * Index of a feature
 * @author Nguyen Viet Cuong
 */
public class FeatureIndex {

    int obsID; // ID of the observation part
	int patID; // ID of the pattern part

	/**
	 * Construct a feature index from observation and pattern IDs.
	 * @param obsID Observation ID
	 * @param patID Pattern ID
	 */
    public FeatureIndex(int obsID, int patID) {
		this.obsID = obsID;
		this.patID = patID;
    }
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeatureIndex that = (FeatureIndex) o;
        if (obsID != that.obsID) return false;
        if (patID != that.patID) return false;

        return true;
    }
	
    @Override
    public int hashCode() {
        int result = 23;
		result = result*31 + obsID;
		result = result*31 + patID;
		return result;
    }
}
