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
import Applications.RefFeatures.*;

/**
 * Reference Tagger class
 * @author Nguyen Viet Cuong
 */
public class ReferenceTagger {

    HighOrderSemiCRF highOrderSemiCrfModel; // High-order semi-CRF model
    FeatureGenerator featureGen; // Feature generator
    LabelMap labelmap = new LabelMap(); // Label map
    String configFile; // Configuration filename

    /**
     * Construct a tagger from a configuration file.
     * @param filename Name of configuration file
     */
    public ReferenceTagger(String filename) {
        configFile = filename;
    }
    
    /**
     * Read the training file.
     * @param filename Name of the training file
     * @return The training data
     */
    public DataSet readTagged(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));

        ArrayList td = new ArrayList();
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
        
        // 1st-order CRF features
        fts.add(new WordBag());
        fts.add(new PreviousWordBag());
        fts.add(new NextWordBag());
        fts.add(new WordKPositionBeforeBag());
        fts.add(new WordKPositionAfterBag());
        fts.add(new LetterNGramsBag());

        fts.add(new EdgeBag());
        fts.add(new Edge());
        fts.add(new EdgeWordBag());
        fts.add(new EdgeWord());
        fts.add(new EdgePreviousWordBag());
        fts.add(new EdgePreviousWord());
        
        // Add this for 1st-order Semi-CRF
        // fts.add(new FirstOrderTransition());
        
        // Add this for 2nd-order CRF and Semi-CRF
        // fts.add(new SecondOrderTransition());
        
        // Add this for 3rd-order CRF and Semi-CRF
        // fts.add(new ThirdOrderTransition());
        
        // Process parameters
        Params params = new Params(configFile, labelmap.size());
        
        // Initialize feature generator
        featureGen = new FeatureGenerator(fts, params);
    }

    /**
     * Train the high-order semi-CRF.
     */
    public void train() throws Exception {
        // Set training file name and create output directory
        String trainFilename = "ref.train";
        File dir = new File("learntModels/");
        dir.mkdirs();
        
        // Read training data and save the label map
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
    public void test() throws Exception {
    	// Read label map, features, and CRF model
        labelmap.read("learntModels/labelmap");
        createFeatureGenerator();
        featureGen.read("learntModels/features");
        highOrderSemiCrfModel = new HighOrderSemiCRF(featureGen);
        highOrderSemiCrfModel.read("learntModels/crf");
        
        // Run Viterbi algorithm
        System.out.print("Running Viterbi...");
        String testFilename = "ref.test";
        DataSet testData = readTagged(testFilename);
        long startTime = System.currentTimeMillis();
        highOrderSemiCrfModel.runViterbi(testData.getSeqList());
        System.out.println("done in " + (System.currentTimeMillis() - startTime) + " ms");
        
        // Print out the predicted data
        File dir = new File("out/");
        dir.mkdirs();
        testData.writeToFile("out/ref.test");
        
        // Score the results
        System.out.println("Scoring results...");
        startTime = System.currentTimeMillis();
        DataSet trueTestData = readTagged(testFilename);
        Scorer scr = new Scorer(trueTestData.getSeqList(), testData.getSeqList(), labelmap, false);
        scr.phraseScore();
        System.out.println("done in " + (System.currentTimeMillis() - startTime) + " ms");
    }

    /**
     * Main class
     */
    public static void main(String argv[]) throws Exception {
        ReferenceTagger refTagger = new ReferenceTagger(argv[1]);
        if (argv[0].toLowerCase().equals("all")) {
            refTagger.train();
            refTagger.test();
        } else if (argv[0].toLowerCase().equals("train")) {
            refTagger.train();
        } else if (argv[0].toLowerCase().equals("test")) {
            refTagger.test();
        }
    }
}
