package HOSemiCRF;

import java.io.*;
import java.util.*;

/**
 * Parameters class
 * @author Nguyen Viet Cuong
 * @author tndoan
 */
public class Params {

    int numLabels; // Number of labels
    int maxIters = 100; // Number of training iterations
    int numthreads = 1; // Number of parallel threads
    int maxSegment = -1; // Maximum segment length
    double invSigmaSquare = 1.0; // Inverse of Sigma Squared
    double epsForConvergence = 0.001; // Convergence Precision
    
    /**
     * learning rate for first order minimizer
     */
    double learningRate = 0.1; // 

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
        if ((value = options.getProperty("learningRate")) != null){
        	learningRate = Double.parseDouble(value);
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

	/**
	 * 
	 * @return the value of learning rate
	 */
	public double getLearningRate() {
		return learningRate;
	}
}
