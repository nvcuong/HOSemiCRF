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

/**
 * Class for word dictionary
 * @author Nguyen Viet Cuong
 */
public class Dictionary {

    HashMap dict; // Map from words to number of their occurrence
    HashSet<String> knownLCWords; // All lower-case words
	
	/**
	 * Construct an empty dictionary.
	 */
	public Dictionary() {
		dict = new HashMap();
		knownLCWords = new HashSet<String>();
	}
	
	/**
	 * Construct a dictionary from a training file.
	 * @param trainfile Name of the training file
	 */
	public Dictionary(String trainfile) throws Exception {
		dict = new HashMap();
		knownLCWords = new HashSet<String>();
		
        BufferedReader in = new BufferedReader(new FileReader(trainfile));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.length() > 0) {
                StringTokenizer toks = new StringTokenizer(line);
                String word = toks.nextToken();
                addWord(word);
                if (Character.isLowerCase(word.charAt(0))) {
                    knownLCWords.add(word);
                }
            }
        }
        in.close();
    }

	/**
	 * Check if the dictionary contains a given lower-case word.
	 * @param word The input word
	 * @return True if the dictionary contains the word, false otherwise
	 */
    public boolean containsLCWord(String word) {
        return knownLCWords.contains(word);
    }

	/**
	 * Check if the dictionary contains a given word.
	 * @param word The input word
	 * @return True if the dictionary contains the word, false otherwise
	 */
    public boolean containsWords(String word) {
        return (dict.get(word) != null);
    }

	/**
	 * Add a word into the dictionary.
	 * @param word The word to be added
	 */
    public void addWord(String word) {
        if (dict.get(word) == null) {
            dict.put(word, new Integer(1));
        } else {
            Integer count = (Integer) dict.get(word);
            dict.put(word, new Integer(count.intValue() + 1));
        }
    }

	/**
	 * Return the number of occurrence of a given word.
	 * @param word The input word
	 * @return The number of occurrence of the input word
	 */
    public int countWord(String word) {
        if (dict.get(word) == null) {
            return 0;
        } else {
            return ((Integer) dict.get(word)).intValue();
        }
    }

	/**
	 * Write a dictionary to a file.
	 * @param filename Name of the output file
	 */
    public void write(String filename) throws Exception {
        PrintWriter out = new PrintWriter(new FileOutputStream(filename));
        Object[] words = dict.keySet().toArray();
        out.println(words.length);
        for (int i = 0; i < words.length; i++) {
            out.println(words[i] + " " + dict.get(words[i]));
        }
        out.close();
    }

	/**
	 * Read a dictionary from a file.
	 * @param filename Name of the dictionary file
	 */
    public void read(String filename) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        dict = new HashMap();
        knownLCWords = new HashSet<String>();
        int numWords = Integer.parseInt(in.readLine());
        for (int i = 0; i < numWords; i++) {
            StringTokenizer toks = new StringTokenizer(in.readLine());
            String word = toks.nextToken();
            int count = Integer.parseInt(toks.nextToken());
            dict.put(word, new Integer(count));
            if (Character.isLowerCase(word.charAt(0))) {
                knownLCWords.add(word);
            }
        }
        in.close();
    }
}
