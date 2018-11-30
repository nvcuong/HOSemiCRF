package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Edge and previous word features within a segment
 * @author Nguyen Viet Cuong
 */
public class EdgePreviousWordBag extends FeatureType {
	
    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart + 1; i <= segEnd; i++) {
            obs.add("EPWB." + seq.x(i - 1));
        }
        return obs;
    }
	
    public int order() {
        return 0;
    }
}
