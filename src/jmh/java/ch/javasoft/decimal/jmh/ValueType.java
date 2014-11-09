package ch.javasoft.decimal.jmh;

import java.util.Random;

/**
 * Value types.
 */
public enum ValueType {
	/** 8 bit integer value*/ 
	Byte {
		@Override
		public long random(SignType signType) {
			long value = signType == SignType.ALL ? (byte)RND.nextInt() : RND.nextInt(java.lang.Byte.MAX_VALUE);
			if (signType == SignType.NON_ZERO) {
				while (value == 0) {
					value = signType == SignType.ALL ? (byte)RND.nextInt() : RND.nextInt(java.lang.Byte.MAX_VALUE);
				}
			}
			return value;
		}
	},
	/** 16 bit integer value*/ 
	Short {
		@Override
		public long random(SignType signType) {
			long value = signType == SignType.ALL ? (short)RND.nextInt() : RND.nextInt(java.lang.Short.MAX_VALUE);
			if (signType == SignType.NON_ZERO) {
				while (value == 0) {
					value = signType == SignType.ALL ? (short)RND.nextInt() : RND.nextInt(java.lang.Short.MAX_VALUE);
				}
			}
			return value;
		}
	},
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