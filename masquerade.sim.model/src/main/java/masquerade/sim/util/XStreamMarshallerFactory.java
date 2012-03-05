package masquerade.sim.util;

import java.lang.reflect.Constructor;
import java.util.UUID;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Factory creating pre-configured {@link XStream} marshallers for
 * marshalling simulation model objects.
 */
public class XStreamMarshallerFactory {
	public XStream createXStream() {
		XStream xstream = new XStream(createReflectionProvider(), new DomDriver());
		xstream.setMode(XStream.NO_REFERENCES);
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
