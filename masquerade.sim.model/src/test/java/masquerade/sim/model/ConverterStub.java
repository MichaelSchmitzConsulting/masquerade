package masquerade.sim.model;

/**
 * Test converter stub, does not provide any conversions
 */
public class ConverterStub implements Converter {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object value, Class<T> to) {
		return (T) value;
	}

	@Override
	public boolean canConvert(Class<?> from, Class<?> to) {
		return to.isAssignableFrom(from);
	}

}
