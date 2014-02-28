package ch.javasoft.decimal.arithmetic;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.arithmetic.RoundDownArithmetics;

/**
 * Unit test for {@link RoundDownArithmetics} and subclasses.
 */
public class RoundDownDecimalArithmeticsTest extends
		TruncatingDecimalArithmeticsTest {
	
	@Override
	protected DecimalArithmetics initArithmetics() {
		return new RoundDownArithmetics(6);
	}

}
