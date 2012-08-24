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

package Parallel;

import java.util.*;

/**
 * Timer class
 * @author Ye Nan
 */
public class Timer {

    public static Hashtable<String, Double> records = new Hashtable<String, Double>();
    public static Vector<Double> stimes = new Vector<Double>();
    public static double stime, etime;

    public static void report() {
        System.out.println("Running times");
        System.out.println(records);
    }

    public static void start() {
        stimes.add((double) System.currentTimeMillis());
    }

    public static void end(String msg) {
        etime = System.currentTimeMillis();
        stime = stimes.lastElement();
        stimes.remove(stimes.size() - 1);
        System.out.println(msg + " Time taken: " + (etime - stime) / 1000 + "s");
    }

    public static void record(String task) {
        etime = System.currentTimeMillis();
        stime = stimes.lastElement();
        stimes.remove(stimes.size() - 1);
        double t = (etime - stime) / 1000;
        if (records.containsKey(task)) {
            double oldT = records.get(task);
            records.put(task, oldT + t);
        } else {
            records.put(task, t);
        }
    }
}
