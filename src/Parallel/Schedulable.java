package Parallel;

/**
 * Interface for one schedulable task
 * @author Ye Nan
 */
public interface Schedulable {

	/**
	 * Process one task.
	 * @param taskID ID of the task
	 * @return Partial result
	 */
	public Object compute(int taskID); //can do synchronized update here if partial result takes too much memory

	/**
	 * Return the total number of tasks.
	 * @return Total number of tasks
	 */
	 public int getNumTasks();

	/**
	 * Return the next task ID.
	 * @return Next task ID
	 */
	 public int fetchCurrTaskID(); // synchronize

	/**
	 * Update using the partial result.
	 * @param partialResult Partial result
	 */
	 public void update(Object partialResult); // synchronize
}
