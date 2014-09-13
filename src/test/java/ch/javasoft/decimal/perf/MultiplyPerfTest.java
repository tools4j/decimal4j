package ch.javasoft.decimal.perf;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.MutableDecimal;
import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

public class MultiplyPerfTest extends AbstractPerfTest {

	public MultiplyPerfTest(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
	}

	@Override
	protected String operation() {
		return "*";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b, MathContext mathContext) {
		return a.multiply(b, mathContext);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		return a.multiply(b);
	}
	
	@Override
	protected int signumOfResult(BigDecimal a, BigDecimal b, MathContext mathContext) {
		return a.multiply(b, mathContext).signum();
	}
	
	@Override
	protected int signumOfResult(double a, double b) {
		return (int)Math.signum(a * b);
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(Decimal<S> a, Decimal<S> b) {
		return a.multiply(b).signum();
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(MutableDecimal<S, ?, ?> m, Decimal<S> a, Decimal<S> b) {
		return m.set(a).multiply(b).signum();
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(DecimalArithmetics arith, long a, long b) {
		return arith.signum(arith.multiply(a, b));
	}

}
