package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.RoundDownDecimalArithmetics;

/**
 * Unit test for {@link RoundDownDecimalArithmetics} and subclasses.
 */
public class RoundDownDecimalArithmeticsTest extends
		TruncatingDecimalArithmeticsTest {
	
	@Override
	protected DecimalArithmetics initArithmetics() {
		return new RoundDownDecimalArithmetics(6);
	}

}
