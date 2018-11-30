package OCR.Features;

import java.util.*;
import HOCRF.*;

/**
 * Third order transition features
 * @author Nguyen Viet Cuong
 */
public class ThirdOrderTransition extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int pos) {
        ArrayList<String> obs = new ArrayList<String>();
        if (pos >= 3) {
            obs.add("3E.");
        }
        return obs;
    }

    public int order() {
        return 3;
    }
}
