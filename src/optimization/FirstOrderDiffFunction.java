package optimization;

import java.util.ArrayList;

import HOSemiCRF.DataSequence;
import HOSemiCRF.FeatureGenerator;
import HOSemiCRF.LogliComputer;
import HOSemiCRF.Loglikelihood;
import Parallel.Scheduler;

public class FirstOrderDiffFunction extends AbstractSVRGFunction {
	
	/**
	 * Feature Generator
	 */
    FeatureGenerator featureGen; // Feature generator

	public FirstOrderDiffFunction(FeatureGenerator fg, ArrayList<DataSequence> data) {
		this.data = data;
		this.featureGen = fg;
	}
	
	@Override
	public double valueAt(double[] w) {
        double logli = 0;
        for (int i = 0; i < w.length; i++) {
            logli -= ((w[i] * w[i]) * featureGen.getParams().getInvSigmaSquare()) / 2;
        }
        
        Loglikelihood l = new Loglikelihood(logli, new double[w.length]); // we dont care about derivative

        LogliComputer logliComp = new LogliComputer(w, featureGen, data, l);
        Scheduler sch = new Scheduler(logliComp, featureGen.getParams().getNumthreads(), Scheduler.DYNAMIC_NEXT_AVAILABLE);
        try {
            sch.run();
        } catch (Exception e) {
            System.out.println("Errors occur when training in parallel! " + e);
        }

        // Change sign to maximize and divide the values by size of dataset
        double result = l.getLogli();
        int n = data.size();
        result = -(result / n);
		return result;
	}

	@Override
	public double[] takeDerivative(double[] w) {
        double[] derivatives = new double[w.length];
        for (int i = 0; i < w.length; i++) {
            derivatives[i] -= (w[i] * featureGen.getParams().getInvSigmaSquare());
        }

        Loglikelihood logli = new Loglikelihood(0, derivatives); // we dont care loglikelihood value
        LogliComputer logliComp = new LogliComputer(w, featureGen, data, logli);
        Scheduler sch = new Scheduler(logliComp, featureGen.getParams().getNumthreads(), Scheduler.DYNAMIC_NEXT_AVAILABLE);
        try {
            sch.run();
        } catch (Exception e) {
            System.out.println("Errors occur when training in parallel! " + e);
        }

        // Change sign to maximize and divide the values by size of dataset        
        int n = data.size();
        
        double[] result = new double[w.length];
        System.arraycopy(logli.getDerivatives(), 0, result, 0, w.length);
        for (int i = 0; i < result.length; i++) {
            result[i] = -(result[i] / n);
        }
        
        return result;
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
		double[] result = new double[w.length];
        for (int i = 0; i < w.length; i++) {
            result[i] -= (w[i] * featureGen.getParams().getInvSigmaSquare());
        }
        
        Loglikelihood llh = new Loglikelihood(0, result); // can set loglikelihood any value; we dont care about this value
        LogliComputer llc = new LogliComputer(w, featureGen, data, llh);
        Loglikelihood l = (Loglikelihood) llc.compute(index);
        
        double[] d = l.getDerivatives(); 
        
        for (int i = 0; i < w.length; i++) {
        	result[i] += d[i];
        }
        
        // Change sign to maximize and divide the values by size of dataset        
        int n = data.size();
        for (int i = 0; i < w.length; i++){
            result[i] = -(result[i] / n);
        }
		return result;
	}
}