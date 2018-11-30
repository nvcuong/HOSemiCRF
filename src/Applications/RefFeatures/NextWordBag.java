package Applications.RefFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Next word features within a segment
 * @author Nguyen Viet Cuong
 */
public class NextWordBag extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
            obs.add("NWB." + seq.x(i + 1));
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
