package HOCRF;

import java.io.*;
import java.util.*;
import edu.stanford.nlp.optimization.*;
import Parallel.*;

/**
 * High-order CRF class
 * @author Nguyen Viet Cuong
 */
public class HighOrderCRF {

    FeatureGenerator featureGen; // Feature generator
    double[] lambda; // Feature weight vector
	
    /**
     * Construct and initialize a high-order CRF from feature generator.
     * @param fgen Feature generator
     */
    public HighOrderCRF(FeatureGenerator fgen) {
        featureGen = fgen;
        lambda = new double[featureGen.featureMap.size()];
        Arrays.fill(lambda, 0.0);
    }

    /**
     * Train a high-order CRF from data.
     * @param data Training data
     */
    public void train(ArrayList data) {
        QNMinimizer qn = new QNMinimizer();
        Function df = new Function(featureGen, data);
        lambda = qn.minimize(df, featureGen.params.epsForConvergence, lambda, featureGen.params.maxIters);
    }

    /**
     * Run Viterbi algorithm on testing data.
     * @param data Testing data
     */
    public void runViterbi(ArrayList data) throws Exception {
        Viterbi tester = new Viterbi(featureGen, lambda, data);
        Scheduler sch = new Scheduler(tester, featureGen.params.numthreads, Scheduler.DYNAMIC_NEXT_AVAILABLE);
        sch.run();
    }
	
    /**
     * Write the high-order CRF to a file.
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
     * Read the high-order CRF from a file.
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
