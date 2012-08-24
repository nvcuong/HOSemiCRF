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

package Applications.RefFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Letter n-grams of words within a segment
 * @author Nguyen Viet Cuong
 */
public class LetterNGramsBag extends FeatureType {

    static final int K = 6;

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
            String word = (String) seq.x(i);
            for (int N = 2; N <= K; N++) {
                ArrayList<String> ngrams = letterNGrams(word, N);
                int c = ngrams.size();
                for (int j = 0; j < c; j++) {
                    obs.add("LNGB." + ngrams.get(j));
                }
            }
        }
        return obs;
    }

    public int order() {
        return 0;
    }

    ArrayList<String> letterNGrams(String word, int N) {
        word = "<" + word + ">";
        ArrayList<String> ngrams = new ArrayList<String>();
        int l = word.length() - N + 1;
        for (int i = 0; i < l; i++) {
            if (i != 0 && i + N != l) {
                continue;
            }
            ngrams.add("#" + word.substring(i, i + N) + "#");
        }
        return ngrams;
    }
}
