package ch.javasoft.decimal.perf;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.MutableDecimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.op.SqrtTest;
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
		return BIG_INT_SQRT ? SqrtTest.sqrt(a) : null;//null to avoid asserts
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		return a.sqrt();
	}
	
	@Override
	protected int signumOfResult(BigDecimal a, BigDecimal b, MathContext mathContext) {
		return BIG_INT_SQRT ? SqrtTest.sqrt(a).signum() : signumOfResult(a.doubleValue(), b.doubleValue());
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
