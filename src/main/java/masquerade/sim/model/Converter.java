package masquerade.sim.model;

public interface Converter {
	<T> T convert(Object value, Class<T> to);
}