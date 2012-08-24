/*
Copyright (C) 2012 Nguyen Viet Cuong, Ye Nan, Sumit Bhagwani

This file is part of HOSemiCRF.

HOSemiCRF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

HOSemiCRF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with HOSemiCRF. If not, see <http://www.gnu.org/licenses/>.
*/

package HOSemiCRF;

import java.util.*;

/**
 * Implementations of the utility methods
 * @author Nguyen Viet Cuong
 */
public class Utility {

    /**
     * Return ln(exp(a) + exp(b)) for two real numbers a and b.
	 * @param a First real number
	 * @param b Second real number
	 * @return ln(exp(a) + exp(b))
     */
    public static double logSumExp(double a, double b) {
        if (a == Double.NEGATIVE_INFINITY) {
            return b;
        } else if (b == Double.NEGATIVE_INFINITY) {
            return a;
        } else if (a > b) {
            return a + Math.log(1 + Math.exp(b - a));
        } else {
            return b + Math.log(1 + Math.exp(a - b));
        }
    }
    
	/**
	 * Generate all proper prefixes of a label pattern.
	 * @param labelPat Label pattern
	 * @return List of proper prefixes
	 */
    public static ArrayList<String> generateProperPrefixes(String labelPat) {
        String pats = new String(labelPat);
        ArrayList<String> res = new ArrayList<String>();
        while (pats.contains("|")) {
            pats = pats.substring(pats.indexOf('|') + 1);
            if (pats.contains("|")) {
                res.add(pats);
            }
        }
        return res;
    }

	/**
	 * Generate all suffixes of a label pattern.
	 * @param labelPat Label pattern
	 * @return List of suffixes
	 */
    public static ArrayList<String> generateSuffixes(String labelPat) {
        String pats = new String(labelPat);
        ArrayList<String> res = new ArrayList<String>();
        res.add(pats);
        while (pats.contains("|")) {
            pats = pats.substring(0, pats.lastIndexOf("|"));
            res.add(pats);
        }
        return res;
    }

	/**
	 * Return the last label in a label pattern.
	 * @param labelPat Label pattern
	 * @return The last label in the pattern
	 */
    public static String getLastLabel(String labelPat) {
        String pats = new String(labelPat);
        if (pats.contains("|")) {
            return pats.substring(0, pats.indexOf("|"));
        } else {
            return pats;
        }
    }   

	/**
	 * Return the order of a label pattern.
	 * @param labelPat Label pattern
	 * @return The order of the pattern
	 */
    public static int getOrder(String labelPat) {
        int res = 0;
        String pats = new String(labelPat);
        while (pats.contains("|")) {
            pats = pats.substring(pats.indexOf('|') + 1);
            res++;
        }
        return res;
    }
}
