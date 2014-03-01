package ch.javasoft.decimal.arithmetic;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import ch.javasoft.decimal.Decimal;

/**
 * Defines the same constants as {@link RoundingMode} with added functionality
 * for each constant to apply such rounding to a {@link Decimal} number.
 * <p>
 * <b>Description copied from {@link RoundingMode}:</b>
 * <p>
 * Specifies a <i>rounding behavior</i> for numerical operations
 * capable of discarding precision. Each rounding mode indicates how
 * the least significant returned digit of a rounded result is to be
 * calculated.  If fewer digits are returned than the digits needed to
 * represent the exact numerical result, the discarded digits will be
 * referred to as the <i>discarded fraction</i> regardless the digits'
 * contribution to the value of the number.  In other words,
 * considered as a numerical value, the discarded fraction could have
 * an absolute value greater than one.
 *
 * <p>Each rounding mode description includes a table listing how
 * different two-digit decimal values would round to a one digit
 * decimal value under the rounding mode in question.  The result
 * column in the tables could be gotten by creating a
 * {@code BigDecimal} number with the specified value, forming a
 * {@link MathContext} object with the proper settings
 * ({@code precision} set to {@code 1}, and the
 * {@code roundingMode} set to the rounding mode in question), and
 * calling {@link BigDecimal#round round} on this number with the
 * proper {@code MathContext}.  A summary table showing the results
 * of these rounding operations for all rounding modes appears below.
 *
 *<p>
 *<table border>
 * <caption><b>Summary of Rounding Operations Under Different Rounding Modes</b></caption>
 * <tr><th></th><th colspan=8>Result of rounding input to one digit with the given
 *                           rounding mode</th>
 * <tr valign=top>
 * <th>Input Number</th>         <th>{@code UP}</th>
 *                                           <th>{@code DOWN}</th>
 *                                                        <th>{@code CEILING}</th>
 *                                                                       <th>{@code FLOOR}</th>
 *                                                                                    <th>{@code HALF_UP}</th>
 *                                                                                                   <th>{@code HALF_DOWN}</th>
 *                                                                                                                    <th>{@code HALF_EVEN}</th>
 *                                                                                                                                     <th>{@code UNNECESSARY}</th>
 *
 * <tr align=right><td>5.5</td>  <td>6</td>  <td>5</td>    <td>6</td>    <td>5</td>  <td>6</td>      <td>5</td>       <td>6</td>       <td>throw {@code ArithmeticException}</td>
 * <tr align=right><td>2.5</td>  <td>3</td>  <td>2</td>    <td>3</td>    <td>2</td>  <td>3</td>      <td>2</td>       <td>2</td>       <td>throw {@code ArithmeticException}</td>
 * <tr align=right><td>1.6</td>  <td>2</td>  <td>1</td>    <td>2</td>    <td>1</td>  <td>2</td>      <td>2</td>       <td>2</td>       <td>throw {@code ArithmeticException}</td>
 * <tr align=right><td>1.1</td>  <td>2</td>  <td>1</td>    <td>2</td>    <td>1</td>  <td>1</td>      <td>1</td>       <td>1</td>       <td>throw {@code ArithmeticException}</td>
 * <tr align=right><td>1.0</td>  <td>1</td>  <td>1</td>    <td>1</td>    <td>1</td>  <td>1</td>      <td>1</td>       <td>1</td>       <td>1</td>
 * <tr align=right><td>-1.0</td> <td>-1</td> <td>-1</td>   <td>-1</td>   <td>-1</td> <td>-1</td>     <td>-1</td>      <td>-1</td>      <td>-1</td>
 * <tr align=right><td>-1.1</td> <td>-2</td> <td>-1</td>   <td>-1</td>   <td>-2</td> <td>-1</td>     <td>-1</td>      <td>-1</td>      <td>throw {@code ArithmeticException}</td>
 * <tr align=right><td>-1.6</td> <td>-2</td> <td>-1</td>   <td>-1</td>   <td>-2</td> <td>-2</td>     <td>-2</td>      <td>-2</td>      <td>throw {@code ArithmeticException}</td>
 * <tr align=right><td>-2.5</td> <td>-3</td> <td>-2</td>   <td>-2</td>   <td>-3</td> <td>-3</td>     <td>-2</td>      <td>-2</td>      <td>throw {@code ArithmeticException}</td>
 * <tr align=right><td>-5.5</td> <td>-6</td> <td>-5</td>   <td>-5</td>   <td>-6</td> <td>-6</td>     <td>-5</td>      <td>-6</td>      <td>throw {@code ArithmeticException}</td>
 *</table>
 */
