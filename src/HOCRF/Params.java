package HOCRF;

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
        if ((value = options.getProperty("invSigmaSquare")) != null) {
            invSigmaSquare = Double.parseDouble(value);
        }
        if ((value = options.getProperty("epsForConvergence")) != null) {
            epsForConvergence = Double.parseDouble(value);
        }
        numLabels = nl;
    }
}
