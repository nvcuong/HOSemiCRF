package ReferenceTagging;

import java.io.*;
import java.util.*;
import HighOrderSemiCRF.*;
import ReferenceTagging.Features.*;

/**
 * Main class for the Reference Tagging task.
 * @author Nguyen Viet Cuong
 */
class ReferenceTagging {

    HighOrderSemiCRF highOrderSemiCrfModel; // High-order semi-CRF model
    FeatureGenerator featureGen; // Feature generator
    LabelMap labelmap = new LabelMap(); // Label map
    String configFile; // Configuration filename

	/**
	* Construct a tagger from a configuration file.
	* @param filename Name of configuration file
	*/
	ReferenceTagging(String filename) {
		configFile = filename;
	}
	
	/**
	* Read the training file.
	* @param filename Name of the training file
	* @return The training data
	*/
    DataSet readTagged(String filename) throws IOException {
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
                td.add(new DataRecord(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
                inps = new ArrayList<String>();
                labels = new ArrayList<String>();
            }
        }
        if (labels.size() > 0) {
            td.add(new DataRecord(labelmap.mapArrayList(labels), inps.toArray(), labelmap));
        }

        in.close();
        return new DataSet(td);
    }

	/**
	* Add the feature types, process the parameters, and initialize the feature generator.
	*/
    void createFeatureGenerator() throws Exception {
		// Add feature types
        ArrayList<FeatureType> fts = new ArrayList<FeatureType>();
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
		
		//fts.add(new FirstOrderTransition());
        fts.add(new SecondOrderTransition());
        fts.add(new ThirdOrderTransition());
		
		// Process parameters
        Params params = new Params(configFile, labelmap.size());
		
		// Initialize feature generator
        featureGen = new FeatureGenerator(fts, params);
    }

	/**
	* Train the high-order semi-CRF.
	*/
    void train() throws Exception {
		// Set training file name and create output directory
        String trainFilename = "ref.train";
		File dir = new File("learntModels/");
        dir.mkdirs();
		
		// Read training data and save the label map
        DataSet trainData = readTagged(trainFilename);
		labelmap.write("learntModels/labelmap");
		
		// Create and save feature generator
        createFeatureGenerator();
        featureGen.initialize(trainData.getRecordList());
		featureGen.write("learntModels/features");
		
        // Train and save model
        highOrderSemiCrfModel = new HighOrderSemiCRF(featureGen);
        highOrderSemiCrfModel.train(trainData.getRecordList());
		highOrderSemiCrfModel.write("learntModels/crf");
    }

	/**
	* Test the high-order semi-CRF.
	*/
    void test() throws Exception {
        labelmap.read("learntModels/labelmap");
        createFeatureGenerator();
        featureGen.read("learntModels/features");
        highOrderSemiCrfModel = new HighOrderSemiCRF(featureGen);
        highOrderSemiCrfModel.read("learntModels/crf");

        System.out.print("Running Viterbi...");
        String testFilename = "ref.test";
        DataSet testData = readTagged(testFilename);
        long startTime = System.currentTimeMillis();
        highOrderSemiCrfModel.runViterbi(testData.getRecordList());
        System.out.println("done in " + (System.currentTimeMillis() - startTime) + " ms");

        File dir = new File("out/");
        dir.mkdirs();
        testData.writeToFile("out/ref.test");

        System.out.println("Scoring results...");
        startTime = System.currentTimeMillis();
		DataSet trueTestData = readTagged(testFilename);
		Scorer scr = new Scorer(trueTestData.getRecordList(), testData.getRecordList(), labelmap, false);
        scr.phraseScore();
        System.out.println("done in " + (System.currentTimeMillis() - startTime) + " ms");
    }

	/**
	* Main class
	*/
    public static void main(String argv[]) throws Exception {
        ReferenceTagging refTagger = new ReferenceTagging(argv[1]);
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
