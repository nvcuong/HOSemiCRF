package Applications.RefFeatures;

import java.util.*;
import HOSemiCRF.*;

/**
 * Letter n-grams of words within a segment
 * @author Nguyen Viet Cuong
 */
public class LetterNGramsBag extends FeatureType {

    static final int K = 6;

    public ArrayList<String> generateObsAt(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (int i = segStart; i <= segEnd; i++) {
            String word = (String) seq.x(i);
            for (int N = 2; N <= K; N++) {
                ArrayList<String> ngrams = letterNGrams(word, N);
                int c = ngrams.size();
                for (int j = 0; j < c; j++) {
                    obs.add("LNGB." + ngrams.get(j));
                }
            }
        }
        return obs;
    }

    public int order() {
        return 0;
    }

    ArrayList<String> letterNGrams(String word, int N) {
        word = "<" + word + ">";
        ArrayList<String> ngrams = new ArrayList<String>();
        int l = word.length() - N + 1;
        for (int i = 0; i < l; i++) {
            if (i != 0 && i + N != l) {
                continue;
            }
            ngrams.add("#" + word.substring(i, i + N) + "#");
        }
        return ngrams;
    }
}
