package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;

/**
 * Unit test for the diverse methods returning a boolean value, such
 * as {@link Decimal#isZero()}, {@link Decimal#isOne()}, {@link Decimal#isNegative()} etc.
 */
@RunWith(Parameterized.class)
public class IsSomethingTest extends Abstract1DecimalArgToAnyResultTest<Boolean> {
	
	public static enum Operation {
		isZero {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.compareTo(BigDecimal.ZERO) == 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isZero();
			}
		},
		isOne {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.compareTo(BigDecimal.ONE) == 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isOne();
			}
		},
		isMinusOne {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.compareTo(BigDecimal.ONE.negate()) == 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isMinusOne();
			}
		},
		isUlp {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.unscaledValue().equals(BigInteger.ONE);
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isUlp();
			}
		},
		isPositive {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.signum() > 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isPositive();
			}
		},
		isNonNegative {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.signum() >= 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isNonNegative();
			}
		},
		isNegative {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.signum() < 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isNegative();
			}
		},
		isNonPositive {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.signum() <= 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isNonPositive();
			}
		},
		isIntegral {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.compareTo(operand.divideToIntegralValue(BigDecimal.ONE)) == 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isIntegral();
			}
		},
		isIntegralPartZero {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.compareTo(BigDecimal.ONE) < 0 && operand.compareTo(BigDecimal.ONE.negate()) > 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isIntegralPartZero();
			}
		},
		isBetweenZeroAndOne {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.compareTo(BigDecimal.ONE) < 0 && operand.compareTo(BigDecimal.ZERO) >= 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isBetweenZeroAndOne();
			}
		},
		isBetweenZeroAndMinusOne {
			@Override
			public boolean expectedResult(BigDecimal operand) {
				return operand.compareTo(BigDecimal.ONE.negate()) > 0 && operand.compareTo(BigDecimal.ZERO) <= 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> operand) {
				return operand.isBetweenZeroAndMinusOne();
			}
		};
		abstract public boolean expectedResult(BigDecimal operand);
		abstract public <S extends ScaleMetrics>boolean actualResult(Decimal<S> operand);
	}
	
	private final Operation operation;
	public IsSomethingTest(ScaleMetrics scaleMetrics, Operation operation, DecimalArithmetics arithmetics) {
		super(arithmetics);
		this.operation = operation;
	}

	@Parameters(name = "{index}: scale={0}, operation={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : SCALES) {
			for (final Operation op : Operation.values()) {
				data.add(new Object[] {s, op, s.getDefaultArithmetics()});
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return operation.name();
	}
	
	@Override
	protected Boolean expectedResult(BigDecimal operand) {
		return operation.expectedResult(operand);
	}
	
	@Override
	protected <S extends ScaleMetrics> Boolean actualResult(Decimal<S> operand) {
		return operation.actualResult(operand);
	}
}
