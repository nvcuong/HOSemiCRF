package OCR.Features;

import java.util.*;
import HOCRF.*;

/**
 * Fifth order transition features
 * @author Nguyen Viet Cuong
 */
public class FifthOrderTransition extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int pos) {
        ArrayList<String> obs = new ArrayList<String>();
        if (pos >= 5) {
            obs.add("5E.");
        }
        return obs;
    }

    public int order() {
        return 5;
    }
}
