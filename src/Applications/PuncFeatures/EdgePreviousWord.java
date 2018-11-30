package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Edge and previous features between two consecutive segments
 * @author Nguyen Viet Cuong
 */
public class EdgePreviousWord extends FeatureType {
	
    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        if (segStart > 0) {
            obs.add("EPW." + seq.x(segStart - 1));
        }
        return obs;
    }
	
    public int order() {
        return 1;
    }
}
