package OCR.Features;

import java.util.*;
import HOCRF.*;

/**
 * Second order transition features
 * @author Nguyen Viet Cuong
 */
public class SecondOrderTransition extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int pos) {
        ArrayList<String> obs = new ArrayList<String>();
        if (pos >= 2) {
            obs.add("2E.");
        }
        return obs;
    }

    public int order() {
        return 2;
    }
}
