<@pp.dropOutputFile />
<#list 0..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "org/decimal4j/factory/Factory" + scale + "f.java" />
package org.decimal4j.factory;

import org.decimal4j.immutable.Decimal${scale}f;
import org.decimal4j.mutable.MutableDecimal${scale}f;
import org.decimal4j.scale.Scale${scale}f;

/**
 * The factory for decimals with scale ${scale} creating {@link Decimal${scale}f} and
 * {@link MutableDecimal${scale}f} instances.
 */
public final class Factory${scale}f implements DecimalFactory<Scale${scale}f> {

	/**
	 * Singleton factory instance immutable and mutable decimals with scale ${scale}.
	 */
	public static final Factory${scale}f INSTANCE = new Factory${scale}f();

	@Override
	public Decimal${scale}f createImmutable(long unscaled) {
		return Decimal${scale}f.valueOfUnscaled(unscaled);
	}

	@Override
	public MutableDecimal${scale}f createMutable(long unscaled) {
		return MutableDecimal${scale}f.unscaled(unscaled);
	}

	@Override
	public Decimal${scale}f[] createImmutableArray(int length) {
		return new Decimal${scale}f[length];
	}

	@Override
	public MutableDecimal${scale}f[] createMutableArray(int length) {
		return new MutableDecimal${scale}f[length];
	}

	// constructor for singleton instance
	private Factory${scale}f() {
		super();
	}
}
</#list>