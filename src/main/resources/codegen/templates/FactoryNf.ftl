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
import org.decimal4j.scale.ScaleMetrics;

/**
 * The factory for decimals with scale ${scale} creating {@link Decimal${scale}f} and
 * {@link MutableDecimal${scale}f} instances.
 */
public enum Factory${scale}f implements DecimalFactory<Scale${scale}f> {

	/**
	 * Singleton factory instance for immutable and mutable decimals with scale ${scale}.
	 */
	INSTANCE;

	@Override
	public final Scale${scale}f getScaleMetrics() {
		return Scale${scale}f.INSTANCE;
	}
	
	@Override
	public final int getScale() {
		return Scale${scale}f.SCALE;
	}

	@Override
	public final Class<Decimal${scale}f> immutableType() {
		return Decimal${scale}f.class;
	}

	@Override
	public final Class<MutableDecimal${scale}f> mutableType() {
		return MutableDecimal${scale}f.class;
	}

	@Override
	public final DecimalFactory<?> deriveFactory(int scale) {
		return Factories.getDecimalFactory(scale);
	}
	
	@Override
	public final <S extends ScaleMetrics> DecimalFactory<S> deriveFactory(S scaleMetrics) {
		return Factories.getDecimalFactory(scaleMetrics);
	}

	@Override
	public final Decimal${scale}f valueOf(long value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public final Decimal${scale}f valueOf(float value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public final Decimal${scale}f valueOf(float value, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal${scale}f valueOf(double value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public final Decimal${scale}f valueOf(double value, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal${scale}f valueOf(BigInteger value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public final Decimal${scale}f valueOf(BigDecimal value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public final Decimal${scale}f valueOf(BigDecimal value, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal${scale}f valueOf(Decimal<?> value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public final Decimal${scale}f valueOf(Decimal<?> value, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal${scale}f parse(String value) {
		return Decimal${scale}f.valueOf(value);
	}

	@Override
	public final Decimal${scale}f parse(String value, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOf(value, roundingMode);
	}

	@Override
	public final Decimal${scale}f valueOfUnscaled(long unscaledValue) {
		return Decimal${scale}f.valueOfUnscaled(unscaledValue);
	}

	@Override
	public final Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale) {
		return Decimal${scale}f.valueOfUnscaled(unscaledValue, scale);
	}

	@Override
	public final Decimal${scale}f valueOfUnscaled(long unscaledValue, int scale, RoundingMode roundingMode) {
		return Decimal${scale}f.valueOfUnscaled(unscaledValue, scale, roundingMode);
	}

	@Override
	public final Decimal${scale}f[] newArray(int length) {
		return new Decimal${scale}f[length];
	}

	@Override
	public final MutableDecimal${scale}f newMutable() {
		return new MutableDecimal${scale}f();
	}

	@Override
	public final MutableDecimal${scale}f[] newMutableArray(int length) {
		return new MutableDecimal${scale}f[length];
	}
}
</#list>