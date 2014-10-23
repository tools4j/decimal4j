package ch.javasoft.decimal.op;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.Decimal;
import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.Scale0f;
import ch.javasoft.decimal.scale.Scale17f;
import ch.javasoft.decimal.scale.Scale18f;
import ch.javasoft.decimal.scale.Scale6f;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.truncate.OverflowMode;

public class DivideProblemTest extends DivideTest {

	public DivideProblemTest(ScaleMetrics scaleMetrics, RoundingMode roundingMode, DecimalArithmetics arithmetics) {
		super(scaleMetrics, OverflowMode.UNCHECKED.getTruncationPolicyFor(roundingMode), arithmetics);
	}
	
	@Parameters(name = "{index}: scale={0}, rounding={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();

		ScaleMetrics s;
		RoundingMode r;
		
		s = Scale0f.INSTANCE;
		r = RoundingMode.HALF_EVEN;
		data.add(new Object[] {s, r, s.getArithmetics(r)});
		
		s = Scale6f.INSTANCE;
		r = RoundingMode.HALF_EVEN;
		data.add(new Object[] {s, r, s.getArithmetics(r)});

		s = Scale17f.INSTANCE;
		r = RoundingMode.DOWN;
		data.add(new Object[] {s, r, s.getArithmetics(r)});

		return data;
	}
	
	@Test
	public void runProblemTest1() {
		if (getScale() == 0 && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale0f> dOpA = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE + 1);
			final Decimal<Scale0f> dOpB = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE);
			runTest(Scale0f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest2() {
		if (getScale() == 0 && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale0f> dOpA = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE);
			final Decimal<Scale0f> dOpB = newDecimal(Scale0f.INSTANCE, -Scale18f.INSTANCE.getScaleFactor());
			runTest(Scale0f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest3() {
		if (getScale() == 6 && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale6f> dOpA = newDecimal(Scale6f.INSTANCE, Long.MIN_VALUE);
			final Decimal<Scale6f> dOpB = newDecimal(Scale6f.INSTANCE, -10000000000000000L);
			runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest4() {
		if (getScale() == 6 && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale6f> dOpA = newDecimal(Scale6f.INSTANCE, Long.MIN_VALUE);
			final Decimal<Scale6f> dOpB = newDecimal(Scale6f.INSTANCE, -4611686018427387905L);
			runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest5() {
		if (getScale() == 17) {
			final Decimal<Scale17f> dOpA = newDecimal(Scale17f.INSTANCE, Scale17f.INSTANCE.getScaleFactor());
			final Decimal<Scale17f> dOpB = newDecimal(Scale17f.INSTANCE, -92233720368547L);
			runTest(Scale17f.INSTANCE, "problem", dOpA, dOpB);
		}
	}

}
