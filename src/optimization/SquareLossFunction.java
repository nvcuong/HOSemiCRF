package optimization;

import java.util.ArrayList;

/**
 * implementation of square loss function. It is used to test optimization function
 * P =  \sum_{i=1}^n (w * x_i - y_i) ^ 2 / n
 * where n is total number of data point
 * @author tndoan
 *
 */
public class SquareLossFunction extends AbstractSVRGFunction{
	ArrayList<double[]> data; // data[i] = x_i
	ArrayList<Double> response; // response[i] = y_i
	
	public SquareLossFunction(ArrayList<double[]> array, double[] r) {
		assert(array.size() == r.length);
		
		// init data
		data = new ArrayList<>();
		for (double[] x : array){
			double[] d = new double[x.length];
			System.arraycopy(x, 0, d, 0, x.length);
			data.add(d);
		}
		
		// init response
		response = new ArrayList<>();
		for (double y : r)
			response.add(y);
	}

	@Override
	public int getNumberOfDataPoints(){
		return data.size();
	}
	
	@Override
	public double valueAt(double[] w) {
		int n = data.size(); // number of data points
		double result = 0;
		
		for (int i = 0; i < n; i++){
			double[] point = data.get(i);
			double y = response.get(i);
			double phi = 0; // phi = w * point; w and point are vectors
			for ( int j = 0; j < w.length; j++){
				phi += w[j] * point[j];
			}
			
			phi -= y;
			result += phi * phi;
		}
		
		result /= n;
		
		return result;
	}

	@Override
	public double[] takeDerivative(double[] w) {
		double[] result = new double[w.length];
		int n = data.size(); // number of data points
		
		for (int i = 0; i < n; i++){
			double[] r = takeDerivative(w, i);
			for (int j = 0; j < result.length; j++){
				result[j] += r[j];
			}
		}
		
		return result;
	}

	@Override
	public double[] takeDerivative(double[] w, int[] index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] takeDerivative(double[] w, int index) {
		double[] point = data.get(index);
		double r = response.get(index);
		
		double[] result = new double[w.length];
		
		double phi = 0.0;
		for (int i = 0; i < w.length; i++){
			phi += w[i] * point[i];
		}
		
		phi -= r;
		
		for (int i = 0; i < w.length; i++){
			result[i] = phi * point[i] * 2;
		}
		
		return result;
	}
}
