package Applications.RefFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Edge features between consecutive segments and current words
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
