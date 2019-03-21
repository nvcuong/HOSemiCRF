package HOSemiCRF;

import java.util.ArrayList;

/**
 * This class extends LogliComputer in order to do parallel and get the derivative of each data point.
 * @author tndoan
 *
 */
public class ExtLogliComputer extends LogliComputer {
	
	/**
	 * store derivative of each data points
	 */
	private double[][] eachDerivatives;

	public ExtLogliComputer(double[] lambdaValues, FeatureGenerator fgen,
			ArrayList<DataSequence> td, Loglikelihood loglh, double[][] eachDerivatives) {
		super(lambdaValues, fgen, td, loglh);
		this.eachDerivatives = eachDerivatives;
	}
	
	/**
	 * Override compute method of super class to store each derivative to {@link ExtLogliComputer#eachDerivatives}
	 */
	@Override
    public Object compute(int taskID) {
    	Object result = super.compute(taskID);
    	double[] d = ((Loglikelihood) result).derivatives;
    	
    	for(int i = 0; i < d.length; i++){
    		eachDerivatives[taskID][i] += d[i]; 
    	}

    	return result;
    }

	/**
	 * get the derivative of each data point
	 * @return	2d array result. result[i] is derivative of data point i.
	 */
	public double[][] getEachDerivative() {
		return eachDerivatives;
	}

}