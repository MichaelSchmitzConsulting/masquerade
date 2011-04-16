package masquerade.sim.model;

/**
 * Context for evaluating requests
 */
public interface RequestContext {
	NamespaceResolver getNamespaceResolver();
	
	Converter getConverter();
}
