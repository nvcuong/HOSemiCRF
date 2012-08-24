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

import java.util.*;
import java.io.*;

/**
 * Label map class
 * @author Nguyen Viet Cuong
 */
public class LabelMap {

    ArrayList<String> mapTable; // List of labels

	/**
	 * Construct an empty label map.
	 */
    public LabelMap() {
        mapTable = new ArrayList<String>();
    }

	/**
	 * Return the size of the map.
	 * @return Number of labels
	 */
    public int size() {
        return mapTable.size();
    }

	/**
	 * Return the index of a label.
	 * If there is no such label, add it to the map and return its index.
	 * @param labelStr Input label
	 * @return Index of the input label
	 */
    public int map(String labelStr) {
        int index = mapTable.indexOf(labelStr);
        if (index == -1) {
            mapTable.add(labelStr);
            index = mapTable.size()-1;
        }
        return index;
    }

	/**
	 * Return the label with a given index.
	 * @param l Index of the label
	 * @return The label string
	 */
    public String revMap(int l) {
        return mapTable.get(l);
    }

	/**
	 * Map labels into their indices.
	 * @param labelStrList List of label strings
	 * @return List of indices of the input labels
	 */
    public int[] mapArrayList(ArrayList<String> labelStrList) {
        int[] result = new int[labelStrList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = map(labelStrList.get(i));
        }
        return result;
    }
	
	/**
	 * Map label indices into label strings.
	 * @param labels Label array
	 * @return Label string array
	 */
	public String[] revArray(int[] labels) {
		String[] result = new String[labels.length];
		for (int i = 0; i < labels.length; i++) {
			result[i] = revMap(labels[i]);
		}
		return result;
	}

	/**
	 * Write the label map into a file.
	 * @param filename Name of the output file
	 */
    public void write(String filename) throws IOException {
        PrintWriter out = new PrintWriter(new FileOutputStream(filename));
        out.println(mapTable.size());
        for (int i = 0; i < mapTable.size(); i++) {
            out.println(mapTable.get(i));
        }
        out.close();
    }

	/**
	 * Read the label map from a file.
	 * @param filename Name of the input file
	 */
    public void read(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        int size = Integer.parseInt(in.readLine());
        mapTable = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            mapTable.add(in.readLine());
        }
        in.close();
    }
}
