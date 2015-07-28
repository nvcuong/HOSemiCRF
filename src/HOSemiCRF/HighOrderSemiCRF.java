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

import optimization.FirstOrderDiffFunction;
import optimization.SVRGMinimizer;
import Parallel.*;

/**
 * High-order semi-CRF class
 * @author Nguyen Viet Cuong
 */
public class HighOrderSemiCRF {

    FeatureGenerator featureGen; // Feature generator
    double[] lambda; // Feature weight vector
	
    /**
     * Construct and initialize a high-order semi-CRF from feature generator.
     * @param fgen Feature generator
     */
    public HighOrderSemiCRF(FeatureGenerator fgen) {
        featureGen = fgen;
        lambda = new double[featureGen.featureMap.size()];
        Arrays.fill(lambda, 0.0);
    }

    /**
     * Train a high-order semi-CRF from data.
     * @param data Training data
     */
    public void train(ArrayList<DataSequence> data) {
    	// use library to do minimization
//        QNMinimizer qn = new QNMinimizer();
//        Function df = new Function(featureGen, data);
//        lambda = qn.minimize(df, featureGen.params.epsForConvergence, lambda, featureGen.params.maxIters);
        
        FirstOrderDiffFunction func = new FirstOrderDiffFunction(featureGen, data);
        SVRGMinimizer svrg = new SVRGMinimizer();
        lambda = svrg.minimize(func, lambda, featureGen.params.getLearningRate(), featureGen.params.maxIters, featureGen.params.epsForConvergence);
    }

    /**
     * Run Viterbi algorithm on testing data.
     * @param data Testing data
     */
    public void runViterbi(ArrayList<DataSequence> data) throws Exception {
        Viterbi tester = new Viterbi(featureGen, lambda, data);
        Scheduler sch = new Scheduler(tester, featureGen.params.numthreads, Scheduler.DYNAMIC_NEXT_AVAILABLE);
        sch.run();
    }
	
    /**
     * Write the high-order semi-CRF to a file.
     * @param filename Name of the output file
     */
    public void write(String filename) throws Exception {
        PrintWriter out = new PrintWriter(new FileOutputStream(filename));
        out.println(lambda.length);
        for (int i = 0; i < lambda.length; i++) {
            out.println(lambda[i]);
        }
        out.close();
    }

    /**
     * Read the high-order semi-CRF from a file.
     * @param filename Name of the input file
     */
    public void read(String filename) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        int featureNum = Integer.parseInt(in.readLine());
        lambda = new double[featureNum];
        for (int i = 0; i < featureNum; i++) {
            String line = in.readLine();
            lambda[i] = Double.parseDouble(line);
        }
        in.close();
    }
}
