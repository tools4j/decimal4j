package ch.javasoft.decimal.arithmetic;

import java.math.RoundingMode;

/**
 * Unit test for {@link RoundingArithmetics} with {@link RoundingMode#DOWN}.
 */
public class RoundDownDecimalArithmeticsTest extends
		TruncatingDecimalArithmeticsTest {
	
	@Override
	protected DecimalArithmetics initArithmetics() {
		return new RoundingArithmetics(6, RoundingMode.DOWN);
	}

}
