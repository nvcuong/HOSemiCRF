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

package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Two words around the current word and their positions feature
 * @author Nguyen Viet Cuong
 */
public class TwoWordPositionBag extends FeatureType {
    
    static final int K = 4;

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
            for (int j = -K; j < K; j++) {
                for (int k = j + 1; k <= K; k++) {
                    if (i + j >= 0 && i + j <= seq.length() && i + k >= 0 && i + k <= seq.length()) {
                        obs.add("2WPB." + seq.x(i + j) + "." + seq.x(i + k) + "." + j + "." + k);
                    }
                }
            }
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
