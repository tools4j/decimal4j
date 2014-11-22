package ch.javasoft.decimal.op;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.Scale18f;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Unit test for {@link Decimal#invert()}
 */
@RunWith(Parameterized.class)
public class SqrtTest extends AbstractOperandTest {
	
	private static final int precision = 20;
	private static final BigDecimal TEN_POW_PRECISION = BigDecimal.TEN.pow(precision);
	private static final BigDecimal TEN_POW_2xPRECISION = BigDecimal.TEN.pow(precision<<1);

	public SqrtTest(ScaleMetrics scaleMetrics, RoundingMode roundingMode, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: scale={0}, rounding={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			for (final RoundingMode rm : UNCHECKED_ROUNDING_MODES) {
				data.add(new Object[] { s, rm, s.getArithmetics(rm) });
			}
		}
		return data;
	}
	
	@Override
	public void runRandomTest() {
		super.runRandomTest();
	}
	
	@Override
	public void runSpecialValueTest() {
		super.runSpecialValueTest();
	}

	@Test
	public void runProblemTest0_offByOne() {
		if (getScale() == 18) {
			final Decimal<Scale18f> val = newDecimal(Scale18f.INSTANCE, 6900127896905764146L);
			runTest(val, -1);
		}
	}
	@Test
	public void runProblemTest2_offByTwo() {
		if (getScale() == 18) {
			final Decimal<Scale18f> val = newDecimal(Scale18f.INSTANCE, 8669660385983162309L);
			runTest(val, -1);
		}
	}

	@Override
	protected String operation() {
		return "sqrt";
	}

	private BigDecimal expectedResult(BigDecimal bigDecimal) {
		//we calculate 20 extra decimal places, should be enough, chance that we have 20 zero's or a 5 and 19 zeros is relatively low
		return sqrt(bigDecimal.multiply(TEN_POW_2xPRECISION)).divide(TEN_POW_PRECISION, getScale(), getRoundingMode());
	}
	public static BigDecimal sqrt(BigDecimal bigDecimal) {
		if (bigDecimal.signum() < 0) {
			throw new ArithmeticException("Square root of a negative value: " + bigDecimal);
		}
		final int scale = bigDecimal.scale();
		final BigInteger bigInt = bigDecimal.unscaledValue().multiply(BigInteger.TEN.pow(scale));
		int len = bigInt.bitLength();
		len += len & 0x1;//round up if odd
		BigInteger rem = BigInteger.ZERO;
		BigInteger root = BigInteger.ZERO;
		for (int i = len-1; i >= 0; i-=2) {
			root = root.shiftLeft(1);
			rem = rem.shiftLeft(2);
			final int add = (bigInt.testBit(i) ? 2 : 0) + (bigInt.testBit(i-1) ? 1 : 0);
			rem = rem.add(BigInteger.valueOf(add));
			final BigInteger rootPlusOne = root.add(BigInteger.ONE);
			if (rootPlusOne.compareTo(rem) <= 0) {
				rem = rem.subtract(rootPlusOne);
				root = rootPlusOne.add(BigInteger.ONE);
			}
		}
		return new BigDecimal(root.shiftRight(1), scale);
	}

	private <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> operand) {
		if (isStandardTruncationPolicy() && rnd.nextBoolean()) {
			return operand.sqrt();
		} else {
			return operand.sqrt(getRoundingMode());
		}
	}
	
	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		runTest(randomDecimal(scaleMetrics), index);
	}

	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		int index  = 0;
		for (final long unscaledSpecial : getSpecialValues(scaleMetrics)) {
			runTest(newDecimal(scaleMetrics, unscaledSpecial), index++);
		}
	}
	
	private <S extends ScaleMetrics> void runTest(Decimal<S> operand, int index) {
		if (operand.isNegative()) {
			runNegativeTest(operand);
			return;
		}
		
		if (rnd.nextBoolean() && (getRoundingMode() == RoundingMode.DOWN || getRoundingMode() == RoundingMode.FLOOR)) {
			//when: positive
			final Decimal<S> actual = actualResult(operand);
			
			//then: compare operand with actual^2 and (actual+ULP)^2
			final BigDecimal x = operand.toBigDecimal();
			final BigDecimal xSquared = actual.toBigDecimal().pow(2);
			final BigDecimal xPlusUlpSquared = actual.addUnscaled(1).toBigDecimal().pow(2);
			
			final String msg = "{x=" + operand + ", y=sqrt(x)=" + actual + ", sqrt(x)+ULP=" + actual.addUnscaled(1) + ", sqrt(x)^2=" + xSquared.toPlainString() + ", (sqrt(x)+ULP)^2=" + xPlusUlpSquared.toPlainString() + "}";
			assertTrue("[" + index + "] sqrt(x)^2 must be <= x. " + msg, xSquared.compareTo(x) <= 0);
			assertTrue("[" + index + "] sqrt(x+ULP)^2 must be > x. " + msg, xPlusUlpSquared.compareTo(x) > 0);
		} else {
			//expected
			ArithmeticResult<Long> expected;
			try {
				expected = ArithmeticResult.forResult(arithmetics, expectedResult(toBigDecimal(operand)));
			} catch (ArithmeticException e) {
				expected = ArithmeticResult.forException(e);
			}

			//actual
			ArithmeticResult<Long> actual;
			try {
				actual = ArithmeticResult.forResult(actualResult(operand));
			} catch (ArithmeticException e) {
				actual = ArithmeticResult.forException(e);
			}

			//assert
			final String name = "[" + index + "]";
			actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + operand + " " + operation());
		}
	}
	private <S extends ScaleMetrics> void runNegativeTest(Decimal<S> operand) {
		try {
			//when
			final Decimal<S> actual = actualResult(operand);
			
			//then: expect exception
			fail("expected arithmetic exception for sqrt(" + operand + ") but result was: " + actual);
		} catch (ArithmeticException e) {
			//as expected
			return;
		}
	}
}
