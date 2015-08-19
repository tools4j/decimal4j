<@pp.dropOutputFile />
<#list 0..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "org/decimal4j/exact/Multipliable" + scale + "f.java" />
package org.decimal4j.exact;

import java.util.Objects;

import org.decimal4j.api.Decimal;
<#list 0..maxScale as s>
<#if (scale+s <= maxScale) || (scale <= s)>
import org.decimal4j.immutable.Decimal${s}f;
</#if>
</#list>
<#list 0..maxScale as s>
<#if (scale+s <= maxScale) && (scale != s)>
import org.decimal4j.mutable.MutableDecimal${s}f;
</#if>
</#list>
import org.decimal4j.scale.Scale${scale}f;

public final class Multipliable${scale}f {
	
	private final Decimal<Scale${scale}f> factor;
	
	public Multipliable${scale}f(Decimal<Scale${scale}f> factor) {
		this.factor = Objects.requireNonNull(factor, "factor cannot be null");
	}
<#if (scale+scale <= maxScale)>

	public Decimal${scale+scale}f square() {
		return by(this.factor);
	}
	public Decimal${scale+scale}f by(Decimal<Scale${scale}f> factor) {
		return Decimal${scale+scale}f.valueOf(this.factor.multiplyExact(factor));
	}
</#if>
<#list 0..maxScale as scale2>
<#if (scale != scale2) && (scale+scale2 <= maxScale)>

	public Decimal${scale+scale2}f by(Decimal${scale2}f factor) {
		return Decimal${scale+scale2}f.valueOf(this.factor.multiplyExact(factor));
	}
	public Decimal${scale+scale2}f by(MutableDecimal${scale2}f factor) {
		return Decimal${scale+scale2}f.valueOf(this.factor.multiplyExact(factor));
	}
</#if>
</#list>

	@Override
	public int hashCode() {
		return factor.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		return factor.equals(((Multipliable${scale}f)obj).factor);
	}

	@Override
	public String toString() {
		return factor.toString();
	}
}
</#list>