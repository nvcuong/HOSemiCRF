package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Edge features between two consecutive segments
 * @author Nguyen Viet Cuong
 */
public class Edge extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        if (segStart > 0) {
            obs.add("E.");
        }
        return obs;
    }

    public int order() {
        return 1;
    }
}
