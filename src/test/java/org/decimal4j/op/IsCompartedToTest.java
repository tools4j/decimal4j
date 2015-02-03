package org.decimal4j.op;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit test for the diverse comparison methods returning a boolean value, such
 * as {@link Decimal#isEqualTo(Decimal)}, {@link Decimal#isGreaterThan(Decimal)} etc.
 */
@RunWith(Parameterized.class)
public class IsCompartedToTest extends Abstract2DecimalArgsToAnyResultTest<Boolean> {
	
	public static enum Operation {
		isEqualto {
			@Override
			public boolean expectedResult(int compareToResult) {
				return compareToResult == 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> a, Decimal<S> b) {
				return a.isEqualTo(b);
			}
		},
		isGreaterThan {
			@Override
			public boolean expectedResult(int compareToResult) {
				return compareToResult > 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> a, Decimal<S> b) {
				return a.isGreaterThan(b);
			}
		},
		isGreaterThanOrEqualTo {
			@Override
			public boolean expectedResult(int compareToResult) {
				return compareToResult >= 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> a, Decimal<S> b) {
				return a.isGreaterThanOrEqualTo(b);
			}
		},
		isLessThan {
			@Override
			public boolean expectedResult(int compareToResult) {
				return compareToResult < 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> a, Decimal<S> b) {
				return a.isLessThan(b);
			}
		},
		isLessThanOrEqualTo {
			@Override
			public boolean expectedResult(int compareToResult) {
				return compareToResult <= 0;
			}
			@Override
			public <S extends ScaleMetrics> boolean actualResult(Decimal<S> a, Decimal<S> b) {
				return a.isLessThanOrEqualTo(b);
			}
		};
		
		abstract public boolean expectedResult(int compareToResult);
		abstract public <S extends ScaleMetrics>boolean actualResult(Decimal<S> a, Decimal<S> b);
	}
	
	private final Operation operation;
	public IsCompartedToTest(ScaleMetrics scaleMetrics, Operation operation, DecimalArithmetic arithmetic) {
		super(arithmetic);
		this.operation = operation;
	}

	@Parameters(name = "{index}: scale={0}, operation={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final Operation op : Operation.values()) {
				data.add(new Object[] {s, op, s.getDefaultArithmetic()});
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return operation.name();
	}
	
	@Override
	protected Boolean expectedResult(BigDecimal a, BigDecimal b) {
		return operation.expectedResult(a.compareTo(b));
	}
	
	@Override
	protected <S extends ScaleMetrics> Boolean actualResult(Decimal<S> a, Decimal<S> b) {
		return operation.actualResult(a, b);
	}
}
