package optimization;

import java.util.ArrayList;

import HOSemiCRF.DataSequence;

/**
 * It is the abstract of function
 * @author tndoan
 *
 */
public abstract class AbstractSVRGFunction  {
	// similar AbstractStochasticCachingDiffUpdateFunction
	
	protected ArrayList<DataSequence> data;
	
	/**
	 * 
	 * @return the number of data points
	 */
	public int getNumberOfDataPoints(){
		return data.size();
	}

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
}