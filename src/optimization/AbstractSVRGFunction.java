package optimization;


/**
 * It is the abstract of function
 * @author tndoan
 *
 */
public abstract class AbstractSVRGFunction  {
	// similar AbstractStochasticCachingDiffUpdateFunction
	
	/**
	 * 
	 * @return the number of data points
	 */
	public abstract int getNumberOfDataPoints();

	/**
	 * Get value of function
	 * @param w	value of parameters
	 * @return	value of function
	 */
	public abstract double valueAt(double[] w);
	
	/**
	 * take derivative of function of whole data
	 * @param w	value of parameters
	 * @return	array corresponding to the derivative
	 */
	public abstract double[] takeDerivative(double[] w);
	
	/**
	 * take derivative of function of data point whose indices are given
	 * @param w		value of parameters
	 * @param index	array which contains index of data point that we will use
	 * @return		array of derivative
	 */
	public abstract double[] takeDerivative(double[] w, int[] index);
	
	/**
	 * take derivative of function of data point whose index is given
	 * @param w		value of parameter
	 * @param index index of data point in array
	 * @return		derivative of function with parameter w at data point whose index is specified
	 */
	public abstract double[] takeDerivative(double[] w, int index);
	
	/**
	 * take derivative of function of each data point
	 * @param w		value of parameter
	 * @return		2d array derivative of function with parameter w at each data point. result[i] is the derivative of data point i.
	 */
	public abstract double[][] takeEachDerivative(double[] w);
}