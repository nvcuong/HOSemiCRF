package ReferenceTagging.Features;

import java.util.*;
import HighOrderSemiCRF.*;

/**
 * Edge features within a segment and current words
 * @author Nguyen Viet Cuong
 */
public class EdgeWordBag extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart + 1; i <= segEnd; i++) {
            obs.add("EWB." + seq.x(i));
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
