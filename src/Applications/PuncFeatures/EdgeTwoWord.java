package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Edge and two consecutive words between two segments
 * @author Nguyen Viet Cuong
 */
public class EdgeTwoWord extends FeatureType {
	
    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        if (segStart > 0) {
            obs.add("ETW." + seq.x(segStart - 1) + "." + seq.x(segStart));
        }
        return obs;
    }
	
    public int order() {
        return 1;
    }
}
