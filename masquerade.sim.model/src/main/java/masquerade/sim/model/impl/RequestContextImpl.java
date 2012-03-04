package masquerade.sim.model.impl;

import masquerade.sim.model.Converter;
import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.RequestContext;

/**
 * Default implementation of {@link RequestContext}
 */
public class RequestContextImpl implements RequestContext {

	private final NamespaceResolver namespaceResolver;
	private final Converter converter;

	public RequestContextImpl(NamespaceResolver namespaceResolver, Converter converter) {
		this.namespaceResolver = namespaceResolver;
		this.converter = converter;
	}

	@Override
	public NamespaceResolver getNamespaceResolver() {
		return namespaceResolver;
	}

	@Override
	public Converter getConverter() {
		return converter;
	}
}
