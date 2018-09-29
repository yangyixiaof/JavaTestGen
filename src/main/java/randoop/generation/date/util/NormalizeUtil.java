package randoop.generation.date.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import randoop.generation.date.influence.Reward;

public class NormalizeUtil {

	public static <T> CopiedStructures<T> CopyAndGenerateNecessaryStructures(Map<T, Reward> wait_select) {
		Map<T, Reward> r_map = new HashMap<T, Reward>();
		Set<T> w_keys = wait_select.keySet();
		Iterator<T> w_k_itr = w_keys.iterator();
		while (w_k_itr.hasNext()) {
			T t = w_k_itr.next();
			r_map.put(t, wait_select.get(t).CopySelf());
		}
		ArrayList<Double[]> rs = new ArrayList<Double[]>();
		Collection<Reward> vals = wait_select.values();
		int val_size = vals.size();
		if (val_size > 0) {
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
					for (int i = 0; i < r_num; i++) {
						rs.get(i)[v_idx] = rewards[i];
					}
				}
			}
		}
		CopiedStructures<T> result = new CopiedStructures<T>(r_map, rs);
		return result;
	}

	/**
	 * we assume that the values in Reward could be modified.
	 * 
	 * @param <T>
	 * 
	 * @param wait_select
	 */
	public static <T> Map<T, Reward> ProbabilizeRewards(Map<T, Reward> wait_select) {
		CopiedStructures<T> cs = CopyAndGenerateNecessaryStructures(wait_select);
		for (int i = 0; i < cs.rs.size(); i++) {
			Double[] data = cs.rs.get(i);
			double min = Collections.min(Arrays.asList(data));
			Assert.isTrue(min >= 0);
			double data_sum = sum(data);
			Iterator<Reward> v_itr = cs.r_map.values().iterator();
			while (v_itr.hasNext()) {
				Reward r = v_itr.next();
				double[] rewards = r.GetRewards();
				rewards[i] = data_sum == 0 ? rewards[i] : (rewards[i] / data_sum);
			}
		}
		return cs.r_map;
	}
	
	public static <T> Map<T, Double> ProbabilizeDoubleValues(Map<T, Double> wait_select) {
		Map<T, Double> f_wait_select = new HashMap<T, Double>();
		double data_sum = sum((Double[])wait_select.values().toArray());
		Set<T> w_keys = wait_select.keySet();
		Iterator<T> w_k_itr = w_keys.iterator();
		while (w_k_itr.hasNext()) {
			T t = w_k_itr.next();
			Double t_val = wait_select.get(t);
			f_wait_select.put(t, data_sum == 0 ? t_val : t_val / data_sum);
		}
		return f_wait_select;
	}

	// /**
	// * we assume that the values in Reward could be modified.
	// *
	// * @param wait_select
	// */
	// public static <T> Map<T, Reward> NormalizeRewards(Map<T, Reward> wait_select)
	// {
	// CopiedStructures<T> cs = CopyAndGenerateNecessaryStructures(wait_select);
	// for (int i = 0; i < cs.rs.size(); i++) {
	// Double[] data = cs.rs.get(i);
	// double min = Collections.min(Arrays.asList(data));
	// double max = Collections.max(Arrays.asList(data));
	// double gap = max - min;
	// Iterator<Reward> v_itr = cs.r_map.values().iterator();
	// while (v_itr.hasNext()) {
	// Reward r = v_itr.next();
	// double[] rewards = r.GetRewards();
	// rewards[i] = gap == 0 ? rewards[i] : (rewards[i] - min) / gap;
	// }
	// }
	// return cs.r_map;
	// }

	/**
	 * we assume that the values in Reward could be modified.
	 * 
	 * @param wait_select
	 * @return
	 */
	public static <T> Map<T, Reward> StandardizeRewards(Map<T, Reward> wait_select) {
		CopiedStructures<T> cs = CopyAndGenerateNecessaryStructures(wait_select);
		for (int i = 0; i < cs.rs.size(); i++) {
			Double[] data = cs.rs.get(i);
			double avg = average(data);
			double std = standardDeviation(data);
			Iterator<Reward> v_itr = cs.r_map.values().iterator();
			while (v_itr.hasNext()) {
				Reward r = v_itr.next();
				double[] rewards = r.GetRewards();
				rewards[i] = std == 0 ? rewards[i] : (rewards[i] - avg) / std;
			}
		}
		return cs.r_map;
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
	
	public static double variance(Double[] x) {
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

	public static double standardDeviation(Double[] x) {
		return Math.sqrt(variance(x));
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

	public static double sum(Double[] x) {
		int m = x.length;
		double sum = 0;
		for (int i = 0; i < m; i++) {
			sum += x[i];
		}
		return sum;
	}

	public static double sum(double[] x) {
		int m = x.length;
		double sum = 0;
		for (int i = 0; i < m; i++) {
			sum += x[i];
		}
		return sum;
	}
	
	public static double average(Double[] x) {
		double sum = sum(x);
		double dAve = sum / x.length;
		return dAve;
	}

	/**
	 * average
	 *
	 * @param x
	 * @return average
	 */
	public static double average(double[] x) {
		double sum = sum(x);
		double dAve = sum / x.length;
		return dAve;
	}

	public static double[] probabilize(double[] x) {
		int m = x.length;
		double[] arr = new double[m];
		double sum = sum(x);
		for (int i = 0; i < m; i++) {
			if (sum == 0.0) {
				arr[i] = x[i];
			} else {
				arr[i] = x[i] / sum;
			}
		}
		return arr;
	}

}

class CopiedStructures<T> {

	Map<T, Reward> r_map;
	ArrayList<Double[]> rs;

	public CopiedStructures(Map<T, Reward> r_map, ArrayList<Double[]> rs) {
		this.r_map = r_map;
		this.rs = rs;
	}

}
