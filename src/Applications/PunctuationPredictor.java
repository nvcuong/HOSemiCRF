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

package Applications;

import java.io.*;
import java.util.*;
import HOSemiCRF.*;
import Applications.PuncFeatures.*;

/**
 * Main class for the Punctuation Prediction task
 * @author Nguyen Viet Cuong
 */
public class PunctuationPredictor {
	
    HighOrderSemiCRF highOrderSemiCrfModel; // High-order semi-CRF model
    FeatureGenerator featureGen; // Feature generator
    LabelMap labelmap = new LabelMap(); // Label map
    String configFile; // Configuration filename

    /**
     * Construct a punctuation tagger from a configuration file.
     * @param filename Name of configuration file
     */
    public PunctuationPredictor(String filename) {
        configFile = filename;
    }
    
    /**
     * Read the training file.
     * @param filename Name of the training file
     * @return The training data
     */
    public DataSet readTagged(String filename) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));

        ArrayList<DataSequence> td = new ArrayList<>();
        ArrayList<String> inps = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();
        String line;

        while ((line = in.readLine()) != null) {
            if (line.length() > 0) {
                StringTokenizer toks = new StringTokenizer(line);
                String word = toks.nextToken();
                String tagRel = toks.nextToken();
                inps.add(word);
                labels.add(tagRel);
            } else if (labels.size() > 0) {
                td.add(new DataSequence(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
                inps = new ArrayList<String>();
                labels = new ArrayList<String>();
            }
        }
        if (labels.size() > 0) {
            td.add(new DataSequence(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
        }

        in.close();
        return new DataSet(td);
    }

    /**
     * Add the feature types, process the parameters, and initialize the feature generator.
     */
    public void createFeatureGenerator() throws Exception {
    	// Add feature types
        ArrayList<FeatureType> fts = new ArrayList<FeatureType>();
        
        // 1st-order CRF featutres
        fts.add(new WordPositionBag());
        fts.add(new TwoWordPositionBag());
		
        fts.add(new EdgeBag());
        fts.add(new Edge());
        fts.add(new EdgeWordBag());
        fts.add(new EdgeWord());
        fts.add(new EdgePreviousWordBag());
        fts.add(new EdgePreviousWord());
        fts.add(new EdgeTwoWordBag());
        fts.add(new EdgeTwoWord());
        
        // Add these for 1st-order Semi-CRF
        // fts.add(new FirstOrderTransition());
        // fts.add(new FirstOrderTransitionWord());
        
        // Add these for 2nd-order CRF and Semi-CRF
        // fts.add(new SecondOrderTransition());
        // fts.add(new SecondOrderTransitionWord());
        
        // Add these for 3rd-order CRF and Semi-CRF
        // fts.add(new ThirdOrderTransition());
        // fts.add(new ThirdOrderTransitionWord());
        
        // Process parameters
        Params params = new Params(configFile, labelmap.size());
        
        // Initialize feature generator
        featureGen = new FeatureGenerator(fts, params);
    }

    /**
     * Train the high-order semi-CRF.
     */
    public void train(String puncFilename) throws Exception {
    	// Set training file name and create output directory
        String trainFilename = "punc.train";
        File dir = new File("learntModels/");
        dir.mkdirs();
        
        // Read training data and save the label map
        PuncConverter.convert(puncFilename, trainFilename);
        DataSet trainData = readTagged(trainFilename);
        labelmap.write("learntModels/labelmap");
        
        // Create and save feature generator
        createFeatureGenerator();
        featureGen.initialize(trainData.getSeqList());
        featureGen.write("learntModels/features");

        // Train and save model
        highOrderSemiCrfModel = new HighOrderSemiCRF(featureGen);
        highOrderSemiCrfModel.train(trainData.getSeqList());
        highOrderSemiCrfModel.write("learntModels/crf");
    }

    /**
     * Test the high-order semi-CRF.
     */
    public void test(String tsFilename) throws Exception {
    	// Read label map, features, and CRF model
        labelmap.read("learntModels/labelmap");
        createFeatureGenerator();
        featureGen.read("learntModels/features");
        highOrderSemiCrfModel = new HighOrderSemiCRF(featureGen);
        highOrderSemiCrfModel.read("learntModels/crf");
        
        // Run Viterbi algorithm
        System.out.print("Running Viterbi...");
        String testFilename = "punc.test";
        PuncConverter.convert(tsFilename, testFilename);
        DataSet testData = readTagged(testFilename);
        long startTime = System.currentTimeMillis();
        highOrderSemiCrfModel.runViterbi(testData.getSeqList());
        System.out.println("done in " + (System.currentTimeMillis() - startTime) + " ms");
        
        // Print out the predicted data
        File dir = new File("out/");
        dir.mkdirs();
        testData.writeToFile("out/punc.test");
        
        // Score the results
        System.out.println("Scoring results...");
        startTime = System.currentTimeMillis();
        DataSet trueTestData = readTagged(testFilename);
        Scorer scr = new Scorer(trueTestData.getSeqList(), testData.getSeqList(), labelmap, true);
        scr.tokenScore();
        System.out.println("done in " + (System.currentTimeMillis() - startTime) + " ms");
    }

    /**
     * Main class
     */
    public static void main(String argv[]) throws Exception {
        PunctuationPredictor puncPredictor = new PunctuationPredictor(argv[1]);
        if (argv[0].toLowerCase().equals("all")) {
            puncPredictor.train(argv[2]);
            puncPredictor.test(argv[3]);
        } else if (argv[0].toLowerCase().equals("train")) {
            puncPredictor.train(argv[2]);
        } else if (argv[0].toLowerCase().equals("test")) {
            puncPredictor.test(argv[3]);
        }
    }
}
