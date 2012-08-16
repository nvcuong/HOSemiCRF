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
