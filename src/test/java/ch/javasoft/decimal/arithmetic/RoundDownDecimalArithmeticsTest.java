package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

import ch.javasoft.decimal.ScaleMetrics.Scale6f;

/**
 * Unit test for {@link RoundingArithmetics} with {@link RoundingMode#DOWN}.
 */
public class RoundDownDecimalArithmeticsTest extends
		TruncatingDecimalArithmeticsTest {
	
	@Override
	protected DecimalArithmetics initArithmetics() {
		return Scale6f.INSTANCE.getTruncatingArithmetics().derive(RoundingMode.DOWN);
	}

}
