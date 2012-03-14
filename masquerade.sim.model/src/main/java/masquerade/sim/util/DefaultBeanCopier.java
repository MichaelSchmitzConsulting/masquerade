package masquerade.sim.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Creates deep clones of Java Beans, taking special cases such as collections, maps,
 * numbers, strings, primitives and classes into account.
 * 
 * Instances are safe to use from multiple threads at the same time.
 */
public class DefaultBeanCopier implements BeanCopier {
	private static final String MSG = "Unable to clone bean property";

	@Override
	public void copyBean(Object source, Object target) {
		try {
			// TODO: Remember path for better error messages
			// TODO: Handle cycles
			internalClone(source, target);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(MSG, e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(MSG, e);
		}
	}
	
	private void internalClone(Object source, Object target) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (source == null || target == null) {
			throw new IllegalArgumentException("Cannot copy properties from/to null source or target"); 
		}
		
		BeanInfo beanInfo = getBeanInfo(source);
		
		for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors()) {
			if (isReadWrite(prop)) {
				Object sourceValue = prop.getReadMethod().invoke(source);
				Object valueCopy = copyBean(sourceValue);
				
				prop.getWriteMethod().invoke(target, valueCopy);
			}
		}
	}

	@Override
	public Object copyBean(Object sourceValue) {
		if (sourceValue == null) {
			return null;
		}
		
		Class<?> type = sourceValue.getClass();

		if (type.isPrimitive() || Number.class.isAssignableFrom(type) || String.class == type || Class.class == type || Boolean.class == type) {
			// Copy primitive type properties using boxing/unboxing which is a copy by value already
			// Number, String, Class are immutable, keep reference to original value
			return sourceValue;
		} else if (LinkedHashMap.class.isAssignableFrom(type)) {
			// Copy ordered maps
			Map<?, ?> map = (Map<?, ?>) sourceValue;
			Map<Object, Object> mapCopy = new LinkedHashMap<Object, Object>(map);
			return copyMap(map, mapCopy);
		} else if (Map.class.isAssignableFrom(type)) {
			// Copy maps
			Map<?, ?> map = (Map<?, ?>) sourceValue;
			Map<Object, Object> mapCopy = new HashMap<Object, Object>(map);
			return copyMap(map, mapCopy);
		} else if (LinkedHashSet.class.isAssignableFrom(type)) {
			// Copy linked sets
			Set<?> set = (Set<?>) sourceValue;
			Collection<Object>  setCopy = new LinkedHashSet<Object>(set);
			return copyCollection(set, setCopy);
		} else if (Set.class.isAssignableFrom(type)) {
			// Copy sets
			Set<?> set = (Set<?>) sourceValue;
			Collection<Object>  setCopy = new HashSet<Object>(set);
			return copyCollection(set, setCopy);
		} else if (Collection.class.isAssignableFrom(type)) {
			// Copy collections as ArrayList
			Collection<?> collection = (Collection<?>) sourceValue;
			Collection<Object> target = new ArrayList<Object>();
			return copyCollection(collection, target);
		} else {
			// Copy anything else as Bean, instantiate using default constructor
			Object valueCopy = instantiateBean(type);
			copyBean(sourceValue, valueCopy);
			return valueCopy;
		}
	}

	private static Object instantiateBean(Class<?> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(MSG, e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(MSG, e);
		}
	}

	private Collection<Object> copyCollection(Collection<?> source, Collection<Object> target) {
		for (Object value : source) {
			Object copy = copyBean(value);
			target.add(copy);
		}
		return target;
	}

	private Map<Object, Object> copyMap(Map<?, ?> source, Map<Object, Object> target) {
		for (Map.Entry<?, ?> entry: source.entrySet()) {
			Object keyCopy = copyBean(entry.getKey());
			Object valueCopy = copyBean(entry.getValue());
			target.put(keyCopy, valueCopy);
		}
		return target;
	}

	private static boolean isReadWrite(PropertyDescriptor prop) {
		return prop.getReadMethod() != null && prop.getWriteMethod() != null;
	}

	private static BeanInfo getBeanInfo(Object source) {
		Class<?> type = source.getClass();
		try {
			return Introspector.getBeanInfo(type);
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException("Unable to introspect bean " + type.getName());
		}
	}
}
