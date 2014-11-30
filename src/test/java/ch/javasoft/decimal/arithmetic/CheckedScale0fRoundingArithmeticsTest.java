package ch.javasoft.decimal.arithmetic;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ch.javasoft.decimal.truncate.DecimalRounding;

public class CheckedScale0fRoundingArithmeticsTest {

	private CheckedScale0fRoundingArithmetics arithmetics;
	
	@Before
	public void initArithmetics() {
		arithmetics = new CheckedScale0fRoundingArithmetics(DecimalRounding.DOWN);
	}
	
	@Test
	public void fromDouble() {
		// given
		
		// when
		final long result = arithmetics.fromDouble(3.14);
		
		// then
		assertEquals("Result differs from expected", 3, result);
	}

}
