package ch.javasoft.decimal.arithmetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.math.RoundingMode;

import org.junit.Test;

/**
 * Unit test for {@link DecimalRounding#valueOf(RoundingMode)}
 */
public class DecimalRoundingValueOfTest {

	@Test
	public void shouldGetByRoundingMode() {
		assertEquals("different number of constant in RoundingMode and DecimalRounding", RoundingMode.values().length, DecimalRounding.values().length);
		for (final RoundingMode mode : RoundingMode.values()) {
			final DecimalRounding rounding = DecimalRounding.valueOf(mode);
			assertSame("wrong RoundingMode in DecimalRounding", mode, rounding.getRoundingMode());
			assertEquals("constant name of RoundingMode and DecimalRounding does not match", mode.name(), rounding.name());
		}
	}
	
}
