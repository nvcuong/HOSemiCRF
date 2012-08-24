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
import Applications.RelFeatures.*;

/**
 * Main class for the Relation Extraction task
 * @author Nguyen Viet Cuong
 */
public class RelationExtractor {
	
	HighOrderSemiCRF highOrderSemiCrfModel; // High-order semi-CRF model
	FeatureGenerator featureGen; // Feature generator
	LabelMap labelmap; // Label map
	String relType; // Relation type
	int relation; // Relation type ID
	String configFile; // Configuration filename

	/**
	 * Construct a relation extractor for a relation type from a configuration file.
	 * @param filename Name of configuration file
	 * @param rel Relation type
	 */
	public RelationExtractor(String filename, String rel) {
		labelmap = new LabelMap();
		relType = rel;
		relation = Integer.parseInt(relType.substring(relType.length() - 1));
		configFile = filename;
	}

	/**
	 * Read the training file.
	 * @param filename Name of the training file
	 * @param dict Dictionary of words in the training file
	 * @return The training data
	 */
    public DataSet readTagged(String filename, Dictionary dict) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));

        ArrayList td = new ArrayList();
        ArrayList<WordDetails> inps = new ArrayList<WordDetails>();
        ArrayList<String> labels = new ArrayList<String>();
        String line;

        while ((line = in.readLine()) != null) {
            if (line.length() > 0) {
                String[] toks = line.split("[ \t]");
                inps.add(new WordDetails(toks[0], toks[1], toks[2], toks[3], dict));
                labels.add(toks[4]);
            } else if (labels.size() > 0) {
                td.add(new DataSequence(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
                inps = new ArrayList<WordDetails>();
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
		fts.add(new CapitializationBag());
		fts.add(new PreviousCapitalizationBag());
		fts.add(new NextCapitalizationBag());
		fts.add(new PreviousWordCurrentPatternBag());
		fts.add(new NextWordCurrentPatternBag());
		fts.add(new PreviousTwoPatternBag());
		fts.add(new NextTwoPatternBag());
		fts.add(new PreviousCurrentNextPatternBag());
		fts.add(new LetterNGramsBag());
		
		fts.add(new EdgeBag());
		fts.add(new Edge());
		fts.add(new EdgeWordBag());
		fts.add(new EdgeWord());
		fts.add(new EdgePreviousWordBag());
		fts.add(new EdgePreviousWord());
		fts.add(new EdgePreviousPatternBag());
		fts.add(new EdgePreviousPattern());
		fts.add(new EdgeCapitalizationBag());
		fts.add(new EdgeCapitalization());
		fts.add(new EdgeTwoCapitalizationBag());
		fts.add(new EdgeTwoCapitalization());
		fts.add(new EdgeNextPatternBag());
		fts.add(new EdgeNextPattern());

        fts.add(new POSBag());
        fts.add(new PreviousPOSBag());
        fts.add(new NextPOSBag());
        fts.add(new PreviousTwoPOSBag());
        fts.add(new NextTwoPOSBag());

        fts.add(new NEBag());
        fts.add(new PreviousNEBag());
        fts.add(new NextNEBag());
        fts.add(new PreviousTwoNEBag());
        fts.add(new NextTwoNEBag());

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
        String trainFilename = "data/" + relType + ".train";
		File datadir = new File("data/");
		datadir.mkdirs();
		File modeldir = new File("learntModels/" + relType);
        modeldir.mkdirs();
		
		// Read training data and save the label map
		RelConverter.convert("tr", trainFilename, relation, true);
        Dictionary dict = new Dictionary(trainFilename);
        DataSet trainData = readTagged(trainFilename, dict);
		labelmap.write("learntModels/" + relType + "/labelmap");
		dict.write("learntModels/" + relType + "/dictionary");
		
		// Create and save feature generator
        createFeatureGenerator();
		featureGen.initialize(trainData.getSeqList());
		featureGen.write("learntModels/" + relType + "/features");

        // Train and save model
        highOrderSemiCrfModel = new HighOrderSemiCRF(featureGen);
        highOrderSemiCrfModel.train(trainData.getSeqList());
		highOrderSemiCrfModel.write("learntModels/" + relType + "/crf");
    }

	/**
	 * Test the high-order semi-CRF.
	 */
    public void test() throws Exception {
		// Read dictionary, label map, features, and CRF model
		Dictionary dict = new Dictionary();
		dict.read("learntModels/" + relType + "/dictionary");
		labelmap.read("learntModels/" + relType + "/labelmap");
		createFeatureGenerator();
		featureGen.read("learntModels/" + relType + "/features");
		highOrderSemiCrfModel = new HighOrderSemiCRF(featureGen);
		highOrderSemiCrfModel.read("learntModels/" + relType + "/crf");
		
		// Run Viterbi algorithm
		System.out.print("Running Viterbi...");
		String testFilename = "data/" + relType + ".test";
		RelConverter.convert("ts", testFilename, relation, false);
		DataSet testData = readTagged(testFilename, dict);
		long startTime = System.currentTimeMillis();
		highOrderSemiCrfModel.runViterbi(testData.getSeqList());
		System.out.println("done in " + (System.currentTimeMillis() - startTime) + " ms");
		
		// Print out the predicted data
		File dir = new File("out/");
		dir.mkdirs();
		testData.writeToFile("out/" + relType + ".test");
		
		// Score the results
		System.out.println("Scoring results...");
        startTime = System.currentTimeMillis();
		DataSet trueTestData = readTagged(testFilename, dict);
		Scorer scr = new Scorer(trueTestData.getSeqList(), testData.getSeqList(), labelmap, false);
		scr.phraseScore();
		System.out.println("done in " + (System.currentTimeMillis() - startTime) + " ms");
    }

	/**
	 * Main class
	 */
    public static void main(String argv[]) throws Exception {
        RelationExtractor relExtractor = new RelationExtractor(argv[1], argv[2]);
        if (argv[0].toLowerCase().equals("all")) {
            relExtractor.train();
            relExtractor.test();
        } else if (argv[0].toLowerCase().equals("train")) {
            relExtractor.train();
        } else if (argv[0].toLowerCase().equals("test")) {
            relExtractor.test();
        }
    }
}
