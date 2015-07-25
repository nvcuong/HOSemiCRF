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

import Parallel.*;

/**
 * Feature generator class
 * @author Nguyen Viet Cuong
 * @author Sumit Bhagwani
 */
public class FeatureGenerator {

    ArrayList<FeatureType> featureTypes; // Feature types list
    int maxOrder; // Maximum order of the Semi-CRF
    Params params; // Parameters
    int[] maxMemory; // Maximum segment length for each label
    
    HashMap<String, Integer> obsMap; // Map from feature observation to its ID
    HashMap<String, Integer> patternMap; // Map from feature pattern to index
    HashMap<FeatureIndex, Integer> featureMap; // Map from FeatureIndex to its ID in lambda vector
    ArrayList<Feature> featureList; // Map from feature ID to features
    
    HashMap<String, Integer> forwardStateMap; // Map from forward state to index
    ArrayList<Integer>[] forwardTransition1; // Map from piID to list of pkID (see paper)
    ArrayList<Integer>[] forwardTransition2; // Map from piID to list of pkyID (see paper)
    int[] lastForwardStateLabel; // Map from piID to its last label
    
    HashMap<String, Integer> backwardStateMap; // Map from backward state to index
    int[][] backwardTransition; // Map from [siID,y] to skID (see paper)
    ArrayList<Integer>[] allSuffixes; // Map from sID to its suffixes patID
    ArrayList<String> backwardStateList;
	
    ArrayList<Integer>[] patternTransition1; // Map from z to piID (see paper)
    ArrayList<Integer>[] patternTransition2; // Map from z to piyID (see paper)
    int[] lastPatternLabel; // Map from pattern ID to its last label
    int[] patternBackwardID; // Map from pattern ID to its backward ID
	
    /**
     * Constructor a feature generator.
     * @param fts List of feature types
     * @param pr Parameters
     */
    public FeatureGenerator(ArrayList<FeatureType> fts, Params pr) {
        featureTypes = fts;
        maxOrder = getMaxOrder();
        params = pr;
    }
	
    /**
     * Initialize the feature generator with the training data.
     * This method needs to be called before the training process.
     * @param trainData List of training sequences
     */
    public void initialize(ArrayList<DataSequence> trainData) throws Exception {
        createMaxMemory(trainData);
        generateFeatureMap(trainData);
        generateForwardStatesMap();
        generateBackwardStatesMap();
        generateSentenceObs(trainData);
        buildForwardTransition();
        buildBackwardTransition();
        buildPatternTransition();
    }
    
    /**
     * Write the feature generator to a file.
     * @param filename Name of the output file
     */
    public void write(String filename) throws Exception {
        PrintWriter out = new PrintWriter(new FileOutputStream(filename));
        
        // Write observation map
        out.println(obsMap.size());
        Iterator<String> iter = obsMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            out.println(key + " " + obsMap.get(key));
        }
        
