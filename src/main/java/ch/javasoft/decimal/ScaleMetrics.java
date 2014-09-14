package ch.javasoft.decimal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import ch.javasoft.decimal.arithmetic.CheckedLongTruncatingArithmetics;
import ch.javasoft.decimal.arithmetic.CheckedScaledTruncatingArithmetics;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.DecimalRounding;
import ch.javasoft.decimal.arithmetic.UncheckedLongRoundingArithmetics;
import ch.javasoft.decimal.arithmetic.UncheckedLongTruncatingArithmetics;
import ch.javasoft.decimal.arithmetic.UncheckedScaledRoundingArithmetics;
import ch.javasoft.decimal.arithmetic.UncheckedScaledTruncatingArithmetics;

/**
 * <tt>ScaleMetrics</tt> is associated with {@link Decimal} numbers and
 * represents the factor applied to the {@code long} value underlying a
 * {@code Decimal}. Scale stands for the fixed number of fraction digits of a
 * {@code Decimal}.
 * <p>
 * The <tt>Scale</tt> class contains a number of subclasses used by different
 * decimal types. With <tt>Scale</tt> subclasses, it is possible to distinguish
 * different decimal types and we can ensure that only decimals of the same
 * scale can directly operate with each other.
 */
abstract public class ScaleMetrics {

	/**
	 * This mask is used to obtain the value of an int as if it were unsigned.
	 */
	private final static long LONG_MASK = 0xffffffffL;

	/**
	 * Scale class for decimals with 0 {@link #getScale() fraction digits} (aka
	 * as integers) and {@link #getScaleFactor() scale factor} 1.
	 */
	public static final class Scale0f extends ScaleMetrics {
		public static final Scale0f INSTANCE = new Scale0f();

		@Override
		protected EnumMap<RoundingMode, DecimalArithmetics> initArithmetics() {
			final EnumMap<RoundingMode, DecimalArithmetics> map = new EnumMap<RoundingMode, DecimalArithmetics>(RoundingMode.class);
			for (final DecimalRounding dr : DecimalRounding.VALUES) {
				final RoundingMode roundingMode = dr.getRoundingMode();
				if (roundingMode == RoundingMode.DOWN) {
					map.put(roundingMode, UncheckedLongTruncatingArithmetics.INSTANCE);
				} else {
					map.put(roundingMode, new UncheckedLongRoundingArithmetics(dr));
				}
			}
			return map;
		}

		@Override
		protected EnumMap<RoundingMode, DecimalArithmetics> initCheckedArithmetics() {
			final EnumMap<RoundingMode, DecimalArithmetics> map = new EnumMap<RoundingMode, DecimalArithmetics>(RoundingMode.class);
			for (final DecimalRounding dr : DecimalRounding.VALUES) {
				final RoundingMode roundingMode = dr.getRoundingMode();
				if (roundingMode == RoundingMode.DOWN) {
					map.put(roundingMode, CheckedLongTruncatingArithmetics.INSTANCE);
				} else {
					//FIXME add when implemented
					//					map.put(roundingMode, new CheckedLongRoundingArithmetics(dr));
				}
			}
			return map;
		}

		@Override
		public int getScale() {
			return 0;
		}

		@Override
		public long getScaleFactor() {
			return 1;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return factor & LONG_MASK;
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return 0;
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return 0;
		}

		@Override
		public Decimal0f createImmutable(long unscaled) {
			return Decimal0f.valueOfUnscaled(unscaled);
		}

		@Override
		public MutableDecimal0f createMutable(long unscaled) {
			return MutableDecimal0f.unscaled(unscaled);
		}
	}

	/**
	 * Scale class for decimals with 1 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10.
	 */
	public static final class Scale1f extends ScaleMetrics {
		public static final Scale1f INSTANCE = new Scale1f();

		@Override
		public int getScale() {
			return 1;
		}

