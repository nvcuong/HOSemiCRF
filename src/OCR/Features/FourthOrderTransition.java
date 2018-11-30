package OCR.Features;

import java.util.*;
import HOCRF.*;

/** 
 * Fourth order transition features
 * @author Nguyen Viet Cuong
 */
public class FourthOrderTransition extends FeatureType {

    public ArrayList<String> generateObsAt(DataSequence seq, int pos) {
        ArrayList<String> obs = new ArrayList<String>();
        if (pos >= 4) {
            obs.add("4E.");
        }
        return obs;
    }

    public int order() {
        return 4;
    }
}
