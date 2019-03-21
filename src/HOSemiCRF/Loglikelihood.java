package HOSemiCRF;

/**
 * Loglikelihood class
 * @author Nguyen Viet Cuong
 * @author tndoan
 */
public class Loglikelihood {

    public double logli; // Loglikelihood value
    public double derivatives[]; // Loglikelihood derivatives

    /**
     * Construct a loglikelihood with a given number of features.
     * @param n Number of features
     */
    public Loglikelihood(int n) {
        logli = 0;
        derivatives = new double[n];
    }
    
    /**
     * construct loglikelihood with initial loglikelihood and derivative
     * @param logli	initial loglikelihood
     * @param dev	initial derivative
     */
    public Loglikelihood(double logli, double[] dev) {
    	this.logli = logli;
    	derivatives = new double[dev.length];
    	System.arraycopy(dev, 0, derivatives, 0, dev.length);
    }

    /**
     * 
     * @return the log likelihood
     */
	public double getLogli() {
		return logli;
	}

	/**
	 * 
	 * @return the derivative
	 */
	public double[] getDerivatives() {
		return derivatives;
	}
}
