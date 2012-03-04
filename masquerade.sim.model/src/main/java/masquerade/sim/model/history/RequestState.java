package masquerade.sim.model.history;

/**
 * State of received requests
 */
public enum RequestState {
	/** Request is being processed */
	Pending,
	/** An exception occured while processing the request */
	Error, 
	/** No response definition could be found for this request */
	NoMatch, 
	/** A response to the request was succesfully sent */
	Success
}
