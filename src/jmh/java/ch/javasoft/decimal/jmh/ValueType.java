package ch.javasoft.decimal.jmh;

import java.util.Random;

/**
 * Value types.
 */
public enum ValueType {
	/** 32 bit integer value*/ 
	Int {
		@Override
		public long random(SignType signType) {
			long value = signType == SignType.ALL ? RND.nextInt() : RND.nextInt(Integer.MAX_VALUE);
			if (signType == SignType.NON_ZERO) {
				while (value == 0) {
					value = signType == SignType.ALL ? RND.nextInt() : RND.nextInt(Integer.MAX_VALUE);
				}
			}
			return value;
		}
	},
	/** 64 bit integer value*/ 
	Long {
		@Override
		public long random(SignType signType) {
			long val = RND.nextLong();
			if (signType != SignType.ALL) {
				while ((val <= 0 && signType == SignType.POSITIVE) || (val == 0 && signType == SignType.NON_ZERO)) {
					val = RND.nextLong();
				}
			}
			return val;
		}
	};

	private static final Random RND = new Random();

	abstract public long random(SignType signType);
}