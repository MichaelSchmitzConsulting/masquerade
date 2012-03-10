package masquerade.sim.util;

/**
 * Creates deep clones of Java Beans
 */
public interface BeanCloner {

	/**
	 * Copies all read/write properties from a source bean instance to a target bean instance. Beans
	 * must be of same type. Creates deep copies, all nested properties will be cloned as well.
	 * 
	 * @param source Source bean instance to copy all properties from to target
	 * @param target Target bean instance
	 * @exception IllegalArgumentException If the properties cannot be copied, e.g. due to incompatible instances or due to nested properties not 
	 *                                     conforming to the java beans spec (e.g. no default constructor).
	 */
	void cloneBean(Object source, Object target);

	/**
	 * Creates a copy of a bean instance using it's type's default constructor, and copies all 
	 * read/write properties to the copy. Creates deep copies, all nested properties will be 
	 * cloned as well.
	 * 
	 * @param sourceValue
	 * @return A copy of the bean with all read/write properties copied as well
	 * @exception IllegalArgumentException If the properties cannot be copied, e.g. due to incompatible instances or due to nested properties not 
	 *                                     conforming to the java beans spec (e.g. no default constructor).
	 */
	Object cloneBean(Object source);

}
