package masquerade.sim.converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import masquerade.sim.model.Converter;
import masquerade.sim.util.DomUtil;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CompoundConverter implements Converter {

	@Override
	public boolean canConvert(Class<?> from, Class<?> to) {
		// TODO: Sensible converter implementation
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object value, Class<T> to) {
		if (value == null) {
			return null;
		}
		
		Class<?> from = value.getClass();
		if (to.isAssignableFrom(from)) {
			return (T) value;
		}
		
		// TODO: Hierarchical converter registry
		
		if (Document.class.isAssignableFrom(from)) {
			Document doc = (Document) value;
			if (to == String.class) {
				return (T) DomUtil.asString(doc);
			} else if (to == Element.class) {
				return (T) doc.getDocumentElement();
			} else if (to == InputStream.class) { 
				String str = DomUtil.asString(doc);
				return (T) new ByteArrayInputStream(str.getBytes());
			}
		} else if (String.class.isAssignableFrom(from)) {
			String str = (String) value;
			if (to == Document.class) {
				return (T) DomUtil.parse(str);
			} else if (to == Element.class) {
				return (T) DomUtil.parse(str).getDocumentElement();
			} else if (to == InputStream.class) { 
				return (T) new ByteArrayInputStream(str.getBytes());
			}
		} else if (InputStream.class.isAssignableFrom(from)) {
			InputStream is = (InputStream) value;
			if (to == String.class){ 
				return (T) toString(is);
			} else if (to == Document.class) {
				return (T) DomUtil.parse(is);
			} else if (to == Element.class) {
				Document doc = DomUtil.parse(is);
				return (T) doc.getDocumentElement();
			}
		}
		
		return null;
	}

	private String toString(InputStream value) {
		try {
			return (String) IOUtils.toString(value);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot convert input stream to string", e);
		}
	}

}