		@Override
		public long getScaleFactor() {
			return 10;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 10;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 10;
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return 0;
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 10;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 10;
		}
	}

	/**
	 * Scale class for decimals with 2 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100.
	 */
	public static final class Scale2f extends ScaleMetrics {
		public static final Scale2f INSTANCE = new Scale2f();

		@Override
		public int getScale() {
			return 2;
		}

		@Override
		public long getScaleFactor() {
			return 100;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 100;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 100;
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return 0;
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 100;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 100;
		}
	}

	/**
	 * Scale class for decimals with 3 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000.
	 */
	public static final class Scale3f extends ScaleMetrics {
		public static final Scale3f INSTANCE = new Scale3f();

		@Override
		public int getScale() {
			return 3;
		}

		@Override
		public long getScaleFactor() {
			return 1000;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 1000;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 1000;
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return 0;
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 1000;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 1000;
		}
	}

	/**
	 * Scale class for decimals with 4 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10,000.
	 */
	public static final class Scale4f extends ScaleMetrics {
		public static final Scale4f INSTANCE = new Scale4f();

		@Override
		public int getScale() {
			return 4;
		}

		@Override
		public long getScaleFactor() {
			return 10000;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 10000;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 10000;
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return 0;
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 10000;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 10000;
		}
	}

	/**
	 * Scale class for decimals with 5 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100,000.
	 */
	public static final class Scale5f extends ScaleMetrics {
		public static final Scale5f INSTANCE = new Scale5f();

		@Override
		public int getScale() {
			return 5;
		}

		@Override
		public long getScaleFactor() {
			return 100000;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 100000;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 100000;
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return 0;
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 100000;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 100000;
		}
	}

	/**
	 * Scale class for decimals with 6 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000,000.
	 */
	public static final class Scale6f extends ScaleMetrics {
		public static final Scale6f INSTANCE = new Scale6f();

		@Override
		public int getScale() {
			return 6;
		}

		@Override
		public long getScaleFactor() {
			return 1000000;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 1000000;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 1000000;
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return 0;
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 1000000;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 1000000;
		}

		@Override
		public Decimal6f createImmutable(long unscaled) {
			return Decimal6f.valueOfUnscaled(unscaled);
		}

		@Override
		public MutableDecimal6f createMutable(long unscaled) {
			return MutableDecimal6f.unscaled(unscaled);
		}
	}

	/**
	 * Scale class for decimals with 7 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10,000,000.
	 */
	public static final class Scale7f extends ScaleMetrics {
		public static final Scale7f INSTANCE = new Scale7f();

		@Override
		public int getScale() {
			return 7;
		}

		@Override
		public long getScaleFactor() {
			return 10000000;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 10000000;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 10000000;
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return 0;
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 10000000;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 10000000;
		}
	}

	/**
	 * Scale class for decimals with 8 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100,000,000.
	 */
	public static final class Scale8f extends ScaleMetrics {
		public static final Scale8f INSTANCE = new Scale8f();

		@Override
		public int getScale() {
			return 8;
		}

		@Override
		public long getScaleFactor() {
			return 100000000;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 100000000;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 100000000;
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return 0;
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 100000000;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 100000000;
		}
	}

	/**
	 * Scale class for decimals with 9 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000,000,000.
	 */
	public static final class Scale9f extends ScaleMetrics {
		public static final Scale9f INSTANCE = new Scale9f();

		@Override
		public int getScale() {
			return 9;
		}

		@Override
		public long getScaleFactor() {
			return 1000000000;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 1000000000;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 1000000000;
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return 0;
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 1000000000;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 1000000000;
		}
	}

	/**
	 * Scale class for decimals with 10 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10,000,000,000.
	 */
	public static final class Scale10f extends ScaleMetrics {
		public static final Scale10f INSTANCE = new Scale10f();

		@Override
		public int getScale() {
			return 10;
		}

