package HighOrderSemiCRF;

/**
 * Loglikelihood class
 * @author Nguyen Viet Cuong
 */
public class Loglikelihood {

    double logli; // Loglikelihood value
    double derivatives[]; // Loglikelihood derivatives

	/**
	 * Construct a loglikelihood with a given number of features.
	 * @param n Number of features
	 */
    public Loglikelihood(int n) {
        logli = 0;
        derivatives = new double[n];
    }
}
