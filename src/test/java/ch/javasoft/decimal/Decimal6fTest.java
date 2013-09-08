package ch.javasoft.decimal;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import ch.javasoft.decimal.Decimal6f;
import ch.javasoft.decimal.MutableDecimal6f;

/**
 * Unit test for {@link Decimal6f} and {@link MutableDecimal6f}
 */
public class Decimal6fTest {
	
	@Test
	public void testMutableCalculationChain() {
		Assert.assertEquals(Decimal6f.valueOf(20), new MutableDecimal6f().add(7).multiply(3.0).subtract(BigInteger.ONE));
		Assert.assertEquals(Decimal6f.valueOf(20), new MutableDecimal6f().add(7).multiply(3.0).subtract(BigInteger.ONE).toImmutableValue());
	}
	@Test
	public void testCalculateToGetOneMillionth() {
		Assert.assertEquals(Decimal6f.MILLIONTH, new MutableDecimal6f(0.1).divide(10).divide(10).divide(1000));
		Assert.assertEquals(Decimal6f.MILLIONTH, new MutableDecimal6f(0.1).divide(10).divide(10).divide(1000).toImmutableValue());
		Assert.assertEquals(Decimal6f.MILLIONTH, new MutableDecimal6f(1).divide(10).divide(100).divide(1000));
		Assert.assertEquals(Decimal6f.MILLIONTH, new MutableDecimal6f(1).divide(10).divide(100).divide(1000).toImmutableValue());
		Assert.assertEquals(Decimal6f.MILLIONTH, new MutableDecimal6f(0.1).multiply(0.1).multiply(0.1).multiply(0.001));
		Assert.assertEquals(Decimal6f.MILLIONTH, new MutableDecimal6f(0.1).multiply(0.1).multiply(0.1).multiply(0.001).toImmutableValue());
		Assert.assertEquals(Decimal6f.MILLIONTH, Decimal6f.TEN.pow(-6));
		Assert.assertEquals(Decimal6f.MILLIONTH, new MutableDecimal6f(10).pow(-6));
	}
	
}
