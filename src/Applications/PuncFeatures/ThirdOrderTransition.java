package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Semi-Markov third order transition features
 * @author Nguyen Viet Cuong
 */
public class ThirdOrderTransition extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        if (segStart >= 3) {
            obs.add("3E.");
        }
        return obs;
    }

    public int order() {
        return 3;
    }
}
