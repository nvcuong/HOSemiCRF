package Applications.RefFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Semi-Markov first order transition features
 * @author Nguyen Viet Cuong
 */
public class FirstOrderTransition extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        if (segStart >= 1) {
            obs.add("1E.");
        }
        return obs;
    }

    public int order() {
        return 1;
    }
}