		@Override
		public long getScaleFactor() {
			return 10000000000L;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 10000000000L;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 1410065408;//(scaleFactor & LONG_MASK)
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 2;//(scaleFactor >>> 32)
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 10000000000L;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 10000000000L;
		}
	}

	/**
	 * Scale class for decimals with 11 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100,000,000,000.
	 */
	public static final class Scale11f extends ScaleMetrics {
		public static final Scale11f INSTANCE = new Scale11f();

		@Override
		public int getScale() {
			return 11;
		}

		@Override
		public long getScaleFactor() {
			return 100000000000L;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 100000000000L;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 1215752192;//(scaleFactor & LONG_MASK)
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 23;//(scaleFactor >>> 32)
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 100000000000L;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 100000000000L;
		}
	}

	/**
	 * Scale class for decimals with 12 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000,000,000,000.
	 */
	public static final class Scale12f extends ScaleMetrics {
		public static final Scale12f INSTANCE = new Scale12f();

		@Override
		public int getScale() {
			return 12;
		}

		@Override
		public long getScaleFactor() {
			return 1000000000000L;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 1000000000000L;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 3567587328L;//(scaleFactor & LONG_MASK)
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 232;//(scaleFactor >>> 32)
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 1000000000000L;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 1000000000000L;
		}
	}

	/**
	 * Scale class for decimals with 13 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10,000,000,000,000.
	 */
	public static final class Scale13f extends ScaleMetrics {
		public static final Scale13f INSTANCE = new Scale13f();

		@Override
		public int getScale() {
			return 13;
		}

		@Override
		public long getScaleFactor() {
			return 10000000000000L;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 10000000000000L;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 1316134912;//(scaleFactor & LONG_MASK)
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 2328;//(scaleFactor >>> 32)
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 10000000000000L;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 10000000000000L;
		}
	}

	/**
	 * Scale class for decimals with 14 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100,000,000,000,000.
	 */
	public static final class Scale14f extends ScaleMetrics {
		public static final Scale14f INSTANCE = new Scale14f();

		@Override
		public int getScale() {
			return 14;
		}

		@Override
		public long getScaleFactor() {
			return 100000000000000L;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 100000000000000L;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 276447232;//(scaleFactor & LONG_MASK)
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 23283;//(scaleFactor >>> 32)
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 100000000000000L;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 100000000000000L;
		}
	}

	/**
	 * Scale class for decimals with 15 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000,000,000,000,000.
	 */
	public static final class Scale15f extends ScaleMetrics {
		public static final Scale15f INSTANCE = new Scale15f();

		@Override
		public int getScale() {
			return 15;
		}

		@Override
		public long getScaleFactor() {
			return 1000000000000000L;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 1000000000000000L;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 2764472320L;//(scaleFactor & LONG_MASK)
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 232830;//(scaleFactor >>> 32)
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 1000000000000000L;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 1000000000000000L;
		}
	}

	/**
	 * Scale class for decimals with 16 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 10,000,000,000,000,000.
	 */
	public static final class Scale16f extends ScaleMetrics {
		public static final Scale16f INSTANCE = new Scale16f();

		@Override
		public int getScale() {
			return 16;
		}

		@Override
		public long getScaleFactor() {
			return 10000000000000000L;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 10000000000000000L;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 1874919424;//(scaleFactor & LONG_MASK)
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 2328306;//(scaleFactor >>> 32)
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 10000000000000000L;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 10000000000000000L;
		}
	}

	/**
	 * Scale class for decimals with 17 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 100,000,000,000,000,000.
	 */
	public static final class Scale17f extends ScaleMetrics {
		public static final Scale17f INSTANCE = new Scale17f();

		@Override
		public int getScale() {
			return 17;
		}

		@Override
		public long getScaleFactor() {
			return 100000000000000000L;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 100000000000000000L;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 1569325056;//(scaleFactor & LONG_MASK)
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 23283064;//(scaleFactor >>> 32)
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 100000000000000000L;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 100000000000000000L;
		}

