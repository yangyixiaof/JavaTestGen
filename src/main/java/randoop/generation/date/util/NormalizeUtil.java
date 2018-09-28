package randoop.generation.date.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.influence.Reward;

public class NormalizeUtil {
	
	/**
	 * we assume that the values in Reward could be modified.
	 * 
	 * @param wait_select
	 */
	public static <T> void NormalizeRewards(Map<T, Reward> wait_select) {
		Collection<Reward> vals = wait_select.values();
		int val_size = vals.size();
		if (val_size > 0) {
			ArrayList<Double[]> rs = new ArrayList<Double[]>();
			{
				Reward r = vals.iterator().next();
				int r_num = r.GetNumberOfRewards();
				for (int i = 0; i < r_num; i++) {
					rs.add(new Double[val_size]);
				}
			}
			{
				int v_idx = -1;
				Iterator<Reward> v_itr = vals.iterator();
				int previous_r_num = -1;
				while (v_itr.hasNext()) {
					v_idx++;
					Reward r = v_itr.next();
					double[] rewards = r.GetRewards();
					int r_num = rewards.length;
					Assert.isTrue(previous_r_num == -1 || previous_r_num == r_num);
					previous_r_num = r_num;
					for (int i=0;i<r_num;i++) {
						rs.get(i)[v_idx] = rewards[i];
					}
				}
			}
			{
				for (int i=0;i<rs.size();i++) {
					Double[] data = rs.get(i);
					double min = Collections.min(Arrays.asList(data));
					double max = Collections.max(Arrays.asList(data));
					double gap = max - min;
					Iterator<Reward> v_itr = vals.iterator();
					while (v_itr.hasNext()) {
						Reward r = v_itr.next();
						double[] rewards = r.GetRewards();
						rewards[i] = gap == 0 ? rewards[i] : (rewards[i] - min) / gap;
					}
				}
			}
		}
	}

	/**
	 * we assume that the values in Reward could be modified.
	 * 
	 * @param wait_select
	 */
	public static <T> void StandardizeRewards(Map<T, Reward> wait_select) {
		Collection<Reward> vals = wait_select.values();
		int val_size = vals.size();
		if (val_size > 0) {
			ArrayList<double[]> rs = new ArrayList<double[]>();
			{
				Reward r = vals.iterator().next();
				int r_num = r.GetNumberOfRewards();
				for (int i = 0; i < r_num; i++) {
					rs.add(new double[val_size]);
				}
			}
			{
				int v_idx = -1;
				Iterator<Reward> v_itr = vals.iterator();
				int previous_r_num = -1;
				while (v_itr.hasNext()) {
					v_idx++;
					Reward r = v_itr.next();
					double[] rewards = r.GetRewards();
					int r_num = rewards.length;
					Assert.isTrue(previous_r_num == -1 || previous_r_num == r_num);
					previous_r_num = r_num;
					for (int i=0;i<r_num;i++) {
						rs.get(i)[v_idx] = rewards[i];
					}
				}
			}
			{
				for (int i=0;i<rs.size();i++) {
					double[] data = rs.get(i);
					double avg = average(data);
					double std = standardDeviation(data);
					Iterator<Reward> v_itr = vals.iterator();
					while (v_itr.hasNext()) {
						Reward r = v_itr.next();
						double[] rewards = r.GetRewards();
						rewards[i] = std == 0 ? rewards[i] : (rewards[i] - avg) / std;
					}
				}
			}
		}
	}

	public static void normalize(double[] data) {
		if (data != null && data.length > 0) {
			double avg = average(data);
			double std = standardDeviation(data);
			for (int i = 0; i < data.length; i++) {
				data[i] = std == 0 ? data[i] : (data[i] - avg) / std;
			}
		}
	}

	// /**
	// * 0 average: X(norm) = (X - ¦Ì) / ¦Ò X(norm) = (X - average) / standard
	// deviation
	// *
	// * @param points
	// * original data
	// * @return
	// */
	// public static double[][] normalize4ZScore(double[][] points) {
	// if (points == null || points.length < 1) {
	// return points;
	// }
	// double[][] p = new double[points.length][points[0].length];
	// double[] matrixJ;
	// double avg;
	// double std;
	// for (int j = 0; j < points[0].length; j++) {
	// matrixJ = getMatrixCol(points, j);
	// avg = average(matrixJ);
	// std = standardDeviation(matrixJ);
	// for (int i = 0; i < points.length; i++) {
	// p[i][j] = std == 0 ? points[i][j] : (points[i][j] - avg) / std;
	// }
	// }
	// return p;
	// }

	/**
	 * variance s^2=[(x1-x)^2 +...(xn-x)^2]/n
	 *
	 * @param x
	 * @return variance
	 */
	public static double variance(double[] x) {
		int m = x.length;
		double sum = 0;
		for (int i = 0; i < m; i++) {
			sum += x[i];
		}
		double dAve = sum / m;
		double dVar = 0;
		for (int i = 0; i < m; i++) {
			dVar += (x[i] - dAve) * (x[i] - dAve);
		}
		return dVar / m;
	}

	/**
	 * ¦Ò = sqrt(s^2)
	 *
	 * @param x
	 *            x
	 * @return standard deviation
	 */
	public static double standardDeviation(double[] x) {
		return Math.sqrt(variance(x));
	}

	/**
	 * average
	 *
	 * @param x
	 * @return average
	 */
	public static double average(double[] x) {
		int m = x.length;
		double sum = 0;
		for (int i = 0; i < m; i++) {
			sum += x[i];
		}
		double dAve = sum / m;
		return dAve;
	}

}
