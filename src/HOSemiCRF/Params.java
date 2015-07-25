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

import java.io.*;
import java.util.*;

/**
 * Parameters class
 * @author Nguyen Viet Cuong
 */
public class Params {

    int numLabels; // Number of labels
    int maxIters = 100; // Number of training iterations
    int numthreads = 1; // Number of parallel threads
    int maxSegment = -1; // Maximum segment length
    double invSigmaSquare = 1.0; // Inverse of Sigma Squared
    double epsForConvergence = 0.001; // Convergence Precision

    /**
     * Construct a parameters object.
     * @param filename Name of configuration file
     * @param nl Number of labels
     */
    public Params(String filename, int nl) throws IOException {
    	Properties options = new Properties();
        options.load(new FileInputStream(filename));
        String value = null;
        if ((value = options.getProperty("maxIters")) != null) {
            maxIters = Integer.parseInt(value);
        }
        if ((value = options.getProperty("numthreads")) != null) {
            numthreads = Integer.parseInt(value);
        }
        if ((value = options.getProperty("maxSegment")) != null) {
            maxSegment = Integer.parseInt(value);
        }
        if ((value = options.getProperty("invSigmaSquare")) != null) {
            invSigmaSquare = Double.parseDouble(value);
        }
        if ((value = options.getProperty("epsForConvergence")) != null) {
            epsForConvergence = Double.parseDouble(value);
        }
        numLabels = nl;
    }

    /**
     * 
     * @return
     */
	public int getNumLabels() {
		return numLabels;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxIters() {
		return maxIters;
	}

	/**
	 * 
	 * @return
	 */
	public int getNumthreads() {
		return numthreads;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxSegment() {
		return maxSegment;
	}

	/**
	 * 
	 * @return
	 */
	public double getInvSigmaSquare() {
		return invSigmaSquare;
	}

	/**
	 * 
	 * @return
	 */
	public double getEpsForConvergence() {
		return epsForConvergence;
	}
}
