package ch.javasoft.decimal.perf;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.MutableDecimal;
import ch.javasoft.decimal.ScaleMetrics;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;

public class AddPerfTest extends AbstractPerfTest {

	public AddPerfTest(ScaleMetrics scaleMetrics) {
		super(scaleMetrics);
	}

	@Override
	protected String operation() {
		return "+";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, BigDecimal b, MathContext mathContext) {
		return a.add(b, mathContext);
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, Decimal<S> b) {
		return a.add(b);
	}
	
	@Override
	protected int signumOfResult(BigDecimal a, BigDecimal b, MathContext mathContext) {
		return a.add(b, mathContext).signum();
	}
	
	@Override
	protected int signumOfResult(double a, double b) {
		return (int)Math.signum(a + b);
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(Decimal<S> a, Decimal<S> b) {
		return a.add(b).signum();
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(MutableDecimal<S, ?> m, Decimal<S> a, Decimal<S> b) {
		return m.set(a).add(b).signum();
	}
	
	@Override
	protected <S extends ScaleMetrics> int signumOfResult(DecimalArithmetics arith, long a, long b) {
		return arith.signum(arith.add(a, b));
	}

}
