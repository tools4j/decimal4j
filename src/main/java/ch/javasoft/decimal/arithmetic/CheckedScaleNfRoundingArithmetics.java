package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.DecimalRounding;
import ch.javasoft.decimal.truncate.OverflowMode;

import com.google.common.math.DoubleMath;

public class CheckedScaleNfRoundingArithmetics extends AbstractCheckedScaleNfArithmetics {

	private final DecimalRounding rounding;

	/**
	 * Constructor for decimal arithmetics with given scale, rounding mode and
	 * {@link OverflowMode#CHECKED} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal arithmetics
	 * @param roundingMode
	 *            the rounding mode to use for all decimal arithmetics
	 */
	public CheckedScaleNfRoundingArithmetics(ScaleMetrics scaleMetrics, RoundingMode roundingMode) {
		this(scaleMetrics, DecimalRounding.valueOf(roundingMode));
	}

	/**
	 * Constructor for decimal arithmetics with given scale, rounding mode and
	 * {@link OverflowMode#CHECKED} overflow mode.
	 * 
	 * @param scaleMetrics
	 *            the scale metrics for this decimal arithmetics
	 * @param rounding
	 *            the rounding mode to use for all decimal arithmetics
	 */
	public CheckedScaleNfRoundingArithmetics(ScaleMetrics scaleMetrics, DecimalRounding rounding) {
		super(scaleMetrics, rounding);
		this.rounding = rounding;
	}
	
	@Override
	public RoundingMode getRoundingMode() {
		return rounding.getRoundingMode();
	}

	@Override
	public long multiply(long uDecimal1, long uDecimal2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long multiplyByPowerOf10(long uDecimal, int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long divide(long uDecimalDividend, long uDecimalDivisor) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long divideByPowerOf10(long uDecimal, int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long square(long uDecimal) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long sqrt(long uDecimal) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long pow(long uDecimalBase, int exponent) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long shiftLeft(long uDecimal, int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long shiftRight(long uDecimal, int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long round(long uDecimal, int precision) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long fromDouble(double value) {
		// FIXME should be in a helper like checkExtremalDoubleValue(value)
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			throw new NumberFormatException("cannot convert double to long: " + value);
		}

		return DoubleMath.roundToLong(value, getRoundingMode());
	}

}
