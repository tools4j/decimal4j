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
import ch.javasoft.decimal.truncate.DecimalRounding;
import ch.javasoft.decimal.truncate.TruncationPolicy;

public class DivideProblemTest extends DivideTest {

	public DivideProblemTest(ScaleMetrics scaleMetrics, TruncationPolicy truncationPolicy, DecimalArithmetics arithmetics) {
		super(scaleMetrics, truncationPolicy, arithmetics);
	}
	
	@Parameters(name = "{index}: scale={0}, rounding={1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();

		ScaleMetrics s;
		TruncationPolicy tp;
		
		s = Scale6f.INSTANCE;
		tp = DecimalRounding.DOWN.getCheckedTruncationPolicy();
		data.add(new Object[] {s, tp, s.getArithmetics(tp)});
		
		s = Scale0f.INSTANCE;
		tp = DecimalRounding.HALF_EVEN.getUncheckedTruncationPolicy();
		data.add(new Object[] {s, tp, s.getArithmetics(tp)});
		
		s = Scale6f.INSTANCE;
		tp = DecimalRounding.HALF_EVEN.getUncheckedTruncationPolicy();
		data.add(new Object[] {s, tp, s.getArithmetics(tp)});

		s = Scale6f.INSTANCE;
		tp = DecimalRounding.UNNECESSARY.getUncheckedTruncationPolicy();
		data.add(new Object[] {s, tp, s.getArithmetics(tp)});

		s = Scale17f.INSTANCE;
		tp = DecimalRounding.DOWN.getUncheckedTruncationPolicy();
		data.add(new Object[] {s, tp, s.getArithmetics(tp)});

		return data;
	}
	
	@Test
	public void runProblemTest0() {
		if (getScale() == 6 && !isUnchecked()) {
			final Decimal<Scale6f> dOpA = newDecimal(Scale6f.INSTANCE, 345);
			final Decimal<Scale6f> dOpB = newDecimal(Scale6f.INSTANCE, 0);
			runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest1() {
		if (getScale() == 0 && isUnchecked() && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale0f> dOpA = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE + 1);
			final Decimal<Scale0f> dOpB = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE);
			runTest(Scale0f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest2() {
		if (getScale() == 0 && isUnchecked() && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale0f> dOpA = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE);
			final Decimal<Scale0f> dOpB = newDecimal(Scale0f.INSTANCE, -Scale18f.INSTANCE.getScaleFactor());
			runTest(Scale0f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest3() {
		if (getScale() == 6 && isUnchecked() && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale6f> dOpA = newDecimal(Scale6f.INSTANCE, Long.MIN_VALUE);
			final Decimal<Scale6f> dOpB = newDecimal(Scale6f.INSTANCE, -10000000000000000L);
			runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest4() {
		if (getScale() == 6 && isUnchecked() && getRoundingMode() == RoundingMode.HALF_EVEN) {
			final Decimal<Scale6f> dOpA = newDecimal(Scale6f.INSTANCE, Long.MIN_VALUE);
			final Decimal<Scale6f> dOpB = newDecimal(Scale6f.INSTANCE, -4611686018427387905L);
			runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest5() {
		if (getScale() == 17 && isUnchecked()) {
			final Decimal<Scale17f> dOpA = newDecimal(Scale17f.INSTANCE, Scale17f.INSTANCE.getScaleFactor());
			final Decimal<Scale17f> dOpB = newDecimal(Scale17f.INSTANCE, -92233720368547L);
			runTest(Scale17f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest6() {
		if (getScale() == 17 && isUnchecked()) {
			final Decimal<Scale17f> dOpA = newDecimal(Scale17f.INSTANCE, Scale17f.INSTANCE.getScaleFactor());
			final Decimal<Scale17f> dOpB = newDecimal(Scale17f.INSTANCE, Integer.MIN_VALUE * 1000L);
			runTest(Scale17f.INSTANCE, "problem", dOpA, dOpB);
		}
	}
	@Test
	public void runProblemTest7() {
		if (getScale() == 6 && isUnchecked() && getRoundingMode() == RoundingMode.UNNECESSARY) {
			final Decimal<Scale6f> dOpA = newDecimal(Scale6f.INSTANCE, 99999999000000L);
			final Decimal<Scale6f> dOpB = newDecimal(Scale6f.INSTANCE, 5);
			runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB);
		}
	}

}
