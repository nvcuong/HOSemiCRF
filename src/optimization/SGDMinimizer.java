package optimization;

/**
 * implementation of Stochastic Gradient Descent
 * @author tndoan
 *
 */
public class SGDMinimizer extends Minimizer {

	@Override
	public double[] minimize(AbstractSVRGFunction f, double[] init,
			double learningRate, int maxPasses, double funcTol) {
		
		double[] result = new double[init.length];
		System.arraycopy(init, 0, result, 0, init.length);
		double pre_obj = Double.MAX_VALUE;
		
		for (int i = 0; i < maxPasses; i++){
			for (int j = 0; j < f.getNumberOfDataPoints(); j++){
				
				double[] dev = f.takeDerivative(result, j);
				for (int k = 0; k < dev.length; k++){
					result[k] -= learningRate * dev[k];
				}
//				System.out.println(f.valueAt(result));
			}
			
			// check convergence
			double cur_obj = f.valueAt(result);
			if (i > 1 && Math.abs(pre_obj - cur_obj) < funcTol) {
				this.isConv = true;
				break;
			}
			pre_obj = cur_obj;
			System.out.println(f.valueAt(result));
		}
		
		return result;
	}
}
