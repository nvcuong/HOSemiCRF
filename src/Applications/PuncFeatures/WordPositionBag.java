package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Current word and position features
 * @author Nguyen Viet Cuong
 */
public class WordPositionBag extends FeatureType {

    static final int K = 5;

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
            for (int j = i; j < i + K && j <= seq.length(); j++) {
                obs.add("WPB." + seq.x(j) + "." + (j - i));
            }
            for (int j = i - 1; j > i - K && j >= -1; j--) {
                obs.add("WPB." + seq.x(j) + "." + (j - i));
            }
        }
        return obs;
    }
	
    public int order() {
        return 0;
    }
}
