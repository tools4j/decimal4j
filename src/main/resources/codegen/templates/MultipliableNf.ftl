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

/**
 * A {@code Multipliable${scale}f} encapsulates a Decimal of scale ${scale} and facilitates
 * exact typed multiplication. The multipliable object acts as first factor in the multiplication
 * and provides a set of overloaded methods for different scales. Each one of those methods 
 * delivers a different result scale which represents the appropriate scale for the product of
 * an exact multiplication.
 * <p>
 * A {@code Multipliable${scale}f} object is returned by {@link Decimal${scale}f#multiplyExact()},
 * hence an exact typed multiplication can be written as:
 * <pre>
 * Decimal${scale}f value = ... //some value
<#if (scale+2<=maxScale)>
 * Decimal${scale+2}f product = value.multiplyExact().by(Decimal2f.FIVE);
<#else>
 * Decimal${scale}f product = value.multiplyExact().by(Decimal0f.FIVE);
</#if>
 * </pre>
 */
public final class Multipliable${scale}f {
	
	private final Decimal<Scale${scale}f> value;
	
	/**
	 * Constructor with Decimal value to be encapsulated.
	 * @param value the decimal value to be wrapped as a multipliable object
	 */
	public Multipliable${scale}f(Decimal<Scale${scale}f> value) {
		this.value = Objects.requireNonNull(value, "value cannot be null");
	}
	
	/**
	 * Returns the value underlying this Multipliable${scale}f.
	 * @return the Decimal value wrapped by this multipliable object
	 */
	public Decimal<Scale${scale}f> getValue() {
		return value;
	}
<#if (scale+scale <= maxScale)>

	/**
	 * Returns a {@code Decimal} whose value is <tt>(this<sup>2</sup>)</tt>. The
	 * result is exact and has scale ${scale+scale} which is twice the scale of
	 * the Decimal that this multipliable object represents. An
	 * {@link ArithmeticException} is thrown if the product is out of the
	 * possible range for a {@code Decimal${scale+scale}f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @return <tt>(this * this)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal${scale+scale}f}
	 */
	public Decimal${scale+scale}f square() {
		return by(this.value);
	}

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale ${scale+scale} which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal${scale+scale}f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal${scale+scale}f}
	 */
	public Decimal${scale+scale}f by(Decimal<Scale${scale}f> factor) {
		return Decimal${scale+scale}f.valueOf(this.value.multiplyExact(factor));
	}
</#if>
<#list 0..maxScale as scale2>
<#if (scale != scale2) && (scale+scale2 <= maxScale)>

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale ${scale+scale2} which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal${scale+scale2}f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal${scale+scale2}f}
	 */
	public Decimal${scale+scale2}f by(Decimal${scale2}f factor) {
		return Decimal${scale+scale2}f.valueOf(this.value.multiplyExact(factor));
	}
	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale ${scale+scale2} which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal${scale+scale2}f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <tt>(this * factor)</tt>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal${scale+scale2}f}
	 */
	public Decimal${scale+scale2}f by(MutableDecimal${scale2}f factor) {
		return Decimal${scale+scale2}f.valueOf(this.value.multiplyExact(factor));
	}
</#if>
</#list>

	
	/**
	 * Returns a hash code for this <tt>Multipliable${scale}f</tt> which happens to be the 
	 * hash code of the underlying {@code Decimal${scale}f} value.
	 * 
	 * @return a hash code value for this object
	 * @see Decimal#hashCode()
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Compares this Multipliable${scale}f to the specified object. The result is {@code true}
	 * if and only if the argument is a {@code Multipliable${scale}f} with an equal underlying 
	 * {@link #getValue() value}.
	 * 
	 * @param obj
	 *            the object to compare with
	 * @return {@code true} if the argument is a {@code Multipliable${scale}f} and if its value
	 *         is equal to this multipliables's value; {@code false} otherwise
	 * @see #getValue()
	 * @see Decimal#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		return value.equals(((Multipliable${scale}f)obj).value);
	}

	/**
	 * Returns a string representation of this {@code Multipliable${scale}f} which is
	 * simply the string representation of the underlying Decimal {@link #getValue() value}.
	 * 
	 * @return a {@code String} Decimal representation of this {@code Multipliable${scale}f}'s
	 *         value with all the fraction digits (including trailing zeros)
	 * @see #getValue()
	 * @see Decimal#toString()
	 */
	@Override
	public String toString() {
		return value.toString();
	}
}
</#list>