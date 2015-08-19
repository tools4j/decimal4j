<@pp.dropOutputFile />
<@pp.changeOutputFile name=pp.home + "org/decimal4j/exact/Multiplier.java" />
package org.decimal4j.exact;

<#list 0..maxScale as s>
import org.decimal4j.immutable.Decimal${s}f;
</#list>
<#list 0..maxScale as s>
import org.decimal4j.mutable.MutableDecimal${s}f;
</#list>

public final class Multiplier {
<#list 0..maxScale as s>

	public static Multipliable${s}f multiplyExact(Decimal${s}f factor) {
		return new Multipliable${s}f(factor);
	}
	public static Multipliable${s}f multiplyExact(MutableDecimal${s}f factor) {
		return new Multipliable${s}f(factor);
	}
</#list>

	//no instances
	private Multiplier() {}
}