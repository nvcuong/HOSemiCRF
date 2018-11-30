package Applications.PuncFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Two words around the current word and their positions feature
 * @author Nguyen Viet Cuong
 */
public class TwoWordPositionBag extends FeatureType {
    
    static final int K = 4;

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
            for (int j = -K; j < K; j++) {
                for (int k = j + 1; k <= K; k++) {
                    if (i + j >= 0 && i + j <= seq.length() && i + k >= 0 && i + k <= seq.length()) {
                        obs.add("2WPB." + seq.x(i + j) + "." + seq.x(i + k) + "." + j + "." + k);
                    }
                }
            }
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
