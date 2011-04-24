package masquerade.sim.status;

import java.util.logging.Level;
import java.util.logging.Logger;

import masquerade.sim.status.Status.Severity;
import masquerade.sim.util.ClassUtil;

/**
 * {@link StatusLog} implementation delegating status logs to a 
 * {@link Logger} and to a {@link StatusRepositoryImpl}.
 */
public class StatusLogger implements StatusLog {

	private final static StatusRepository repo = new StatusRepositoryImpl();
	
	public static StatusLog get(Class<?> type) {
		return get(ClassUtil.unqualifiedName(type));
	}

	public static StatusLog get(String name) {
		return new StatusLogger(name);
	}

	private String name;
	private Logger delegate;

	private StatusLogger(String name) {
		this.name = name;
		this.delegate = Logger.getLogger(name);
	}

	@Override
	public void info(String msg) {
		repo.addStatus(name, msg, Severity.INFO);
		delegate.log(Level.INFO, msg);
	}

	@Override
	public void warning(String msg) {
		repo.addStatus(name, msg, Severity.WARNING);
		delegate.log(Level.WARNING, msg);
	}

	@Override
	public void warning(String msg, Throwable ex) {
		repo.addStatus(name, msg, ex, Severity.WARNING);
		delegate.log(Level.WARNING, msg, ex);
	}

	@Override
	public void error(String msg) {
		repo.addStatus(name, msg, Severity.ERROR);
		delegate.log(Level.SEVERE, msg);
	}

	@Override
	public void error(String msg, Throwable ex) {
		repo.addStatus(name, msg, ex, Severity.ERROR);
		delegate.log(Level.SEVERE, msg, ex);
	}
}
