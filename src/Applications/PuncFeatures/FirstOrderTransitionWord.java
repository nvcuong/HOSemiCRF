package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Semi-Markov first order transition with word features
 * @author Nguyen Viet Cuong
 */
public class FirstOrderTransitionWord extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        if (segStart >= 1) {
            for (int i = segStart; i <= segEnd; i++) {
                obs.add("E1W." + seq.x(i));
            }
        }
        return obs;
    }

    public int order() {
        return 1;
    }
}
