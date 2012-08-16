package ReferenceTagging.Features;

import java.util.*;
import HighOrderSemiCRF.*;

/**
 * Words from 2nd to 4th position before a position within a segment
 * @author Nguyen Viet Cuong
 */
public class WordKPositionBeforeBag extends FeatureType {

    static final int K = 5;

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
            for (int j = i - 1; j > i - K && j >= -1; j--) {
                obs.add("WKBB." + seq.x(j));
            }
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
