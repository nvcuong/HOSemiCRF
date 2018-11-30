package Applications.RefFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Edge features within a segment
 * @author Nguyen Viet Cuong
 */
public class EdgeBag extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart + 1; i <= segEnd; i++) {
            obs.add("EB.");
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
