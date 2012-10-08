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

package HOCRF;

import java.util.*;
import edu.stanford.nlp.optimization.*;
import Parallel.*;

/**
 * Loglikelihood function class
 * @author Nguyen Viet Cuong
 */
public class Function implements DiffFunction {

    FeatureGenerator featureGen; // Feature generator
    ArrayList trainData; // List of training sequences
	
    // Private data structures to compute function value and derivatives
    private Loglikelihood logli; // Loglikelihood values
    private double lambdaCache[]; // Cache of lambda vector for reuse

    /**
     * Construct a function from feature generator and data.
     * @param fgen Feature generator
     * @param data Training data
     */
    public Function(FeatureGenerator fgen, ArrayList data) {
        featureGen = fgen;        
        trainData = data;
        lambdaCache = null;
    }

    /**
     * Return the dimension of the domain.
     * @return Domain dimension
     */
    public int domainDimension() {
        return featureGen.featureMap.size();
    }

    /**
     * Return the loglikelihood given a lambda vector.
     * @param lambda Lambda vector
     * @return Loglikelihood value
     */
    public double valueAt(double[] lambda) {
        if (Arrays.equals(lambda, lambdaCache)) {
            return logli.logli;
        } else {
            lambdaCache = (double[]) lambda.clone();
            computeValueAndDerivatives(lambda);
            return logli.logli;
        }
    }

    /**
     * Return the first derivative of the loglikelihood function given a lambda vector.
     * @param lambda Lambda vector
     * @return First derivatives
     */
    public double[] derivativeAt(double[] lambda) {
        if (Arrays.equals(lambda, lambdaCache)) {
            return logli.derivatives;
        } else {
            lambdaCache = (double[]) lambda.clone();
            computeValueAndDerivatives(lambda);
            return logli.derivatives;
        }
    }	

    /**
     * Compute the values and derivatives of the loglikelihood function.
     * @param lambda Lambda vector
     */
    public void computeValueAndDerivatives(double[] lambda) {        
        logli = new Loglikelihood(lambda.length);
        for (int i = 0; i < lambda.length; i++) {
            logli.logli -= ((lambda[i] * lambda[i]) * featureGen.params.invSigmaSquare) / 2;
            logli.derivatives[i] -= (lambda[i] * featureGen.params.invSigmaSquare);
        }

        LogliComputer logliComp = new LogliComputer(lambda, featureGen, trainData, logli);
        Scheduler sch = new Scheduler(logliComp, featureGen.params.numthreads, Scheduler.DYNAMIC_NEXT_AVAILABLE);
        try {
            sch.run();
        } catch (Exception e) {
            System.out.println("Errors occur when training in parallel! " + e);
        }

        // Change sign to maximize and divide the values by size of dataset        
        int n = trainData.size();
        for (int i = 0; i < logli.derivatives.length; i++) {
            logli.derivatives[i] = -(logli.derivatives[i] / n);
        }
        logli.logli = -(logli.logli / n);
    }	
}
