package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Semi-Markov second order transition features
 * @author Nguyen Viet Cuong
 */
public class SecondOrderTransition extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        if (segStart >= 2) {
            obs.add("2E.");
        }
        return obs;
    }

    public int order() {
        return 2;
    }
}
