<@pp.dropOutputFile />
<#list 0..maxScale as scale>
<@pp.changeOutputFile name=pp.home + "org/decimal4j/factory/Factory" + scale + "f.java" />
package org.decimal4j.factory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal${scale}f;
import org.decimal4j.mutable.MutableDecimal${scale}f;
import org.decimal4j.scale.Scale${scale}f;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;

/**
 * The factory for decimals with scale ${scale} creating {@link Decimal${scale}f} and
 * {@link MutableDecimal${scale}f} instances.
 */
public final class Factory${scale}f implements DecimalFactory<Scale${scale}f> {

	/**
	 * Singleton factory instance for immutable and mutable decimals with scale ${scale}.
	 */
	public static final Factory${scale}f INSTANCE = new Factory${scale}f();

	@Override
	public Scale${scale}f getScaleMetrics() {
		return Scale${scale}f.INSTANCE;
	}

	@Override
	public Decimal${scale}f valueOf(long value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public Decimal${scale}f valueOf(long value, OverflowMode overflowMode) {
		return Decimal${scale}f.valueOf(value, overflowMode);
	}

	@Override
	public Decimal${scale}f valueOf(double value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public Decimal${scale}f valueOf(double value, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOf(value, roundingMode);
	}

	@Override
	public Decimal${scale}f valueOf(double value, TruncationPolicy truncationPolicy) {
		return Decimal${scale}f.valueOf(value, truncationPolicy);
	}

	@Override
	public Decimal${scale}f valueOf(BigInteger value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public Decimal${scale}f valueOf(BigInteger value, OverflowMode overflowMode) {
		return Decimal${scale}f.valueOf(value, overflowMode);
	}

	@Override
	public Decimal${scale}f valueOf(BigDecimal value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public Decimal${scale}f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOf(value, roundingMode);
	}

	@Override
	public Decimal${scale}f valueOf(BigDecimal value, TruncationPolicy truncationPolicy) {
		return Decimal${scale}f.valueOf(value, truncationPolicy);
	}

	@Override
	public Decimal${scale}f valueOf(Decimal<?> value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public Decimal${scale}f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOf(value, roundingMode);
	}

	@Override
	public Decimal${scale}f valueOf(Decimal<?> value, TruncationPolicy truncationPolicy) {
		return Decimal${scale}f.valueOf(value, truncationPolicy);
	}

	@Override
	public Decimal${scale}f valueOf(String value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public Decimal${scale}f valueOf(String value, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOf(value, roundingMode);
	}

	@Override
	public Decimal${scale}f valueOf(String value, TruncationPolicy truncationPolicy) {
		return Decimal${scale}f.valueOf(value, truncationPolicy);
	}

	@Override
	public Decimal${scale}f valueOfUnscaled(long unscaledValue) {
		return Decimal${scale}f.valueOfUnscaled(unscaledValue);
	}

	@Override
	public Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale) {
		return Decimal${scale}f.valueOfUnscaled(unscaledValue, scale);
	}

	@Override
	public Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOfUnscaled(unscaledValue, scale, roundingMode);
	}

	@Override
	public Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale, TruncationPolicy truncationPolicy) {
		return Decimal${scale}f.valueOfUnscaled(unscaledValue, scale, truncationPolicy);
	}

	@Override
	public Decimal${scale}f[] newArray(int length) {
		return new Decimal${scale}f[length];
	}

	@Override
	public MutableDecimal${scale}f newMutable() {
		return new MutableDecimal${scale}f();
	}

	@Override
	public MutableDecimal${scale}f[] newMutableArray(int length) {
		return new MutableDecimal${scale}f[length];
	}

	// constructor for singleton instance
	private Factory${scale}f() {
		super();
	}
}
</#list>