        // Write pattern map
        out.println(patternMap.size());
        iter = patternMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            out.println(key + " " + patternMap.get(key));
        }
        
        // Write feature map
        out.println(featureMap.size());
        Iterator<FeatureIndex> fIter = featureMap.keySet().iterator();
        while (fIter.hasNext()) {
            FeatureIndex fi = (FeatureIndex) fIter.next();
            int index = (Integer) featureMap.get(fi);
            Feature f = featureList.get(index);
            out.println(f.obs + " " + f.pat + " " + f.value + " " + index);
        }
        
        // Write forward state map
        out.println(forwardStateMap.size());
        iter = forwardStateMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (!key.equals("")) {
                out.println(key + " " + forwardStateMap.get(key));
            }
        }
        
        // Write backward state map
        out.println(backwardStateMap.size());
        iter = backwardStateMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            out.println(key + " " + backwardStateMap.get(key));
        }
	
	// Write max memory
        for (int i = 0; i < maxMemory.length; i++) {
            out.println(maxMemory[i]);
        }

        out.close();
    }
	
    /**
     * Load the feature generator from a file.
     * @param filename Name of the file that contains the feature generator information
     */
    public void read(String filename) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        
        // Read observation map
        int mapSize = Integer.parseInt(in.readLine());
        obsMap = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            String line = in.readLine();
            StringTokenizer toks = new StringTokenizer(line);
            String key = toks.nextToken();
            int index = Integer.parseInt(toks.nextToken());
            obsMap.put(key, index);
        }
        
        // Read pattern map
        mapSize = Integer.parseInt(in.readLine());
        patternMap = new HashMap<>();
        for (int i = 0; i < mapSize; i++) {
            String line = in.readLine();
            StringTokenizer toks = new StringTokenizer(line);
            String key = toks.nextToken();
            int index = Integer.parseInt(toks.nextToken());
            patternMap.put(key, index);
        }
        
        // Read feature map
        mapSize = Integer.parseInt(in.readLine());
        featureMap = new HashMap<>();
        featureList = new ArrayList<Feature>(mapSize);
        for (int i = 0; i < mapSize; i++) featureList.add(null);
        for (int i = 0; i < mapSize; i++) {
            String line = in.readLine();
            StringTokenizer toks = new StringTokenizer(line);
            String obs = toks.nextToken();
            String pat = toks.nextToken();
            double value = Double.parseDouble(toks.nextToken());
            int index = Integer.parseInt(toks.nextToken());
            Feature f = new Feature(obs, pat, value);
            featureMap.put(getFeatureIndex(f), index);
            featureList.set(index, f);
        }
        
        // Read forward state map
        mapSize = Integer.parseInt(in.readLine());
        forwardStateMap = new HashMap<>();
        forwardStateMap.put("", new Integer(0));
        for (int i = 0; i < mapSize-1; i++) {
            String line = in.readLine();
            StringTokenizer toks = new StringTokenizer(line);
            String key = toks.nextToken();
            int index = Integer.parseInt(toks.nextToken());
            forwardStateMap.put(key, index);
        }
        
        // Read backward state map
        mapSize = Integer.parseInt(in.readLine());
        backwardStateMap = new HashMap<>();
        backwardStateList = new ArrayList<String>(mapSize);
        for (int i = 0; i < mapSize; i++) backwardStateList.add(null);
        for (int i = 0; i < mapSize; i++) {
            String line = in.readLine();
            StringTokenizer toks = new StringTokenizer(line);
            String key = toks.nextToken();
            int index = Integer.parseInt(toks.nextToken());
            backwardStateMap.put(key, index);
            backwardStateList.set(index, key);
        }
        
        // Read max memory
        maxMemory = new int[params.numLabels];
        for (int i = 0; i < params.numLabels; i++) {
            maxMemory[i] = Integer.parseInt(in.readLine());
        }

        buildForwardTransition();
        buildBackwardTransition();
        buildPatternTransition();

        in.close();
    }
	
    /**
     * Get the index of a feature.
     * @param f Feature
     * @return The feature index
     */
    public FeatureIndex getFeatureIndex(Feature f) {
        Integer obs = (Integer) getObsIndex(f.obs);
        Integer pat = (Integer) getPatternIndex(f.pat);
        if (obs == null || pat == null) {
            return null;
        } else {
            return new FeatureIndex(getObsIndex(f.obs), getPatternIndex(f.pat));
        }
    }
    
    /**
     * Get the index of an observation string.
     * @param obs Observation string
     * @return Observation index
     */
    public Integer getObsIndex(String obs) {
        return (Integer) obsMap.get(obs);
    }

    /**
     * Get the index of a pattern string.
     * @param p Pattern string
     * @return Pattern index
     */
    public Integer getPatternIndex(String p) {
        return (Integer) patternMap.get(p);
    }
    
    /**
     * Get the index of a forward state.
     * @param p Forward state
     * @return Index of the forward state
     */
    public Integer getForwardStateIndex(String p) {
        return (Integer) forwardStateMap.get(p);
    }

    /**
     * Get the index of a backward state.
     * @param p Backward state
     * @return Index of the backward state
     */
    public Integer getBackwardStateIndex(String p) {
        return (Integer) backwardStateMap.get(p);
    }

    /**
     * Get the maximum order of the semi-CRF.
     * @return Maximum order of the semi-CRF
     */
    public int getMaxOrder() {
        int res = -1;
        for (int i = 0; i < featureTypes.size(); i++) {
            if (res < featureTypes.get(i).order()) {
                res = featureTypes.get(i).order();
            }
        }
        return res;
    }
    
    /**
     * Generate the observations for each training sequence.
     * @param trainData List of training sequences
     */
    public void generateSentenceObs(ArrayList<DataSequence> trainData) throws Exception {
        SentenceObsGenerator gen = new SentenceObsGenerator(trainData, this);
        Scheduler sch = new Scheduler(gen, params.numthreads, Scheduler.DYNAMIC_NEXT_AVAILABLE);
        sch.run();
    }
    
    /**
     * Initialize the maximum segment length for each label.
     * Reset the overall maximum segment length.
     * Reset the segment information for each training sequence.
     * @param trainData List of training sequences
     */
    public void createMaxMemory(ArrayList<DataSequence> trainData) throws Exception {
        maxMemory = new int[params.numLabels];
        Arrays.fill(maxMemory, -1);
        
        if (params.maxSegment == -1) {
            for (int t = 0; t < trainData.size(); t++) {
                DataSequence seq = (DataSequence) trainData.get(t);
                for (int segStart = 0; segStart < seq.length(); segStart = seq.getSegmentEnd(segStart) + 1) {
                    int segEnd = seq.getSegmentEnd(segStart);
                    int l = seq.y(segEnd);
                    if (maxMemory[l] < segEnd - segStart + 1) {
                        maxMemory[l] = segEnd - segStart + 1;
                        params.maxSegment = Math.max(params.maxSegment, maxMemory[l]);
                    }
                }
            }
        } else if (params.maxSegment == 1) {
            for (int t = 0; t < trainData.size(); t++) {
                DataSequence seq = (DataSequence) trainData.get(t);
                for (int i = 0; i < seq.length(); i++) {
                    seq.startPos[i] = i;
                    seq.endPos[i] = i;
                    int l = seq.y(i);
                    maxMemory[l] = 1;
                }
            }
        } else {
            throw new Exception("Set maxSegment = -1 for semi-CRF and maxSegment = 1 for CRF.");
        }
    }
    
    /**
     * Generate the observation map, pattern map, feature map, and feature list from training data.
     * @param trainData List of training sequences
     */
    public void generateFeatureMap(ArrayList<DataSequence> trainData) {
        obsMap = new HashMap<>();
        patternMap = new HashMap<>();
        featureMap = new HashMap<>();
        featureList = new ArrayList<Feature>();
        for (int t = 0; t < trainData.size(); t++) {
            DataSequence seq = (DataSequence) trainData.get(t);
            int segStart, segEnd;
            for (segStart = 0; segStart < seq.length(); segStart = segEnd + 1) {
                segEnd = seq.getSegmentEnd(segStart);

                String labelPat = generateLabelPattern(seq, segStart, segEnd);
                ArrayList<Feature> features = generateFeatures(seq, segStart, segEnd, labelPat);
                
                for (Feature f : features) {
                    Integer obs_index = getObsIndex(f.obs);
                    if (obs_index == null) {
                        obsMap.put(f.obs, obsMap.size());
                    }
                    
                    Integer pat_index = getPatternIndex(f.pat);
                    if (pat_index == null) {
                        patternMap.put(f.pat, patternMap.size());
                    }
					
                    FeatureIndex index = getFeatureIndex(f);
                    if (!featureMap.containsKey(index)) {
                        featureMap.put(index, featureMap.size());
                        featureList.add(f);
                    }
                }
            }
        }
    }
    
    /**
     * Generate the forward state map.
     */
    public void generateForwardStatesMap() {
        forwardStateMap = new HashMap<>();
        forwardStateMap.put("", new Integer(0));
        for (int i = 0; i < params.numLabels; i++) {
            forwardStateMap.put("" + i, new Integer(forwardStateMap.size()));
        }
        Iterator<String> iter = patternMap.keySet().iterator();
        while (iter.hasNext()) {
            String labelPat = (String) iter.next();
            ArrayList<String> pats = Utility.generateProperPrefixes(labelPat);
            for (String pat : pats) {
                if (getForwardStateIndex(pat) == null) {
                    forwardStateMap.put(pat, forwardStateMap.size());
                }
            }
        }
    }

    /**
     * Generate the backward state map and the backward state list.
     */
    public void generateBackwardStatesMap() {
        backwardStateMap = new HashMap<>();
        backwardStateList = new ArrayList<String>();
        Iterator<String> iter = forwardStateMap.keySet().iterator();
        while (iter.hasNext()) {
            String p = (String) iter.next();
            int lastLabel = p.equals("") ? -1 : Integer.parseInt(Utility.getLastLabel(p));
            for (int y = 0; y < params.numLabels; y++) {
                if (y != lastLabel || params.maxSegment == 1) {
                    String py = p.equals("") ? y + "" : y + "|" + p;
                    if (getBackwardStateIndex(py) == null) {
                        backwardStateMap.put(py, backwardStateMap.size());
                        backwardStateList.add(py);
                    }
                }
            }
        }
    }

    /**
     * Generate the maximum posible pattern for a segment.
     * Note that patterns are in reversed order: y(t)|y(t-1)|y(t-2)|...
     * @param seq Data sequence
     * @param segStart Start position of the segment
     * @param segEnd End position of the segment
     * @return Pattern string
     */
    public String generateLabelPattern(DataSequence seq, int segStart, int segEnd) {
        String labelPat = "";
        int pos = segStart;
        for (int i = 0; i <= maxOrder; i++) {
            labelPat = labelPat + "|" + seq.y(pos);
            if (pos == 0) {
                break;
            } else {
                pos = seq.getSegmentStart(pos - 1);
            }
        }
        labelPat = labelPat.substring(1);
        return labelPat;
    } 

    /**
     * Generate all features activated at a segment with a given label pattern.
     * @param seq Data sequence
     * @param segStart Start position of the segment
     * @param segEnd End position of the segment
     * @param labelPat Label pattern
     * @return List of activated features
     */
    public ArrayList<Feature> generateFeatures(DataSequence seq, int segStart, int segEnd, String labelPat) {
        ArrayList<Feature> features = new ArrayList<Feature>();
        ArrayList<String> suffixes = Utility.generateSuffixes(labelPat);
        for (String s : suffixes) {
            ArrayList<Feature> fi = generateFeaturesWithExactPattern(seq, segStart, segEnd, s);
            features.addAll(fi);
        }
        return features;
    }
    
    /**
     * Generate all features activated at a segment with an exact label pattern.
     * @param seq Data sequence
     * @param segStart Start position of the segment
     * @param segEnd End position of the segment
     * @param labelPat Exact label pattern of the activated features
     * @return List of activated features
     */
    public ArrayList<Feature> generateFeaturesWithExactPattern(DataSequence seq, int segStart, int segEnd, String labelPat) {
        ArrayList<Feature> features = new ArrayList<Feature>();
        for (FeatureType ft : featureTypes) {
            ArrayList<Feature> fi = ft.generateFeaturesAt(seq, segStart, segEnd, labelPat);
            features.addAll(fi);
        }
        return features;
    }
    
    /**
     * Generate all observations at a segment.
     * @param seq Data sequence
     * @param segStart Start position of the segment
     * @param segEnd End position of the segment
     * @return List of observations
     */
    public ArrayList<String> generateObs(DataSequence seq, int segStart, int segEnd) {
        ArrayList<String> obs = new ArrayList<String>();
        for (FeatureType ft : featureTypes) {
            obs.addAll(ft.generateObsAt(seq, segStart, segEnd));
        }
        return obs;
    }
    
    /**
     * Return the index of the longest suffix of a string.
     * @param p The input string
     * @param map Map from strings to indices
     * @return Index of the longest suffix of the input string from the input map.
     */
    public Integer getLongestSuffixID(String p, HashMap<String, Integer> map) {
        ArrayList<String> suffixes = Utility.generateSuffixes(p);
        for (int i = 0; i < suffixes.size(); i++) {
            Integer index = (Integer) map.get(suffixes.get(i));
            if (index != null) {
                return index;
            }
        }
        throw new UnsupportedOperationException("No longest suffix index!\n");
    }

    /**
     * Return the longest suffix of a string.
     * @param p The input string
     * @param map Map from strings to indices
     * @return The longest suffix of the input string from the input map.
     */
    public String getLongestSuffix(String p, HashMap<String, Integer> map) {
        ArrayList<String> suffixes = Utility.generateSuffixes(p);
        for (int i = 0; i < suffixes.size(); i++) {
            Integer index = (Integer) map.get(suffixes.get(i));
            if (index != null) {
                return suffixes.get(i);
            }
        }
        throw new UnsupportedOperationException("No longest suffix!\n");
    }	

    /**
     * Build the information for the forward algorithm.
     */
    @SuppressWarnings("unchecked")
	public void buildForwardTransition() {
        forwardTransition1 = new ArrayList[forwardStateMap.size()];
        forwardTransition2 = new ArrayList[forwardStateMap.size()];
        lastForwardStateLabel = new int[forwardStateMap.size()];
        
        Iterator<String> iter = forwardStateMap.keySet().iterator();
        while (iter.hasNext()) {
            String pk = iter.next();
            int pkID = getForwardStateIndex(pk);
            lastForwardStateLabel[pkID] = pk.equals("") ? -1 : Integer.parseInt(Utility.getLastLabel(pk));
            
            for (int y = 0; y < params.numLabels; y++) {
                if (y != lastForwardStateLabel[pkID] || params.maxSegment == 1) {
                    String pky = pk.equals("") ? y + "" : y + "|" + pk;
                    Integer index = getLongestSuffixID(pky, forwardStateMap);
                    if (forwardTransition1[index] == null) {
                        forwardTransition1[index] = new ArrayList<Integer>();
                        forwardTransition2[index] = new ArrayList<Integer>();
                    }
                    forwardTransition1[index].add(pkID);
                    forwardTransition2[index].add(getBackwardStateIndex(pky));
                }
            }
        }
    }

    /**
     * Build the information for the backward algorithm.
     */
    @SuppressWarnings("unchecked")
	public void buildBackwardTransition() {
        backwardTransition = new int[backwardStateMap.size()][params.numLabels];
        allSuffixes = new ArrayList[backwardStateMap.size()];
		
        Iterator<String> iter = backwardStateMap.keySet().iterator();
        while (iter.hasNext()) {
            String si = iter.next();
            int siID = getBackwardStateIndex(si);
            int lastLabel = si.equals("") ? -1 : Integer.parseInt(Utility.getLastLabel(si));
            for (int y = 0; y < params.numLabels; y++) {
                if (y != lastLabel || params.maxSegment == 1) {
                    String siy = y + "|" + si;
                    String sk = getLongestSuffix(siy, backwardStateMap);
                    backwardTransition[siID][y] = getBackwardStateIndex(sk);
                } else {
                    backwardTransition[siID][y] = -1;
                }
            }
            
            allSuffixes[siID] = new ArrayList<Integer>();
            ArrayList<String> suffixes = Utility.generateSuffixes(si);
            for (String suffix : suffixes) {
                Integer patID = getPatternIndex(suffix);
                if (patID != null) {
                    allSuffixes[siID].add(patID);
                }
            }
        }
    }

    /**
     * Build the information to compute the marginals and expected feature scores.
     */
    @SuppressWarnings("unchecked")
	public void buildPatternTransition() {
        patternTransition1 = new ArrayList[patternMap.size()];
        patternTransition2 = new ArrayList[patternMap.size()];
        lastPatternLabel = new int[patternMap.size()];
        patternBackwardID = new int[patternMap.size()];
        
        Iterator<String> iter = patternMap.keySet().iterator();
        while (iter.hasNext()) {
            String p = iter.next();
            int pID = getPatternIndex(p).intValue();
            patternBackwardID[pID] = getBackwardStateIndex(p);
            String lastY = Utility.getLastLabel(p);
            if (lastY.equals("")) {
                lastPatternLabel[pID] = -1;
            } else {
                lastPatternLabel[pID] = Integer.parseInt(lastY);
            }
        }
		
        Iterator<String> forwardIter = forwardStateMap.keySet().iterator();
        while (forwardIter.hasNext()) {
            String pi = forwardIter.next();
            int lastLabel = pi.equals("") ? -1 : Integer.parseInt(Utility.getLastLabel(pi));
            int piID = getForwardStateIndex(pi);
            for (int y = 0; y < params.numLabels; y++) {
                if (y != lastLabel || params.maxSegment == 1) {
                    String piy = pi.equals("") ? y + "" : y + "|" + pi;
                    Integer piyID = getBackwardStateIndex(piy);
                    ArrayList<String> suffixes = Utility.generateSuffixes(piy);
                    for (String zi : suffixes) {
                        Integer ziIndex = getPatternIndex(zi);
                        if (ziIndex != null) {
                            if (patternTransition1[ziIndex] == null) {
                                patternTransition1[ziIndex] = new ArrayList<Integer>();
                                patternTransition2[ziIndex] = new ArrayList<Integer>();
                            }
                            patternTransition1[ziIndex].add(piID);
                            patternTransition2[ziIndex].add(piyID);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Get the IDs of the features activated at a segment for a given pattern.
     * @param seq Data sequence
     * @param segStart Start position of the segment
     * @param segEnd End position of the segment
     * @param patID Pattern ID
     * @return List of feature IDs
     */
    public ArrayList<Integer> getFeatures(DataSequence seq, int segStart, int segEnd, int patID) {
        ArrayList<Integer> feats = new ArrayList<Integer>();
        int[] obsList = seq.getObservation(segStart, segEnd);
        for (int obsID : obsList) {
            Integer feat = (Integer) featureMap.get(new FeatureIndex(obsID, patID));
            if (feat != null) {
                feats.add(feat);
            }
        }
        return feats;
    }
    
    /**
     * Get the IDs of a list of features.
     * @param fs List of features
     * @return List of feature IDs
     */
    public ArrayList<Integer> getFeatureID(ArrayList<Feature> fs) {
        ArrayList<Integer> feats = new ArrayList<Integer>();
        for (Feature f : fs) {
            Integer feat = (Integer) featureMap.get(getFeatureIndex(f));
            if (feat != null) {
                feats.add(feat);
            }
        }
        return feats;
    }
    
    /**
     * Compute the feature scores of a list of features and a weight vector.
     * @param feats List of feature IDs
     * @param lambda Weights of all the features
     * @return The total feature score
     */
    public double computeFeatureScores(ArrayList<Integer> feats, double[] lambda) {
        double featuresScore = 0.0;
        for (int index : feats) {
            Feature feat = featureList.get(index);
            featuresScore += lambda[index] * feat.value;
        }
        return featuresScore;
    }
    
    /**
     * Print all statistics for testing.
     */
    public void printStatesStatistics() {
        System.out.println("Forward Transition:");
        for (int piID = 0; piID < forwardStateMap.size(); piID++) {
            System.out.println(piID + " --> " + lastForwardStateLabel[piID]);
            if (forwardTransition1[piID] != null) {
                for (int i = 0; i < forwardTransition1[piID].size(); i++) {
                    System.out.println(forwardTransition1[piID].get(i) + " " + forwardTransition2[piID].get(i));
                }
            }
        }
        
        System.out.println("Backward Transition:");
        for (int sID = 0; sID < backwardStateMap.size(); sID++) {
            for (int y = 0; y < params.numLabels; y++) {
                System.out.println(sID + " " + y + " --> " + backwardTransition[sID][y]);
            }
        }

        System.out.println("Pattern Transition:");
        for (int pID = 0; pID < patternMap.size(); pID++) {
            System.out.println(pID + " --> " + lastPatternLabel[pID] + " " + patternBackwardID[pID]);
            if (patternTransition1[pID] != null) {
                for (int i = 0; i < patternTransition1[pID].size(); i++) {
                    System.out.println(patternTransition1[pID].get(i) + " " + patternTransition2[pID].get(i));
                }
            }
        }
    }

    /**
     * 
     * @return object which contains all value of parameters.
     */
	public Params getParams() {
		return params;
	}
}
