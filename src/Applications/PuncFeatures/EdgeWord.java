package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Edge and word features between two consecutive segments
 * @author Nguyen Viet Cuong
 */
public class EdgeWord extends FeatureType {
	
    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        if (segStart > 0) {
            obs.add("EW." + seq.x(segStart));
        }
        return obs;
    }
	
    public int order() {
        return 1;
    }
}
