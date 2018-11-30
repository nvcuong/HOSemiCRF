package OCR.Features;

import java.util.*;
import HOCRF.*;
import OCR.*;

/**
 * Pixel features
 * @author Nguyen Viet Cuong
 */
public class Pixel extends FeatureType {
    
    public ArrayList<String> generateObsAt(DataSequence seq, int pos) {
        ArrayList<String> obs = new ArrayList<String>();
        CharDetails cd = (CharDetails) seq.x(pos);
        for (int r = 0; r < CharDetails.ROWS; r++) {
            for (int c = 0; c < CharDetails.COLS; c++) {
                if (cd.getPixels(r, c) != 0) {
                    obs.add(r + "." + c);
                }
            }
        }
        return obs;
    }

    public int order() {
        return 0;
    }
}
