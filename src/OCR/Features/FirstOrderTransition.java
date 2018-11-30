package OCR.Features;

import java.util.*;
import HOCRF.*;

/**
 * First order transition features
 * @author Nguyen Viet Cuong
 */
public class FirstOrderTransition extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int pos) {
        ArrayList<String> obs = new ArrayList<String>();
        if (pos >= 1) {
            obs.add("1E.");
        }
        return obs;
    }

    public int order() {
        return 1;
    }
}

