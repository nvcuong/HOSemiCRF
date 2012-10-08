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

package OCR.Features;

import java.util.*;
import HOCRF.*;

/**
 * Fifth order transition features
 * @author Nguyen Viet Cuong
 */
public class FifthOrderTransition extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int pos) {
        ArrayList<String> obs = new ArrayList<String>();
        if (pos >= 5) {
            obs.add("5E.");
        }
        return obs;
    }

    public int order() {
        return 5;
    }
}
