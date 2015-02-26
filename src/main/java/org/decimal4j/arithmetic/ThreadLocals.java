package org.decimal4j.arithmetic;

/**
 * Helper class to remove all values held by {@link ThreadLocal} variables. This 
 * may be useful if the library is used in a web service or servlet container.
 */
public class ThreadLocals {
	
	/**
	 * Removes all values held by {@link ThreadLocal} variables that are used by 
	 * the decimal4j library. {@link ThreadLocal#remove()} is called on every
	 * variable to make objects held by those variables available for garbage 
	 * collection.
	 */
	public static void removeAll() {
		StringConversion.STRING_BUILDER_THREAD_LOCAL.remove();
		UnsignedDecimal9i36f.THREAD_LOCAL_1.remove();
		UnsignedDecimal9i36f.THREAD_LOCAL_2.remove();
	}
	
	// no instances
	private ThreadLocals() {
		super();
	}

}
