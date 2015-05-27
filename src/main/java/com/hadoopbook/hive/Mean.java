package com.hadoopbook.hive;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;

@SuppressWarnings("deprecation")
public class Mean extends UDAF {

	public static class MeanDoubleUDAFEvaluator implements UDAFEvaluator {
		public static class PartialResult {
			double sum;
			long count;
		}

		private PartialResult partial;

		public void init() {
			System.err.printf("%s %s\n", hashCode(), "init");
			partial = null;
		}

		public boolean iterate(DoubleWritable value) {
			System.err.printf("%s %s: %s\n", hashCode(), "iterate", value);
			if (value == null) {
				return true;
			}
			if (partial == null) {
				partial = new PartialResult();
			}
			partial.sum += value.get();
			partial.count++;
			return true;
		}

		public PartialResult terminatePartial() {
			System.err.printf("%s %s\n", hashCode(), "terminatePartial");
			return partial;
		}

		public boolean merge(PartialResult other) {
			System.err.printf("%s %s %s\n", hashCode(), "merge", other);
			if (other == null) {
				return true;
			}
			if (partial == null) {
				partial = new PartialResult();
			}
			partial.sum += other.sum;
			partial.count += other.count;
			return true;
		}

		public DoubleWritable terminate() {
			System.err.printf("%s %s\n", hashCode(), "terminate");
			if (partial == null) {
				return null;
			}
			System.err.printf("%s %s: %s/%s\n", hashCode(), "terminate",
					partial.sum, partial.count);
			return new DoubleWritable(partial.sum / partial.count);
		}
	}
}
