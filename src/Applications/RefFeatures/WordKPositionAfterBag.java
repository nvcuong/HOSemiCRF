package Applications.RefFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Words from 2nd to 4th position after a position within a segment
 * @author Nguyen Viet Cuong
 */
public class WordKPositionAfterBag extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
            for (int j = i + 1; j < i + WordKPositionBeforeBag.K && j <= seq.length(); j++) {
                obs.add("WKAB." + seq.x(j));
            }
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
