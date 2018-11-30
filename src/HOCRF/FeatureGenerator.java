package HOCRF;

import java.io.*;
import java.util.*;
import Parallel.*;

/**
 * Feature generator class
 * @author Nguyen Viet Cuong
 */
public class FeatureGenerator {

    ArrayList<FeatureType> featureTypes; // Feature types list
    int maxOrder; // Maximum order of the CRF
    Params params; // Parameters
    
    HashMap obsMap; // Map from feature observation to its ID
    HashMap patternMap; // Map from feature pattern to index
    HashMap featureMap; // Map from FeatureIndex to its ID in lambda vector
    ArrayList<Feature> featureList; // Map from feature ID to features
    
    HashMap forwardStateMap; // Map from forward state to index
    ArrayList<Integer>[] forwardTransition1; // Map from piID to list of pkID (see paper)
    ArrayList<Integer>[] forwardTransition2; // Map from piID to list of pkyID (see paper)
    
    HashMap backwardStateMap; // Map from backward state to index
    int[][] backwardTransition; // Map from [siID,y] to skID (see paper)
    ArrayList<Integer>[] allSuffixes; // Map from sID to its suffixes patID
    ArrayList<String> backwardStateList; // List of backward states
	
    ArrayList<Integer>[] patternTransition1; // Map from z to piID (see paper)
    ArrayList<Integer>[] patternTransition2; // Map from z to piyID (see paper)
	
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
    public void initialize(ArrayList trainData) throws Exception {
        generateFeatureMap(trainData);
        generateForwardStatesMap();
        generateBackwardStatesMap();
        generateSentenceFeat(trainData);
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
        Iterator iter = obsMap.keySet().iterator();
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
        iter = featureMap.keySet().iterator();
        while (iter.hasNext()) {
            FeatureIndex fi = (FeatureIndex) iter.next();
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
        obsMap = new HashMap();
        for (int i = 0; i < mapSize; i++) {
            String line = in.readLine();
            StringTokenizer toks = new StringTokenizer(line);
            String key = toks.nextToken();
            int index = Integer.parseInt(toks.nextToken());
            obsMap.put(key, index);
        }
        
        // Read pattern map
        mapSize = Integer.parseInt(in.readLine());
        patternMap = new HashMap();
        for (int i = 0; i < mapSize; i++) {
            String line = in.readLine();
            StringTokenizer toks = new StringTokenizer(line);
            String key = toks.nextToken();
            int index = Integer.parseInt(toks.nextToken());
            patternMap.put(key, index);
        }
        
        // Read feature map
        mapSize = Integer.parseInt(in.readLine());
        featureMap = new HashMap();
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
        forwardStateMap = new HashMap();
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
        backwardStateMap = new HashMap();
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
     * Get the maximum order of the CRF.
     * @return Maximum order of the CRF
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
     * Generate the features for each training sequence.
     * @param trainData List of training sequences
     */
    public void generateSentenceFeat(ArrayList trainData) throws Exception {
        SentenceFeatGenerator gen = new SentenceFeatGenerator(trainData, this);
        Scheduler sch = new Scheduler(gen, params.numthreads, Scheduler.DYNAMIC_NEXT_AVAILABLE);
        sch.run();
    }
    
    /**
     * Generate the observation map, pattern map, feature map, and feature list from training data.
     * @param trainData List of training sequences
     */
    public void generateFeatureMap(ArrayList trainData) {
        obsMap = new HashMap();
        patternMap = new HashMap();
        featureMap = new HashMap();
        featureList = new ArrayList<Feature>();
        for (int t = 0; t < trainData.size(); t++) {
            DataSequence seq = (DataSequence) trainData.get(t);
            for (int pos = 0; pos < seq.length(); pos++) {
                String labelPat = generateLabelPattern(seq, pos);
                ArrayList<Feature> features = generateFeatures(seq, pos, labelPat);
                
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
        
        //System.out.println("Num pattern = " + patternMap.size());
    }
    
    /**
     * Generate the forward state map.
     */
    public void generateForwardStatesMap() {
        forwardStateMap = new HashMap();
        forwardStateMap.put("", new Integer(0));
        for (int i = 0; i < params.numLabels; i++) {
            forwardStateMap.put("" + i, new Integer(forwardStateMap.size()));
        }
        Iterator iter = patternMap.keySet().iterator();
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
        backwardStateMap = new HashMap();
        backwardStateList = new ArrayList<String>();
        Iterator iter = forwardStateMap.keySet().iterator();
        while (iter.hasNext()) {
            String p = (String) iter.next();
            int lastLabel = p.equals("") ? -1 : Integer.parseInt(Utility.getLastLabel(p));
            for (int y = 0; y < params.numLabels; y++) {
                String py = p.equals("") ? y + "" : y + "|" + p;
                if (getBackwardStateIndex(py) == null) {
                    backwardStateMap.put(py, backwardStateMap.size());
                    backwardStateList.add(py);
                }
            }
        }
    }

    /**
     * Generate the maximum posible pattern for a position.
     * Note that patterns are in reversed order: y(t)|y(t-1)|y(t-2)|...
     * @param seq Data sequence
     * @param pos Input position
     * @return Pattern string
     */
    public String generateLabelPattern(DataSequence seq, int pos) {
        String labelPat = "";
        for (int i = 0; i <= maxOrder && pos-i >= 0; i++) {
            labelPat = labelPat + "|" + seq.y(pos-i);
        }
        labelPat = labelPat.substring(1);
        return labelPat;
    }

    /**
     * Generate all features activated at a position with a given label pattern.
     * @param seq Data sequence
     * @param pos Input position
     * @param labelPat Label pattern
     * @return List of activated features
     */
    public ArrayList<Feature> generateFeatures(DataSequence seq, int pos, String labelPat) {
        ArrayList<Feature> features = new ArrayList<Feature>();
        ArrayList<String> suffixes = Utility.generateSuffixes(labelPat);
        for (String s : suffixes) {
            ArrayList<Feature> fi = generateFeaturesWithExactPattern(seq, pos, s);
            features.addAll(fi);
        }
        return features;
    }
    
    /**
     * Generate all features activated at a position with an exact label pattern.
     * @param seq Data sequence
     * @param pos Input position
     * @param labelPat Exact label pattern of the activated features
     * @return List of activated features
     */
    public ArrayList<Feature> generateFeaturesWithExactPattern(DataSequence seq, int pos, String labelPat) {
        ArrayList<Feature> features = new ArrayList<Feature>();
        for (FeatureType ft : featureTypes) {
            ArrayList<Feature> fi = ft.generateFeaturesAt(seq, pos, labelPat);
            features.addAll(fi);
        }
        return features;
    }
    
    /**
     * Generate all observations at a position.
     * @param seq Data sequence
     * @param pos Input position
     * @return List of observations
     */
    public ArrayList<String> generateObs(DataSequence seq, int pos) {
        ArrayList<String> obs = new ArrayList<String>();
        for (FeatureType ft : featureTypes) {
            obs.addAll(ft.generateObsAt(seq, pos));
        }
        return obs;
    }
    
    /**
     * Return the index of the longest suffix of a string.
     * @param p The input string
     * @param map Map from strings to indices
     * @return Index of the longest suffix of the input string from the input map.
     */
    public Integer getLongestSuffixID(String p, HashMap map) {
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
    public String getLongestSuffix(String p, HashMap map) {
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
    public void buildForwardTransition() {
        forwardTransition1 = new ArrayList[forwardStateMap.size()];
        forwardTransition2 = new ArrayList[forwardStateMap.size()];
        
        Iterator iter = forwardStateMap.keySet().iterator();
        while (iter.hasNext()) {
            String pk = (String) iter.next();
            int pkID = getForwardStateIndex(pk);
            
            for (int y = 0; y < params.numLabels; y++) {
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

    /**
     * Build the information for the backward algorithm.
     */
    public void buildBackwardTransition() {
        backwardTransition = new int[backwardStateMap.size()][params.numLabels];
        allSuffixes = new ArrayList[backwardStateMap.size()];
		
        Iterator iter = backwardStateMap.keySet().iterator();
        while (iter.hasNext()) {
            String si = (String) iter.next();
            int siID = getBackwardStateIndex(si);
            int lastLabel = si.equals("") ? -1 : Integer.parseInt(Utility.getLastLabel(si));
            for (int y = 0; y < params.numLabels; y++) {
                String siy = y + "|" + si;
                String sk = getLongestSuffix(siy, backwardStateMap);
                backwardTransition[siID][y] = getBackwardStateIndex(sk);
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
    public void buildPatternTransition() {
        patternTransition1 = new ArrayList[patternMap.size()];
        patternTransition2 = new ArrayList[patternMap.size()];
		
        Iterator forwardIter = forwardStateMap.keySet().iterator();
        while (forwardIter.hasNext()) {
            String pi = (String) forwardIter.next();
            int lastLabel = pi.equals("") ? -1 : Integer.parseInt(Utility.getLastLabel(pi));
            int piID = getForwardStateIndex(pi);
            for (int y = 0; y < params.numLabels; y++) {
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
    
    /**
     * Get the IDs of the features activated at a position.
     * @param seq Data sequence
     * @param pos Input position
     * @param patID Pattern ID
     * @return List of feature IDs
     */
    public ArrayList<Integer> getFeatures(DataSequence seq, int pos, int patID) {
        return seq.getFeatures(pos, patID);
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
            System.out.println(piID);
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
            System.out.println(pID);
            if (patternTransition1[pID] != null) {
                for (int i = 0; i < patternTransition1[pID].size(); i++) {
                    System.out.println(patternTransition1[pID].get(i) + " " + patternTransition2[pID].get(i));
                }
            }
        }
    }
}
