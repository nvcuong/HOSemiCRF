package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Semi-Markov second order transition with word features
 * @author Nguyen Viet Cuong
 */
public class SecondOrderTransitionWord extends FeatureType {
	
    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        if (segStart >= 2) {
            for (int i = segStart; i <= segEnd; i++) {
                obs.add("E2W." + seq.x(i));
            }
        }
        return obs;
    }
	
    public int order() {
        return 2;
    }
}
