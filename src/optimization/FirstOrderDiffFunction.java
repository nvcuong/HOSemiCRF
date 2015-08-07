package optimization;

import java.util.ArrayList;

import HOSemiCRF.DataSequence;
import HOSemiCRF.ExtLogliComputer;
import HOSemiCRF.FeatureGenerator;
import HOSemiCRF.LogliComputer;
import HOSemiCRF.Loglikelihood;
import Parallel.Scheduler;

public class FirstOrderDiffFunction extends AbstractSVRGFunction {
	
    FeatureGenerator featureGen; // Feature generator
	ArrayList<DataSequence> data;
	
	// Stored results
	private Loglikelihood logli;
	private double[][] eachDerivatives;

	public FirstOrderDiffFunction(FeatureGenerator fg, ArrayList<DataSequence> data) {
		this.data = data;
		this.featureGen = fg;
	}
	
	/**
	 * Compute loglikelihood, derivatives for all data, and derivatives at each data point
	 * Store in eachDerivatives
	 * @param w	value of parameters
	 */
	public void computeAllValues(double[] w) {
		// init eachDerivative
		logli = new Loglikelihood(w.length);
		eachDerivatives = new double[data.size()][w.length];
		for (int i = 0; i < w.length; i++){
			for (int j = 0; j < data.size(); j++){
				eachDerivatives[j][i] = -(w[i] * featureGen.getParams().getInvSigmaSquare());  
			}
            logli.logli -= ((w[i] * w[i]) * featureGen.getParams().getInvSigmaSquare()) / 2;
            logli.derivatives[i] = -(w[i] * featureGen.getParams().getInvSigmaSquare());
		}
		
		ExtLogliComputer logliComp = new ExtLogliComputer(w, featureGen, data, logli, eachDerivatives);
        Scheduler sch = new Scheduler(logliComp, featureGen.getParams().getNumthreads(), Scheduler.DYNAMIC_NEXT_AVAILABLE);
        try {
            sch.run();
        } catch (Exception e) {
            System.out.println("Errors occur when training in parallel! " + e);
        }

        // Change sign to maximize       
        for (int i = 0; i < w.length; i++) {
            logli.derivatives[i] = -logli.derivatives[i] / data.size();
            for (int j = 0; j < data.size(); j++){
            	eachDerivatives[j][i] = -eachDerivatives[j][i] / data.size(); 
            }
        }
        logli.logli = -logli.logli / data.size();
	}
	
	/**
	 * Ensure that computeAllValues(w) was called before this
	 */
	@Override
	public double valueAt(double[] w) {
		return logli.getLogli();
	}

	@Override
	// Ensure that computeAllValues(w) was called before this
	public double[] takeDerivative(double[] w) {
		return logli.getDerivatives();
	}
	
	@Override
	// Ensure that computeAllValues(w) was called before this
	public double[][] takeEachDerivative(double[] w){
		return eachDerivatives;
	}

	@Override
	public double[] takeDerivative(double[] w, int[] index) {
		int l = w.length;
		double[] result = new double[l];
		
		for(int i : index){
			double[] eachDev = takeDerivative(w, i);
			for (int j = 0; j < l; j++){
				result[j] += eachDev[j];
			}
		}
		
		return result;
	}

	@Override
	public double[] takeDerivative(double[] w, int index) {	
        Loglikelihood llh = new Loglikelihood(w.length); // can set loglikelihood any value; we dont care about this value
        LogliComputer llc = new LogliComputer(w, featureGen, data, llh);
        double[] result = ((Loglikelihood) llc.compute(index)).getDerivatives();
        
        for (int i = 0; i < w.length; i++) {
            result[i] -= (w[i] * featureGen.getParams().getInvSigmaSquare());
            result[i] = -result[i] / data.size(); // Change sign to maximize 
        }     

		return result;
	}

	@Override
	public int getNumberOfDataPoints() {
		return data.size();
	}
}