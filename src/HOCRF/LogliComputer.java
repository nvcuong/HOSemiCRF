package HOCRF;

import java.util.*;
import Parallel.*;

/**
 * Algorithm for computing the partition functions and expected feature scores
 * @author Nguyen Viet Cuong
 */
public class LogliComputer implements Schedulable {

    int curID; // Current task ID (for parallelization)
    FeatureGenerator featureGen; // Feature generator
    ArrayList<DataSequence> trainData; // List of training sequences
    double[] lambda; // Lambda vector
    Loglikelihood logli; // Loglikelihood value and derivatives
    final int BASE = 1; // Base of the logAlpha array
    
    /**
     * Construct a loglikelihood computer.
     * @param lambdaValues Lambda vector
     * @param fgen Feature generator
     * @param td List of training sequences
     * @param loglh Initial loglikelihood and its derivatives (partially computed from class Function)
     */
    public LogliComputer(double[] lambdaValues, FeatureGenerator fgen, ArrayList<DataSequence> td, Loglikelihood loglh) {
        curID = -1;
        featureGen = fgen;
        trainData = td;
        lambda = lambdaValues;
        logli = loglh;
    }

    /**
     * Compute the partition function and expected feature score (in log scale) for a given sequence.
     * @param taskID Index of the training sequence
     * @return Partition function value and expected feature scores
     */
    public Object compute(int taskID) {
        Loglikelihood res = new Loglikelihood(lambda.length);
        DataSequence seq = trainData.get(taskID);
        
        addFeatureScores(seq, res);
        double[][] logAlpha = computeLogAlpha(seq);
        double logZx = computeLogZx(seq, logAlpha);
        double[][] logBeta = computeLogBeta(seq);
        double[][] marginal = computeMarginal(seq, logAlpha, logBeta, logZx);
        double[] expectation = computeExpectation(seq, marginal);

        for (int k = 0; k < lambda.length; k++) {
            res.derivatives[k] -= expectation[k];
        }
        res.logli -= logZx;

        return res;
    }
    
    /**
     * Return total number of tasks (for parallelization).
     * @return Training dataset size
     */
    public int getNumTasks() {
        return trainData.size();
    }

    /**
     * Return the next task ID (for parallelization).
     * @return The next sequence ID
     */
    public synchronized int fetchCurrTaskID() {
        if (curID < getNumTasks()) {
            curID++;
        }
        return curID;
    }

    /**
     * Add the partition function and expected feature scores into the final loglikelihood and its derivatives.
     * @param partialResult Partition function and expected feature scores
     */
    public synchronized void update(Object partialResult) {
        Loglikelihood res = (Loglikelihood) partialResult;
        logli.logli += res.logli;
        for (int i = 0; i < lambda.length; i++) {
            logli.derivatives[i] += res.derivatives[i];
        }
    }
    
    /**
     * Add the empirical feature scores and the sequence probability.
     * @param seq Training sequence
     * @param res Partial loglikelihood to be updated after this method call
     */
    public void addFeatureScores(DataSequence seq, Loglikelihood res) {
        for (int pos = 0; pos < seq.length(); pos++) {
            String labelPat = featureGen.generateLabelPattern(seq, pos);
            int sID = featureGen.getBackwardStateIndex(labelPat);
            for (int patID : featureGen.allSuffixes[sID]) {
                ArrayList<Integer> feats = featureGen.getFeatures(seq, pos, patID);
                for (int index : feats) {
                    Feature feat = featureGen.featureList.get(index);
                    res.derivatives[index] += feat.value;
                    res.logli += lambda[index] * feat.value;
                }
            }
        }
    }
	
    /**
     * Run the forward algorithm.
     * @param seq Training sequence
     * @return Logarithms of the alpha variables
     */
    public double[][] computeLogAlpha(DataSequence seq) {
        double[][] logAlpha = new double[seq.length() + 1][featureGen.forwardStateMap.size()];
        Arrays.fill(logAlpha[0], Double.NEGATIVE_INFINITY);
        logAlpha[0][0] = 0.0;
        for (int j = 0; j < seq.length(); j++) {
            Arrays.fill(logAlpha[j + BASE], Double.NEGATIVE_INFINITY);
            for (int i = 1; i < featureGen.forwardStateMap.size(); i++) {
                ArrayList<Integer> prevState1 = featureGen.forwardTransition1[i];
                ArrayList<Integer> prevState2 = featureGen.forwardTransition2[i];
                for (int k = 0; k < prevState1.size(); k++) {
                    int pkID = prevState1.get(k);
                    int pkyID = prevState2.get(k);
                    double featuresScore = 0.0;
                    for (Integer patID : featureGen.allSuffixes[pkyID]) {
                        ArrayList<Integer> feats = featureGen.getFeatures(seq, j, patID);
                        featuresScore += featureGen.computeFeatureScores(feats, lambda);
                    }
                    logAlpha[j + BASE][i] = Utility.logSumExp(logAlpha[j + BASE][i], logAlpha[j + BASE - 1][pkID] + featuresScore);
                }
            }
        }
        return logAlpha;
    }
	
