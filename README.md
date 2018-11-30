This is a source code for High-order Semi-Markov Conditional Random Field model

=== WARNING ===

HOSemiCRF requires a lot of memory usage. It is best to run the program 
in parallel on a computing cluster with lots of memory.

=== COMPILATION STEPS ===

Requirement: Apache Ant (http://ant.apache.org/)

1. Download the HOSemiCRF repository as a zip file: HOSemiCRF-master.zip
2. Unzip the file:

    unzip HOSemiCRF-master.zip

3. Compile the program:

    cd HOSemiCRF-master
    
    ant

=== RUN THE PUNCTUATION PREDICTION PROGRAM ===

    cp dist/lib/HOSemiCRF.jar run/punc/
    cd run/punc
    java -cp "HOSemiCRF.jar" Applications.PunctuationPredictor all punc.conf
    
=== RUN THE REFERENCE PREDICTION PROGRAM ===
    
    cp dist/lib/HOSemiCRF.jar run/ref/
    cd run/ref
    java -cp "HOSemiCRF.jar" Applications.ReferenceTagger all ref.conf
    
=== RUN THE OCR PROGRAM ===

Download data from http://www.seas.upenn.edu/~taskar/ocr/ to the folder run/ocr/
    
    cp dist/lib/HOSemiCRF.jar run/ocr/
    cd run/ocr
    java -cp "HOSemiCRF.jar" OCR.OCR all ocr.conf 0
    
=== MORE INFO ===

Please visit: https://github.com/nvcuong/HOSemiCRF/wiki
