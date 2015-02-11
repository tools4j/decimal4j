package org.decimal4j.jmh.value;

/**
 * Sign type for random value generation.
 * @see ValueType#random(SignType)
 */
public enum SignType {
	/** positive, negative and zero values*/
	ALL, 
	/** positive or zero values but never negative*/
	NON_ZERO, 
	/** positive values only without zero */
	POSITIVE
}