		@Override
		public Decimal17f createImmutable(long unscaled) {
			return Decimal17f.valueOfUnscaled(unscaled);
		}

		@Override
		public MutableDecimal17f createMutable(long unscaled) {
			return MutableDecimal17f.unscaled(unscaled);
		}
	}

	/**
	 * Scale class for decimals with 18 {@link #getScale() fraction digit} and
	 * {@link #getScaleFactor() scale factor} 1,000,000,000,000,000,000.
	 */
	public static final class Scale18f extends ScaleMetrics {
		public static final Scale18f INSTANCE = new Scale18f();

		@Override
		public int getScale() {
			return 18;
		}

		@Override
		public long getScaleFactor() {
			return 1000000000000000000L;
		}

		@Override
		public long multiplyByScaleFactor(long factor) {
			return factor * 1000000000000000000L;
		}

		@Override
		public long mulloByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 2808348672L;//(scaleFactor & LONG_MASK)
		}

		@Override
		public long mulhiByScaleFactor(int factor) {
			return (factor & LONG_MASK) * 232830643;//(scaleFactor >>> 32)
		}

		@Override
		public long divideByScaleFactor(long dividend) {
			return dividend / 1000000000000000000L;
		}

		@Override
		public long moduloByScaleFactor(long dividend) {
			return dividend % 1000000000000000000L;
		}
	}

	/**
	 * All scale metric constants in an immutable ordered list:
	 * <p>
	 * {@code VALUES=[Scale0f.INSTANCE, Scale1f.INSTANCE, ..., Scale18f.INSTANCE]}
	 */
	public static final List<ScaleMetrics> VALUES = Collections.unmodifiableList(Arrays.asList(Scale0f.INSTANCE, Scale1f.INSTANCE, Scale2f.INSTANCE, Scale3f.INSTANCE, Scale4f.INSTANCE, Scale5f.INSTANCE, Scale6f.INSTANCE, Scale7f.INSTANCE, Scale8f.INSTANCE, Scale9f.INSTANCE, Scale10f.INSTANCE, Scale11f.INSTANCE, Scale12f.INSTANCE, Scale13f.INSTANCE, Scale14f.INSTANCE, Scale15f.INSTANCE, Scale16f.INSTANCE, Scale17f.INSTANCE, Scale18f.INSTANCE));

	private static final long[] SCALE_FACTORS = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L };

	private final long maxIntegerValue = divideByScaleFactor(Long.MAX_VALUE);
	private final long minIntegerValue = divideByScaleFactor(Long.MIN_VALUE);
	private final BigInteger biScaleFactor = BigInteger.valueOf(getScaleFactor());
	private final BigDecimal bdScaleFactor = new BigDecimal(biScaleFactor);

	private final EnumMap<RoundingMode, DecimalArithmetics> roundingModeToArithmetics = initArithmetics();
	private final EnumMap<RoundingMode, DecimalArithmetics> roundingModeToCheckedArithmetics = initCheckedArithmetics();

	/**
	 * Returns the {@code ScaleMetrics} constant based on a given scale
	 * 
	 * @param scale
	 *            the scale value also known as {@link #getScale() fraction
	 *            digits}; must be in {@code [0,18]} both ends inclusive
	 * @return the scale metrics constant corresponding to {@code scale}
	 * @throws IllegalArgumentException
	 *             if scale is not in {@code [0, 18]}
	 */
	public static ScaleMetrics valueOf(int scale) {
		if (0 <= scale && scale <= 18) {
			return VALUES.get(scale);
		}
		throw new IllegalArgumentException("illegal scale, must be in [0,18] but was: " + scale);
	}

	/**
	 * Initialises the arithmetics map. {@link Scale0f} overrides this method.
	 * 
	 * @return the rounding mode to arithmetics map
	 */
	protected EnumMap<RoundingMode, DecimalArithmetics> initArithmetics() {
		final EnumMap<RoundingMode, DecimalArithmetics> map = new EnumMap<RoundingMode, DecimalArithmetics>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, new UncheckedScaledTruncatingArithmetics(this));
			} else {
				map.put(roundingMode, new UncheckedScaledRoundingArithmetics(this, dr));
			}
		}
		return map;
	}

	/**
	 * Initialises the checked arithmetics map. {@link Scale0f} overrides this
	 * method.
	 * 
	 * @return the rounding mode to checked arithmetics map
	 * @see OverflowMode#CHECKED
	 */
	protected EnumMap<RoundingMode, DecimalArithmetics> initCheckedArithmetics() {
		final EnumMap<RoundingMode, DecimalArithmetics> map = new EnumMap<RoundingMode, DecimalArithmetics>(RoundingMode.class);
		for (final DecimalRounding dr : DecimalRounding.VALUES) {
			final RoundingMode roundingMode = dr.getRoundingMode();
			if (roundingMode == RoundingMode.DOWN) {
				map.put(roundingMode, new CheckedScaledTruncatingArithmetics(this));
			} else {
				//FIXME add when implemented
				//				map.put(roundingMode, new CheckedScaledRoundingArithmetics(this, dr));
			}
		}
		return map;
	}

	/**
	 * Returns the {@code ScaleMetrics} constant that matches the given
	 * {@code scaleFactor} if any and null otherwise.
	 * 
	 * @param scaleFactor
	 *            the scale factor to find
	 * @return the scale metrics constant with {@code scaleFactor} equal to
	 *         {@link #getScaleFactor()} if it exists and null otherwise
	 * @see #getScaleFactor()
	 */
	public static ScaleMetrics findByScaleFactor(long scaleFactor) {
		final int index = Arrays.binarySearch(SCALE_FACTORS, scaleFactor);
		return index < 0 ? null : VALUES.get(index);
	}

	/**
	 * Returns the scale, the number of fraction digits to the right of the
	 * decimal point of a {@link Decimal} value.
	 * 
	 * @return the scale also known as number of fraction digits
	 */
	abstract public int getScale();

	/**
	 * Returns the scale factor, which is 10<sup>f</sup> where {@code f} stands
	 * for the {@link #getScale() scale}.
	 * 
	 * @return the scale factor
	 */
	abstract public long getScaleFactor();

	/**
	 * Returns the {@link #getScaleFactor() scale factor} as a
	 * {@link BigInteger} value.
	 * 
	 * @return the scale factor as big integer
	 */
	public BigInteger getScaleFactorAsBigInteger() {
		return biScaleFactor;
	}

	/**
	 * Returns the {@link #getScaleFactor() scale factor} as a
	 * {@link BigDecimal} value.
	 * 
	 * @return the scale factor as big decimal
	 */
	public BigDecimal getScaleFactorAsBigDecimal() {
		return bdScaleFactor;
	}

	/**
	 * Returns the largest integer value that can be represented using this
	 * scale.
	 * 
	 * @return {@code Long.MAX_VALUE / scaleFactor}
	 */
	public long getMaxIntegerValue() {
		return maxIntegerValue;
	}

	/**
	 * Returns the smallest integer value that can be represented using this
	 * scale.
	 * 
	 * @return {@code Long.MIN_VALUE / scaleFactor}
	 */
	public long getMinIntegerValue() {
		return minIntegerValue;
	}

	/**
	 * Returns {@code factor*scaleFactor}.
	 * 
	 * @param factor
	 *            the factor
	 * @return {@code factor*scaleFactor}
	 */
	abstract public long multiplyByScaleFactor(long factor);

	/**
	 * Returns {@code factor*scaleFactor}, checking for lost information. If the
	 * result is out of the range of the {@code long} type, then an
	 * {@code ArithmeticException} is thrown.
	 * 
	 * @param factor
	 *            the factor
	 * @return {@code factor*scaleFactor}
	 * @throws ArithmeticException
	 *             if an overflow occurs
	 */
	public long multiplyByScaleFactorExact(long factor) {
		final long scaleFactor = getScaleFactor();
		final int leadingZeros = Long.numberOfLeadingZeros(factor) + Long.numberOfLeadingZeros(~factor) + Long.numberOfLeadingZeros(scaleFactor);
		final long result = multiplyByScaleFactor(factor);
		if (leadingZeros > Long.SIZE + 1) {
			return result;
		}
		if (leadingZeros < Long.SIZE | divideByScaleFactor(result) != factor) {
			throw new ArithmeticException("overflow: " + factor + " * " + scaleFactor + " = " + result);
		}
		return result;
	}

	/**
	 * Returns {@code factor*low32(scaleFactor)} where low32 refers to the low
	 * 32 bits of the factor.
	 * 
	 * @param factor
	 *            the factor
	 * @return {@code factor*low32(scaleFactor)}
	 */
	abstract public long mulloByScaleFactor(int factor);

	/**
	 * Returns {@code factor*high32(scaleFactor)} where high32 refers to the
	 * high 32 bits of the factor.
	 * 
	 * @param factor
	 *            the factor
	 * @return {@code factor*high32(scaleFactor)}
	 */
	abstract public long mulhiByScaleFactor(int factor);

	/**
	 * Returns {@code dividend/scaleFactor}.
	 * 
	 * @param dividend
	 *            the dividend
	 * @return {@code dividend/scaleFactor}
	 */
	abstract public long divideByScaleFactor(long dividend);

	/**
	 * Returns {@code dividend % scaleFactor} also known as reminder.
	 * 
	 * @param dividend
	 *            the dividend
	 * @return {@code dividend % scaleFactor}
	 */
	abstract public long moduloByScaleFactor(long dividend);

	/**
	 * Creates and returns an immutable value.
	 *
	 * @param unscaled
	 *            the unscaled long value
	 * @return an immutable value.
	 */
	public AbstractImmutableDecimal<?, ?> createImmutable(long unscaled) {
		// FIXME impl
		throw new RuntimeException("not implemented for " + this);
	}

	/**
	 * Creates and returns a mutable value.
	 *
	 * @param unscaled
	 *            the unscaled long value
	 * @return an mutable value.
	 */
	public AbstractMutableDecimal<?, ?> createMutable(long unscaled) {
		// FIXME impl
		throw new RuntimeException("not implemented for " + this);
	}

	/**
	 * Returns the default arithmetics for this scale.
	 * 
	 * @return default arithmetics for this scale
	 */
	public DecimalArithmetics getDefaultArithmetics() {
		return getArithmetics(RoundingMode.DOWN);
	}

	/**
	 * Returns the truncating arithmetics for this scale that performs all
	 * operations without rounding.
	 * 
	 * @return truncating arithmetics for this scale
	 * @see RoundingMode#DOWN
	 */
	public DecimalArithmetics getTruncatingArithmetics() {
		return getArithmetics(RoundingMode.DOWN);
	}

	/**
	 * Returns the arithmetics for this scale that performs all operations with
	 * the specified {@code roundingMode}.
	 *
	 * @param roundingMode
	 *            the rounding mode used by the returned arithmetics
	 * @return arithmetics for this scale with specified rounding mode
	 */
	public DecimalArithmetics getArithmetics(RoundingMode roundingMode) {
		return roundingModeToArithmetics.get(roundingMode);
	}

	/**
	 * Returns the checked arithmetics for this scale that performs all
	 * operations with the specified {@code roundingMode}.
	 *
	 * @param roundingMode
	 *            the rounding mode used by the returned arithmetics
	 * @return checked arithmetics for this scale with specified rounding mode
	 * @see OverflowMode#CHECKED
	 */
	public DecimalArithmetics getCheckedArithmetics(RoundingMode roundingMode) {
		return roundingModeToCheckedArithmetics.get(roundingMode);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
