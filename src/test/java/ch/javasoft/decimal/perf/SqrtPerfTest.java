package ch.javasoft.decimal.perf;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.MutableDecimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

public class SqrtPerfTest extends AbstractPerfTest {
	
	private static final boolean BIG_INT_SQRT = false;

	public SqrtPerfTest(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
	}

	@Override
	protected long nextRandomLong() {
		long val;
		do {
			val = super.nextRandomLong();
		} while (val < 0);//avoid sqrt of negative value
		return val;
	}
	@Override
	protected String operation() {
		return "^(1/2)";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b, MathContext mathContext) {
		return BIG_INT_SQRT ? sqrt(a, mathContext) : null;//null to avoid asserts
	}
	protected BigDecimal sqrt(BigDecimal a, MathContext mathContext) {
		if (a.signum() < 0) {
			throw new ArithmeticException("sqrt of a negative value: " + a);
		}
		final int scale = a.scale();
		final BigInteger bigInt = a.unscaledValue().multiply(BigInteger.TEN.pow(scale));
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
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		return a.sqrt();
	}
	
	@Override
	protected int signumOfResult(BigDecimal a, BigDecimal b, MathContext mathContext) {
		return BIG_INT_SQRT ? sqrt(a, mathContext).signum() : signumOfResult(a.doubleValue(), b.doubleValue());
	}
	
	@Override
	protected int signumOfResult(double a, double b) {
		return (int)Math.signum(Math.sqrt(a));
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(Decimal<S> a, Decimal<S> b) {
		return a.sqrt().signum();
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(MutableDecimal<S, ?> m, Decimal<S> a, Decimal<S> b) {
		return m.set(a).sqrt().signum();
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(DecimalArithmetics arith, long a, long b) {
		return arith.signum(arith.sqrt(a));
	}

}
