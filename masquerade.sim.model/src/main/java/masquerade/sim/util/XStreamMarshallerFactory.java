package masquerade.sim.util;

import java.lang.reflect.Constructor;
import java.util.UUID;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XStreamMarshallerFactory {
	private static final String ROOT_ELEMENT_NAME = "masquerade-simulation";

	public XStream createXStream() {
		XStream xstream = new XStream(createReflectionProvider(), new DomDriver());
		xstream.setMode(XStream.ID_REFERENCES);
		xstream.alias(ROOT_ELEMENT_NAME, SimulationModelContainer.class);

		return xstream;
	}
	
	private static ReflectionProvider createReflectionProvider() {
		// TODO: Remove this, use no-arg constructors for model objects (conforming to JavaBeans spec) instead of ctor(name)
		return new PureJavaReflectionProvider() {
			@Override
			@SuppressWarnings({"rawtypes" })
			public Object newInstance(Class type) {
				try {
					Constructor<?> noArgConstructor = getConstructorIfExists(type);
					Constructor<?> singleStringArgConstructor = getConstructorIfExists(type, String.class);
					if (noArgConstructor == null && singleStringArgConstructor != null) {
						return singleStringArgConstructor.newInstance("generated-name-" + UUID.randomUUID());
					}
				} catch (Exception ex) {
					throw new ObjectAccessException("Error while instantiating class " + type.getName(), ex);
				}
				return super.newInstance(type);
			}
		};		
	}

	private static Constructor<?> getConstructorIfExists(Class<?> type, Class<?>... parameterTypes) {
		try {
			return type.getConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

}
