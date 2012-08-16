package ReferenceTagging.Features;

import java.util.*;
import HighOrderSemiCRF.*;

/**
 * Previous word features within a segment
 * @author Nguyen Viet Cuong
 */
public class PreviousWordBag extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
            obs.add("PWB." + seq.x(i - 1));
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
