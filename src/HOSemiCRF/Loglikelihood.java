/*
Copyright (C) 2012 Nguyen Viet Cuong, Ye Nan, Sumit Bhagwani

This file is part of HOSemiCRF.

HOSemiCRF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

HOSemiCRF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with HOSemiCRF. If not, see <http://www.gnu.org/licenses/>.
*/

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
