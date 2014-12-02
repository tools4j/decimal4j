package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.JDKSupport;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Base class for unit tests with an unscaled decimal operand.
 */
abstract public class AbstractUnscaledTest extends Abstract1DecimalArg1LongArgToDecimalResultTest {
	
	protected final int scale;

	public AbstractUnscaledTest(int scale, DecimalArithmetics arithmetics) {
		super(arithmetics);
		this.scale = scale;
	}

	@Parameters(name = "{index}: {0}, {1}, scale={2}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			for (final TruncationPolicy tp : POLICIES) {
				final DecimalArithmetics arith = s.getArithmetics(tp);
				for (int scale : getScales(s.getScale())) {
					data.add(new Object[] {s, tp, scale, arith});
				}
			}
		}
		return data;
	}
	
	private static Set<Integer> getScales(int scale) {
		final Set<Integer> vals = new TreeSet<Integer>();
		switch (TEST_CASES) {
		case TINY:
			vals.addAll(Arrays.asList(-1, 0, scale-1, scale, scale+1, 18, 19));
			break;
		case SMALL:
			vals.addAll(Arrays.asList(-1, 0, 1, scale-1, scale, scale+1, 17, 18, 19));
			break;
		case STANDARD:
			vals.addAll(Arrays.asList(-10, -1, 0, 1, scale-1, scale, scale+1, 17, 18, 19, 30));
			break;
		case ALL:
			vals.addAll(Arrays.asList(-100, -20, -10, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 25, 30, 100));
			break;
		default:
			throw new RuntimeException("illegal test cases: " + TEST_CASES);
		}
		return vals;
	}

	@Override
	protected long randomLongOperand() {
		return rnd.nextBoolean() ? rnd.nextLong() : rnd.nextInt();
	}
	
	@Override
	protected long[] getSpecialLongOperands() {
		return getSpecialValues(getScaleMetrics());
	}
	
	@Override
	protected int getRandomTestCount() {
		return 1000;
	}
	
	protected BigDecimal toBigDecimal(long unscaled) {
		BigDecimal other = BigDecimal.valueOf(unscaled, scale);
		if (scale != getScale()) {
			other = other.setScale(getScale(), getRoundingMode());
			if (isUnchecked()) {
				other = BigDecimal.valueOf(other.unscaledValue().longValue(), getScale());
			} else {
				//check for overflow
				JDKSupport.bigIntegerToLongValueExact(other.unscaledValue());
			}
		}
		return other;
	}
	
}
