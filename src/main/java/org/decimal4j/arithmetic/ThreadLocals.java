package org.decimal4j.arithmetic;

/**
 * Helper class to reset all thread locals to {@code null}. This may be useful
 * if the library is used in a web service or servlet container.
 */
public class ThreadLocals {
	
	/**
	 * Resets all {@link ThreadLocal} variables that are used by the decimal4j
	 * library. The variables are set to {@code null} to make objects held by
	 * thread local variables available for garbage collection.
	 */
	public static void resetAll() {
		StringConversion.STRING_BUILDER_THREAD_LOCAL.set(null);
		UnsignedDecimal9i36f.THREAD_LOCAL_1.set(null);
		UnsignedDecimal9i36f.THREAD_LOCAL_2.set(null);
	}
	
	// no instances
	private ThreadLocals() {
		super();
	}

}
