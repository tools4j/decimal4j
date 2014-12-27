package ch.javasoft.decimal.op;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import ch.javasoft.decimal.immutable.Decimal18f;
import ch.javasoft.decimal.truncate.DecimalRounding;

public class OperatorTest {

	private boolean a() {
		System.out.println("a");
		return false;
	}
	
	private boolean b() {
		System.out.println("b");
		return true;
	}
	
	@Test
	public void test() {
		assertFalse(a() & b());
//		assertFalse(a() && b());
	}
	
	@Test
	public void valueTests() {
		// given
		final Decimal18f decimal = Decimal18f.valueOfUnscaled(-110486066851821044L);
		
		// when
		final Decimal18f result = decimal.invert(DecimalRounding.UP.getCheckedTruncationPolicy());
		System.out.println(result);
	}

}
