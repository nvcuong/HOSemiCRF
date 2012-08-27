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
 * Parallelization scheduler
 * @author Ye Nan
 */
public class Scheduler {

    public final static int STATIC_UNIFORM_ALLOCATE = 0;
    public final static int DYNAMIC_UNIFORM_ALLOCATE = 1;
    public final static int DYNAMIC_NEXT_AVAILABLE = 2;

    private int[] curIDs = null;

    Schedulable task;
    int nThreads;
    int policy = 0;
    TaskThread[] threads;

    public Scheduler(Schedulable task, int nThreads, int policy) {
        this.policy = policy;
        this.task = task;
        this.nThreads = nThreads;
        threads = new TaskThread[nThreads];
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new TaskThread(task, this, i);
        }

        int nTasks = task.getNumTasks();
        if (policy == STATIC_UNIFORM_ALLOCATE) {
            int[][] taskIDs = new int[nThreads][];
            for (int i = 0; i < nThreads; i++) {
                int n = nTasks / nThreads;
                if (i < nTasks % nThreads) {
                    n++;
                }
                taskIDs[i] = new int[n];
                for (int j = 0; j < n; j++) {
                    taskIDs[i][j] = i + j * nThreads;
                }
                threads[i].setTaskIDs(taskIDs[i]);
            }
        }
    }

    public int fetchTaskID(int threadID) { // no need to synchronize
        if (policy == DYNAMIC_UNIFORM_ALLOCATE) {
            if (curIDs == null) {
                curIDs = new int[nThreads];
                for (int i = 0; i < nThreads; i++) {
                    curIDs[i] = i - nThreads;
                }
            }
            if (curIDs[threadID] < task.getNumTasks() - 1) {
                curIDs[threadID] += nThreads;
            } else {
                return task.getNumTasks();
            }
            return curIDs[threadID];
        } else {
            return task.fetchCurrTaskID();
        }
    }

    public void run() throws Exception {
        for (int i = 0; i < nThreads; i++) {
            threads[i].start();
        }
        for (int i = 0; i < nThreads; i++) {
            threads[i].join();
        }
    }
    
    // Simple test program
    public static void main(String[] args) throws Exception {
        
        class SimpleTask implements Schedulable {

            double[] ans;
            int curID;
            static final int N = 1000000;
            int nTasks;

            public SimpleTask(int nTasks) {
                curID = -1;
                this.nTasks = nTasks;
                ans = new double[2];
            }

            public void showResult() {
                System.out.println(ans[0] + " " + ans[1]);
            }

            public Object compute(int taskID) {
                Random r = new Random(taskID);
                int a = r.nextInt();
                for (int i = 0; i < 10000; i++) {
                    for (int j = 0; j < 10000; j++) {
                        for (int k = 0; k < 2; k++) {
                            a = taskID + i + j + k + r.nextInt();
                        }
                    }
                }

                double[] result = new double[2];
                result[0] = a;
                result[1] = 2;
                return result;
            }

            public int getNumCompletedTasks() {
                return curID;
            }

            public int getNumTasks() {
                return nTasks;
            }

            public synchronized int fetchCurrTaskID() {
                if (curID < nTasks) {
                    curID++;
                }
                return curID;
            }

            public synchronized void update(Object partialResult) {
                double[] res = (double[]) partialResult;
                ans[0] += res[0];
                ans[1] += res[1];
                //showResult();
            }
        }

        if (args.length == 0) {
            System.out.println("Usage: java Scheduler nTasks nThreads");
            System.out.println("  Expected observation:");
            System.out.println("  TotalRunningTime ~= SingleTaskTime * (nTasks/nThreads)");
            System.exit(0);
        }

        Timer.start();
        (new SimpleTask(1)).compute(0);
        Timer.record("single");

        Timer.start();
        int policy = Scheduler.STATIC_UNIFORM_ALLOCATE;
        policy = Scheduler.DYNAMIC_NEXT_AVAILABLE;
        (new Scheduler(new SimpleTask(Integer.parseInt(args[0])), Integer.parseInt(args[1]), policy)).run();
        Timer.record("thread");
        Timer.report();
    }
}
