package masquerade.sim.model;

/**
 * Value converter (e.g. XML &lt;-&gt; {@link String}, {@link Number} &lt;-&gt; {@link String} ...) 
 */
public interface Converter {
	/**
	 * @param <T> Expected return type
	 * @param value Value to be converted
	 * @param to Expected return type class
	 * @return Converted value being of the expected type, or <code>null</code> if conversion is not available
	 */
	<T> T convert(Object value, Class<T> to);
	
	/**
	 * @param from Source type
	 * @param to Target type
	 * @return <code>true</code> if this converter can handle a conversion from the source to the target type
	 */
	boolean canConvert(Class<?> from, Class<?> to);
}
