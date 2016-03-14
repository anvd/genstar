package ummisco.genstar.util;

import java.util.Arrays;

public class MultiDimensionalArray {

	private long[] size;

	private double[] values;
	
	private final int length;
	
	
	public MultiDimensionalArray(final long... size) {
		if (size.length <= 2) { throw new IllegalArgumentException("array must be at least 2d"); }
		for (int i = size.length - 1; i != -1; i--) {
			if (size[i] < 1) { throw new IllegalArgumentException("coordinates must be positive"); }
		}
		
		this.size = Utils.copyCoordinates(size);
		this.length = (int) Utils.productCoordinates(size);
		this.values = new double[length];
	}
	
	public MultiDimensionalArray(final double[] values, final long... size) {
		if (size.length <= 2) { throw new IllegalArgumentException("array must be at least 2d"); }
		for (int i = size.length - 1; i != -1; i--) {
			if (size[i] < 1) { throw new IllegalArgumentException("coordinates must be positive"); }
		}
		
		this.size = Utils.copyCoordinates(size);
		this.length = (int) Utils.productCoordinates(size);
		this.values = values;
	}
	
	public final double getDouble(long... pos) {
		return values[(int) Utils.pos2IndexRowMajor(size, pos)];
	}

	public final void setDouble(double value, long... pos) {
		values[(int) Utils.pos2IndexRowMajor(size, pos)] = value;
	}
	
	
	private static class Utils {

		public static final long[] copyCoordinates(long... c) {
			return Arrays.copyOf(c, c.length);
		}
		
		public static final long productCoordinates(final long... c) {
			long product = 1;
			for (int i = c.length - 1; i != -1; i--) {
				product *= c[i];
			}
			return product;
		}
		
		public static final long pos2IndexRowMajor(long[] size, long[] pos) {
			long sum = 0;
			long prod = 1;
			final int length = pos.length;
			for (int k = length - 1; k >= 0; k--) {
				sum += prod * pos[k];
				prod *= size[k];
			}
			return sum;
		}
	}
}
