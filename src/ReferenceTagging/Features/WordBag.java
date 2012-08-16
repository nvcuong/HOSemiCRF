package ReferenceTagging.Features;

import java.util.*;
import HighOrderSemiCRF.*;

/**
 * Current word features within a segment
 * @author Nguyen Viet Cuong
 */
public class WordBag extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
            obs.add("WB." + seq.x(i));
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