public enum DecimalRounding {

    /**
     * Rounding mode to round away from zero.  Always increments the
     * digit prior to a non-zero discarded fraction.  Note that this
     * rounding mode never decreases the magnitude of the calculated
     * value.
     *
     *<p>Example:
     *<table border>
     *<tr valign=top><th>Input Number</th>
     *    <th>Input rounded to one digit<br> with {@code UP} rounding
     *<tr align=right><td>5.5</td>  <td>6</td>
     *<tr align=right><td>2.5</td>  <td>3</td>
     *<tr align=right><td>1.6</td>  <td>2</td>
     *<tr align=right><td>1.1</td>  <td>2</td>
     *<tr align=right><td>1.0</td>  <td>1</td>
     *<tr align=right><td>-1.0</td> <td>-1</td>
     *<tr align=right><td>-1.1</td> <td>-2</td>
     *<tr align=right><td>-1.6</td> <td>-2</td>
     *<tr align=right><td>-2.5</td> <td>-3</td>
     *<tr align=right><td>-5.5</td> <td>-6</td>
     *</table>
     */
	UP(RoundingMode.UP) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit > 0 || !zeroAfterFirstTruncatedDigit) {
				return truncatedValue < 0 ? -1 : 1;
			}
			return 0;
		}
	},

    /**
     * Rounding mode to round towards zero.  Never increments the digit
     * prior to a discarded fraction (i.e., truncates).  Note that this
     * rounding mode never increases the magnitude of the calculated value.
     *
     *<p>Example:
     *<table border>
     *<tr valign=top><th>Input Number</th>
     *    <th>Input rounded to one digit<br> with {@code DOWN} rounding
     *<tr align=right><td>5.5</td>  <td>5</td>
     *<tr align=right><td>2.5</td>  <td>2</td>
     *<tr align=right><td>1.6</td>  <td>1</td>
     *<tr align=right><td>1.1</td>  <td>1</td>
     *<tr align=right><td>1.0</td>  <td>1</td>
     *<tr align=right><td>-1.0</td> <td>-1</td>
     *<tr align=right><td>-1.1</td> <td>-1</td>
     *<tr align=right><td>-1.6</td> <td>-1</td>
     *<tr align=right><td>-2.5</td> <td>-2</td>
     *<tr align=right><td>-5.5</td> <td>-5</td>
     *</table>
     */
	DOWN(RoundingMode.DOWN) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			return 0;
		}
	},

    /**
     * Rounding mode to round towards positive infinity.  If the
     * result is positive, behaves as for {@code RoundingMode.UP};
     * if negative, behaves as for {@code RoundingMode.DOWN}.  Note
     * that this rounding mode never decreases the calculated value.
     *
     *<p>Example:
     *<table border>
     *<tr valign=top><th>Input Number</th>
     *    <th>Input rounded to one digit<br> with {@code CEILING} rounding
     *<tr align=right><td>5.5</td>  <td>6</td>
     *<tr align=right><td>2.5</td>  <td>3</td>
     *<tr align=right><td>1.6</td>  <td>2</td>
     *<tr align=right><td>1.1</td>  <td>2</td>
     *<tr align=right><td>1.0</td>  <td>1</td>
     *<tr align=right><td>-1.0</td> <td>-1</td>
     *<tr align=right><td>-1.1</td> <td>-1</td>
     *<tr align=right><td>-1.6</td> <td>-1</td>
     *<tr align=right><td>-2.5</td> <td>-2</td>
     *<tr align=right><td>-5.5</td> <td>-5</td>
     *</table>
     */
	CEILING(RoundingMode.CEILING) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (truncatedValue >= 0) {
				if (firstTruncatedDigit > 0 || !zeroAfterFirstTruncatedDigit) {
					return 1;
				}
			}
			return 0;
		}
	},

    /**
     * Rounding mode to round towards negative infinity.  If the
     * result is positive, behave as for {@code RoundingMode.DOWN};
     * if negative, behave as for {@code RoundingMode.UP}.  Note that
     * this rounding mode never increases the calculated value.
     *
     *<p>Example:
     *<table border>
     *<tr valign=top><th>Input Number</th>
     *    <th>Input rounded to one digit<br> with {@code FLOOR} rounding
     *<tr align=right><td>5.5</td>  <td>5</td>
     *<tr align=right><td>2.5</td>  <td>2</td>
     *<tr align=right><td>1.6</td>  <td>1</td>
     *<tr align=right><td>1.1</td>  <td>1</td>
     *<tr align=right><td>1.0</td>  <td>1</td>
     *<tr align=right><td>-1.0</td> <td>-1</td>
     *<tr align=right><td>-1.1</td> <td>-2</td>
     *<tr align=right><td>-1.6</td> <td>-2</td>
     *<tr align=right><td>-2.5</td> <td>-3</td>
     *<tr align=right><td>-5.5</td> <td>-6</td>
     *</table>
     */
	FLOOR(RoundingMode.FLOOR) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (truncatedValue < 0) {
				if (firstTruncatedDigit > 0 || !zeroAfterFirstTruncatedDigit) {
					return -1;
				}
			}
			return 0;
		}
	},

    /**
     * Rounding mode to round towards {@literal "nearest neighbor"}
     * unless both neighbors are equidistant, in which case round up.
     * Behaves as for {@code RoundingMode.UP} if the discarded
     * fraction is &ge; 0.5; otherwise, behaves as for
     * {@code RoundingMode.DOWN}.  Note that this is the rounding
     * mode commonly taught at school.
     *
     *<p>Example:
     *<table border>
     *<tr valign=top><th>Input Number</th>
     *    <th>Input rounded to one digit<br> with {@code HALF_UP} rounding
     *<tr align=right><td>5.5</td>  <td>6</td>
     *<tr align=right><td>2.5</td>  <td>3</td>
     *<tr align=right><td>1.6</td>  <td>2</td>
     *<tr align=right><td>1.1</td>  <td>1</td>
     *<tr align=right><td>1.0</td>  <td>1</td>
     *<tr align=right><td>-1.0</td> <td>-1</td>
     *<tr align=right><td>-1.1</td> <td>-1</td>
     *<tr align=right><td>-1.6</td> <td>-2</td>
     *<tr align=right><td>-2.5</td> <td>-3</td>
     *<tr align=right><td>-5.5</td> <td>-6</td>
     *</table>
     */
	HALF_UP(RoundingMode.HALF_UP) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit >= 5) {
				return truncatedValue < 0 ? -1 : 1;
			}
			return 0;
		}
	},

    /**
     * Rounding mode to round towards {@literal "nearest neighbor"}
     * unless both neighbors are equidistant, in which case round
     * down.  Behaves as for {@code RoundingMode.UP} if the discarded
     * fraction is &gt; 0.5; otherwise, behaves as for
     * {@code RoundingMode.DOWN}.
     *
     *<p>Example:
     *<table border>
     *<tr valign=top><th>Input Number</th>
     *    <th>Input rounded to one digit<br> with {@code HALF_DOWN} rounding
     *<tr align=right><td>5.5</td>  <td>5</td>
     *<tr align=right><td>2.5</td>  <td>2</td>
     *<tr align=right><td>1.6</td>  <td>2</td>
     *<tr align=right><td>1.1</td>  <td>1</td>
     *<tr align=right><td>1.0</td>  <td>1</td>
     *<tr align=right><td>-1.0</td> <td>-1</td>
     *<tr align=right><td>-1.1</td> <td>-1</td>
     *<tr align=right><td>-1.6</td> <td>-2</td>
     *<tr align=right><td>-2.5</td> <td>-2</td>
     *<tr align=right><td>-5.5</td> <td>-5</td>
     *</table>
     */
	HALF_DOWN(RoundingMode.HALF_DOWN) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit > 5 || (firstTruncatedDigit == 5 && !zeroAfterFirstTruncatedDigit)) {
				return truncatedValue < 0 ? -1 : 1;
			}
			return 0;
		}
	},

    /**
     * Rounding mode to round towards the {@literal "nearest neighbor"}
     * unless both neighbors are equidistant, in which case, round
     * towards the even neighbor.  Behaves as for
     * {@code RoundingMode.HALF_UP} if the digit to the left of the
     * discarded fraction is odd; behaves as for
     * {@code RoundingMode.HALF_DOWN} if it's even.  Note that this
     * is the rounding mode that statistically minimizes cumulative
     * error when applied repeatedly over a sequence of calculations.
     * It is sometimes known as {@literal "Banker's rounding,"} and is
     * chiefly used in the USA.  This rounding mode is analogous to
     * the rounding policy used for {@code float} and {@code double}
     * arithmetic in Java.
     *
     *<p>Example:
     *<table border>
     *<tr valign=top><th>Input Number</th>
     *    <th>Input rounded to one digit<br> with {@code HALF_EVEN} rounding
     *<tr align=right><td>5.5</td>  <td>6</td>
     *<tr align=right><td>2.5</td>  <td>2</td>
     *<tr align=right><td>1.6</td>  <td>2</td>
     *<tr align=right><td>1.1</td>  <td>1</td>
     *<tr align=right><td>1.0</td>  <td>1</td>
     *<tr align=right><td>-1.0</td> <td>-1</td>
     *<tr align=right><td>-1.1</td> <td>-1</td>
     *<tr align=right><td>-1.6</td> <td>-2</td>
     *<tr align=right><td>-2.5</td> <td>-2</td>
     *<tr align=right><td>-5.5</td> <td>-6</td>
     *</table>
     */
	HALF_EVEN(RoundingMode.HALF_EVEN) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit >= 5) {
				if (firstTruncatedDigit > 5 || !zeroAfterFirstTruncatedDigit || ((truncatedValue & 0x1) != 0)) {
					return truncatedValue < 0 ? -1 : 1;
				}
			}
			return 0;
		}
	},

    /**
     * Rounding mode to assert that the requested operation has an exact
     * result, hence no rounding is necessary.  If this rounding mode is
     * specified on an operation that yields an inexact result, an
     * {@code ArithmeticException} is thrown.
     *<p>Example:
     *<table border>
     *<tr valign=top><th>Input Number</th>
     *    <th>Input rounded to one digit<br> with {@code UNNECESSARY} rounding
     *<tr align=right><td>5.5</td>  <td>throw {@code ArithmeticException}</td>
     *<tr align=right><td>2.5</td>  <td>throw {@code ArithmeticException}</td>
     *<tr align=right><td>1.6</td>  <td>throw {@code ArithmeticException}</td>
     *<tr align=right><td>1.1</td>  <td>throw {@code ArithmeticException}</td>
     *<tr align=right><td>1.0</td>  <td>1</td>
     *<tr align=right><td>-1.0</td> <td>-1</td>
     *<tr align=right><td>-1.1</td> <td>throw {@code ArithmeticException}</td>
     *<tr align=right><td>-1.6</td> <td>throw {@code ArithmeticException}</td>
     *<tr align=right><td>-2.5</td> <td>throw {@code ArithmeticException}</td>
     *<tr align=right><td>-5.5</td> <td>throw {@code ArithmeticException}</td>
     *</table>
     */
	UNNECESSARY(RoundingMode.UNNECESSARY) {
		@Override
		public int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit) {
			if (firstTruncatedDigit != 0 || !zeroAfterFirstTruncatedDigit) {
				throw new ArithmeticException("rounding necessary for: " + truncatedValue + "." + firstTruncatedDigit + (zeroAfterFirstTruncatedDigit ? "" : "..."));
			}
			return 0;
		}
	};

	/**
	 * Constructor with {@link RoundingMode}.
	 * 
	 * @param roundingMode
	 *            the rounding mode corresponding to this decimal rounding
	 */
	private DecimalRounding(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	private final RoundingMode roundingMode;
	
	/**
	 * Returns the {@link RoundingMode} associated with this decimal rounding
	 * constant.
	 * 
	 * @return the corresponding rounding mode
	 */
	public RoundingMode getRoundingMode() {
		return roundingMode;
	}
	
	/**
	 * Returns the rounding increment appropriate for this decimal rounding. The
	 * returned value is one of -1, 0 or 1.
	 * 
	 * @param truncatedValue
	 *            the truncated result before rounding is applied
	 * @param firstTruncatedDigit
	 *            the first truncated digit, must be in {@code [0, 1, ..., 9]}
	 * @param zeroAfterFirstTruncatedDigit
	 *            true if all truncated digits after the first truncated digit
	 *            are zero, and false otherwise
	 * @return the value to add to {@code truncatedValue} to get the rounded
	 *         result, one of -1, 0 or 1
	 */
	abstract public int calculateRoundingIncrement(long truncatedValue, int firstTruncatedDigit, boolean zeroAfterFirstTruncatedDigit);
	
	private static final DecimalRounding[] VALUES_BY_ROUNDING_MODE_ORDINAL = sortByRoundingModeOrdinal();
	
	/**
	 * Returns the decimal rounding constant for the given rounding mode.
	 * 
	 * @param roundingMode
	 *            the rounding mode
	 * @return the constant corresponding to the given rounding mode
	 */
	public static DecimalRounding valueOf(RoundingMode roundingMode) {
		return VALUES_BY_ROUNDING_MODE_ORDINAL[roundingMode.ordinal()];
	}

	private static DecimalRounding[] sortByRoundingModeOrdinal() {
		final RoundingMode[] modes = RoundingMode.values();
		final DecimalRounding[] sorted = new DecimalRounding[modes.length];
		for (int i = 0; i < modes.length; i++) {
			sorted[modes[i].ordinal()] = valueOf(modes[i].name());
		}
		return sorted;
	}
}
