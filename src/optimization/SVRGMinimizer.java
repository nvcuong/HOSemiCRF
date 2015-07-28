package optimization;

import java.util.Random;

/**
 * It is the implementation of SVRG algorithm
 * http://stat.rutgers.edu/home/tzhang/papers/nips13-svrg.pdf
 * @author tndoan
 *
 */
public class SVRGMinimizer extends Minimizer {

	@Override
	public double[] minimize(AbstractSVRGFunction f, double[] init,
			double learningRate, int maxPasses, double funcTol) {
		int n = f.getNumberOfDataPoints();
		int upFreq = 2 * n;
		int d = init.length;

		double[] w_0_tilde = new double[d];
		double[] mu_tilde = new double[d];
		double[] w_tilde = new double[d];
		double[] w_0 = new double[d];

		Random rand = new Random();
		
		// copy init value
		System.arraycopy(init, 0, w_0_tilde, 0, d);
		double pre_obj = Double.MAX_VALUE;
		
		for (int s = 0; s < maxPasses; s++){
			// w_tilde = w_0_tilde
			System.arraycopy(w_0_tilde, 0, w_tilde, 0, d);
			
			// mu_tilde = (\sum_{i=1}^n \delta \psi_i (w_tilde)) / n
			System.arraycopy(f.takeDerivative(w_tilde), 0, mu_tilde, 0, d);
			for (int i = 0; i < d; i++)
				mu_tilde[i] /= (double) n;
			
			//w_0 = w_tilde
			System.arraycopy(w_tilde, 0, w_0, 0, d);
			
			for (int t = 0; t < upFreq; t++){
//				System.out.println("t:" +t);
				int i_t = rand.nextInt(n);
				double[] f1 = f.takeDerivative(w_0, i_t);
				double[] f2 = f.takeDerivative(w_tilde, i_t);
				
				for (int i = 0; i < d; i++){
					w_0[i] = w_0[i] - learningRate * (f1[i] - f2[i] + mu_tilde[i]);
				}
			}
			
			// w_0_tilde = w_0
			System.arraycopy(w_0, 0, w_0_tilde, 0, d);
			
			// check convergence
			double curr_obj = f.valueAt(w_0_tilde);
			
			double diff = Math.abs(curr_obj - pre_obj);
			if (s > 1 && diff < funcTol) {
				// s > 1 to ensure that objective function is calculated at least 1 time.
				this.isConv = true;
				break;
			}
			System.out.println("Objective function pre:" + pre_obj + " curr:" + curr_obj + " funcTol:" + funcTol);
			pre_obj = curr_obj;
		}
		
		return w_0_tilde;
	}
}
