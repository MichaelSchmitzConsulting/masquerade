package masquerade.sim.model;

/**
 * A simulation defines which script to execute and which ID to
 * assign to a received request. A simulation contains a selector 
 * that activates it depending on a request's content. Simulations 
 * are assigned to listeners on which they are active.
 * 
 * <p>Implementations must be safe for concurrent read access. The framework
 * ensures a happens-before relationship before passing mutated simulations
 * to clients. 
 */
public interface Simulation {
	String getId();
	
	RequestMapping<?> getSelector();
	
	RequestIdProvider<?> getRequestIdProvider();
	
	Script getScript();
	
	NamespaceResolver getNamespaceResolver();
}
