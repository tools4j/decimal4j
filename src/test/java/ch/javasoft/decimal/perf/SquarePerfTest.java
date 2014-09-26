package ch.javasoft.decimal.perf;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.MutableDecimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

public class SquarePerfTest extends AbstractPerfTest {

	public SquarePerfTest(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
	}

	@Override
	protected String operation() {
		return "^2";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b, MathContext mathContext) {
		return a.multiply(a, mathContext);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		return a.square();
	}
	
	@Override
	protected int signumOfResult(BigDecimal a, BigDecimal b, MathContext mathContext) {
		return a.multiply(a, mathContext).signum();
	}
	
	@Override
	protected int signumOfResult(double a, double b) {
		return (int)Math.signum(a * a);
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(Decimal<S> a, Decimal<S> b) {
		return a.multiply(a).signum();
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(MutableDecimal<S, ?> m, Decimal<S> a, Decimal<S> b) {
		return m.set(a).square().signum();
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(DecimalArithmetics arith, long a, long b) {
		return arith.signum(arith.square(a));
	}

}
