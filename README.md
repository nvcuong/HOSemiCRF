Copyright (C) 2012 Nguyen Viet Cuong, Ye Nan, Sumit Bhagwani

This is the README file for HOSemiCRF.

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

=== WARNING ===

HOSemiCRF requires a lot of memory usage. It is best to run the program 
in parallel on a computing cluster with lots of memory.

=== COMPILATION STEPS ===

Requirement: Apache Ant (http://ant.apache.org/)

1. Download the HOSemiCRF repository as a zip file: nvcuong-HOSemiCRF-xxxxxxx.zip
2. Unzip the file:
    unzip nvcuong-HOSemiCRF-xxxxxxx.zip
3. Compile the program:
    cd nvcuong-HOSemiCRF-xxxxxxx
    ant

=== RUN THE PROGRAM ===

1. Copy the jar file to "run"
    cp dist/lib/HOSemiCRF.jar run/
2. Run the program
    cd run
    java -cp "HOSemiCRF.jar" Applications.ReferenceTagger all ref.conf
    or
    java -cp "HOSemiCRF.jar" Applications.PunctuationPredictor all punc.conf

=== MORE INFO ===

Please visit: 
    https://github.com/nvcuong/HOSemiCRF/wiki