    /**
     * Compute the logarithm of partition function from the alpha variables.
     * @param seq Training sequence
     * @param logAlpha Logarithms of the alpha variables
     * @return Logarithm of the partition function
     */
    public double computeLogZx(DataSequence seq, double[][] logAlpha) {
        double logZx = Double.NEGATIVE_INFINITY;
        int l = seq.length();
        for (int i = 0; i < featureGen.forwardStateMap.size(); i++) {
            logZx = Utility.logSumExp(logZx, logAlpha[l][i]);
        }
        return logZx;
    }
    
    /**
     * Run the backward algorithm.
     * @param seq Training sequence
     * @return Logarithms of the beta variables
     */
    public double[][] computeLogBeta(DataSequence seq) {
        double[][] logBeta = new double[seq.length() + 1][featureGen.backwardStateMap.size()];
        Arrays.fill(logBeta[seq.length()], 0.0);
        for (int j = seq.length() - 1; j > 0; j--) {
            Arrays.fill(logBeta[j], Double.NEGATIVE_INFINITY);
            for (int i = 0; i < featureGen.backwardStateMap.size(); i++) {
                for (int y = 0; y < featureGen.params.numLabels; y++) {
                    int skID = featureGen.backwardTransition[i][y];
                    if (skID != -1) {
                        double featuresScore = 0.0;
                        for (Integer patID : featureGen.allSuffixes[skID]) {
                            ArrayList<Integer> feats = featureGen.getFeatures(seq, j, patID);
                            featuresScore += featureGen.computeFeatureScores(feats, lambda);
                        }
                        logBeta[j][i] = Utility.logSumExp(logBeta[j][i], logBeta[j + 1][skID] + featuresScore);
                    }
                }
            }
        }
        return logBeta;
    }

    /**
     * Compute the marginals.
     * @param seq Training sequence
     * @param logAlpha Logarithms of the alpha variables
     * @param logBeta Logarithms of the beta variables
     * @param logZx Logarithm of the partition function
     * @return The array of marginals
     */
    public double[][] computeMarginal(DataSequence seq, double[][] logAlpha, double[][] logBeta, double logZx) {
        double[][] marginal = new double[featureGen.patternMap.size()][seq.length()];
        for (int zID = 0; zID < featureGen.patternMap.size(); zID++) {
            for (int pos = 0; pos < seq.length(); pos++) {
                marginal[zID][pos] = Double.NEGATIVE_INFINITY;
                    
                for (int i = 0; i < featureGen.patternTransition1[zID].size(); i++) {
                    int piID = featureGen.patternTransition1[zID].get(i);
                    int piyID = featureGen.patternTransition2[zID].get(i);
                        
                    double featuresScore = 0.0;
                    for (Integer patID : featureGen.allSuffixes[piyID]) {
                        ArrayList<Integer> feats = featureGen.getFeatures(seq, pos, patID);
                        featuresScore += featureGen.computeFeatureScores(feats, lambda);
                    }
                    marginal[zID][pos] = Utility.logSumExp(marginal[zID][pos], logAlpha[BASE + pos - 1][piID] + logBeta[pos + 1][piyID] + featuresScore);
                }
                    
                marginal[zID][pos] = Math.exp(marginal[zID][pos] - logZx);
            }
        }
        return marginal;
    }
	
    /**
     * Compute the feature expectations.
     * @param seq Training sequence
     * @param marginals The marginals
     * @return The feature expectations
     */
    public double[] computeExpectation(DataSequence seq, double[][] marginal) {
        double[] expectation = new double[lambda.length];
        Arrays.fill(expectation, 0.0);
        for (int zID = 0; zID < featureGen.patternMap.size(); zID++) {
            for (int pos = 0; pos  < seq.length(); pos ++) {
                ArrayList<Integer> feats = featureGen.getFeatures(seq, pos, zID);
                for (int index : feats) {
                    Feature feat = featureGen.featureList.get(index);
                    expectation[index] += feat.value * marginal[zID][pos];
                }
            }
        }
        return expectation;
    }
    
    /**
     * Print a 2D array to stdout for debugging.
     * @param arr The 2D array
     */
    public void printArray(double[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }
}
