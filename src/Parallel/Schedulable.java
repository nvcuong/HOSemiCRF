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
