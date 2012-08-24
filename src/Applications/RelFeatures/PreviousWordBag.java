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

package Applications.RelFeatures;

import java.util.*;
import HOSemiCRF.*;
import Applications.*;

/**
 * Previous word features within a segment
 * @author Nguyen Viet Cuong
 */
public class PreviousWordBag extends FeatureType {
	
	public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
			try {
				obs.add("PWB." + ((WordDetails) seq.x(i - 1)).getWord());
			} catch (ClassCastException e) {
				obs.add("PWB." + (new WordDetails()).getWord());
			}
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
