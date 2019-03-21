package optimization;

/**
 * abstract class for implementing first-order optimization
 * @author tndoan
 *
 */
public abstract class Minimizer {
	
	/**
	 * isConv = false if iteration terminates because of running more than max iterations; otherwise, true
	 */
	protected boolean isConv;
	
	/**
	 * after running algorithm, isConv = true if algorithm terminates because of convergence; 
	 * otherwise(reach maximum number of iteration) isConv = false
	 * @return isConv
	 */
	public boolean isConv() {
		return isConv;
	}

	/**
	 * finding value that minimize function via 1st-order optimization
	 * @param f		function that we want to optimize
	 * @param init	initial values
	 * @return		value that minimize function
	 */
	public double[] minimize(AbstractSVRGFunction f, double[] init){
		return minimize(f, init, 0.1);
	}
	
	/**
	 * finding value that minimize function via 1st-order optimization
	 * @param f				function that we want to optimize
	 * @param init			initial values
	 * @param learningRate	learning rate of each iteration
	 * @return				value that minimize function
	 */
	public double[] minimize(AbstractSVRGFunction f, double[] init, double learningRate){
		return minimize(f, init, learningRate, 50);
	}
	
	/**
	 * finding value that minimize function via 1st-order optimization
	 * @param f				function that we want to optimize
	 * @param init			initial values
	 * @param learningRate	learning rate of each iteration
	 * @param maxPasses		maximum number of data pass
	 * @return				value that minimize function
	 */
	public double[] minimize(AbstractSVRGFunction f, double[] init, double learningRate, int maxPasses){
		return minimize(f, init, learningRate, maxPasses, 0.001);
	}
	
	/**
	 * finding value that minimize function via 1st-order optimization
	 * @param f				function that we want to optimize
	 * @param init			initial values
	 * @param learningRate	learning rate of each iteration
	 * @param maxPasses		maximum number of data pass
	 * @param funcTol		threshold to stop iteration before reaching max number of iteration
	 * @return				value that minimize function
	 */
	public abstract double[] minimize(AbstractSVRGFunction f, double[] init, double learningRate, int maxPasses, double funcTol);
}