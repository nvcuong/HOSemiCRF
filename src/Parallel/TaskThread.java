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

/**
 * Class for one task
 * @author Ye Nan
 */
class TaskThread extends Thread {

    int id;
    Schedulable task;
    Scheduler scheduler;
    boolean done = false; // True if task done
    int nTasks = 0;
    int[] taskIDs = null;

    public TaskThread(Schedulable task, Scheduler scheduler, int id) {
        this.scheduler = scheduler;
        this.task = task;
        this.id = id;
    }

    public void setTaskIDs(int[] taskIDs) {
        this.taskIDs = taskIDs;
    }

    @Override
    public void run() {
        Timer.start();
        while (true) {
            int taskID = -1;
            if (taskIDs == null) {
                taskID = scheduler.fetchTaskID(id);
                if (taskID >= task.getNumTasks()) {
                    break;
                }
            } else {
                if (nTasks == taskIDs.length) {
                    break;
                }
                taskID = taskIDs[nTasks];
            }

            nTasks++;
			
            Object res = task.compute(taskID);
            task.update(res);
        }
        done = true;
    }

    public boolean done() {
        return done;
    }

    public int getNumProcessedTasks() {
        return nTasks;
    }
}
