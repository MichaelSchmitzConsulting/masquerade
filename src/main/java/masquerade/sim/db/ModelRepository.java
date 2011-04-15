package masquerade.sim.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import masquerade.sim.CreateListener;
import masquerade.sim.DeleteListener;
import masquerade.sim.UpdateListener;
import masquerade.sim.model.Channel;
import masquerade.sim.model.CopyRequestToResponseStep;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.ResponseSimulation;
import masquerade.sim.model.Script;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.model.impl.FileChannel;
import masquerade.sim.model.impl.HttpChannel;
import masquerade.sim.model.impl.JmsChannel;
import masquerade.sim.model.impl.SequenceScript;
import masquerade.sim.model.impl.XPathRequestIdProvider;
import masquerade.sim.model.impl.XPathRequestMapping;
import masquerade.sim.model.impl.step.AddResponseElementStep;
import masquerade.sim.model.impl.step.GroovyScriptStep;
import masquerade.sim.model.impl.step.JavaScriptStep;
import masquerade.sim.model.impl.step.LoadTemplateStep;
import masquerade.sim.model.impl.step.LoadTemplateToVariableStep;
import masquerade.sim.model.impl.step.RubyScriptStep;
import masquerade.sim.model.impl.step.WaitStep;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

public class ModelRepository implements UpdateListener, DeleteListener, CreateListener {

	private static Map<Class<?>, Collection<Class<?>>> modelImpls = new HashMap<Class<?>, Collection<Class<?>>>();
	
	static {
		// Channels
		declareModelImplementation(Channel.class, HttpChannel.class);
		declareModelImplementation(Channel.class, JmsChannel.class);
		declareModelImplementation(Channel.class, FileChannel.class);
		
		// Scripts
		declareModelImplementation(Script.class, SequenceScript.class);
		
		// Request ID Providers
		declareModelImplementation(RequestIdProvider.class, XPathRequestIdProvider.class);
		
		// Request Mapping
		declareModelImplementation(RequestMapping.class, XPathRequestMapping.class);

		// Response Simulation
		declareModelImplementation(ResponseSimulation.class, ResponseSimulation.class);
		
		// Simulation Steps
		declareModelImplementation(SimulationStep.class, AddResponseElementStep.class);
		declareModelImplementation(SimulationStep.class, CopyRequestToResponseStep.class);
		declareModelImplementation(SimulationStep.class, WaitStep.class);
		declareModelImplementation(SimulationStep.class, LoadTemplateStep.class);
		declareModelImplementation(SimulationStep.class, LoadTemplateToVariableStep.class);
		declareModelImplementation(SimulationStep.class, JavaScriptStep.class);
		declareModelImplementation(SimulationStep.class, GroovyScriptStep.class);
		declareModelImplementation(SimulationStep.class, RubyScriptStep.class);
	}

	// Common monitor entered by all threads accessing the model to synchronize
	// model state across threads. Entered upon updating the model in on of the
	// notify methods, and when creating the repository for subsequent reads.
	private Object modelMonitor = new Object();
	
	private ObjectContainer db;
	
	public ModelRepository(ObjectContainer db) {
		synchronized (modelMonitor) { 
			this.db = db;
		}
	}

	@Override
	public void notifyCreate(Object value) {
		notifyUpdated(value);
	}

	@Override
	public void notifyUpdated(Object modelObject) {
		synchronized (modelMonitor) {
			db.store(modelObject);
			db.commit();
		}
	}

	@Override
	public void notifyDelete(Object obj) {
		synchronized (modelMonitor) {
			db.delete(obj);
			db.commit();
		}
	}
	
	public boolean contains(Object obj) {
		synchronized (modelMonitor) {
			return db.ext().isStored(obj);
		}
	}

	public void endSession() {
		synchronized (modelMonitor) { 
			// db.close(); TODO: Separate between domain model (configuration) and processing logic in channels. Currently using shared global session
		}
	}
	
	public List<Channel> getChannels() {
		return db.query(Channel.class);
	}
	
	public Channel getChannelByName(final String name) {
		return singleResult(new Predicate<Channel>() {
			@Override
            public boolean match(Channel channel) {
	            return name.equals(channel.getName());
            }
		});
	}

	public List<RequestMapping<?>> getRequestMappings() {
		Query query = startQuery();
		query.constrain(RequestMapping.class);
		return query.execute();
	}

	public Collection<RequestMapping<?>> getRequestIdProviders() {
		Query query = startQuery();
		query.constrain(RequestIdProvider.class);
		return query.execute();
	}

	public Collection<ResponseSimulation> getResponseSimulations() {
		return startQuery(ResponseSimulation.class);
	}

	public Collection<Script> getScripts() {
		return startQuery(Script.class);
	}

	private <T> T singleResult(Predicate<T> predicate) {
		ObjectSet<T> result = startQuery(predicate);
		return result.size() > 0 ? result.get(0) : null;
    }

	public <T> Collection<T> getAll(Class<T> type) {
		return startQuery(type);
    }

	public Collection<Class<?>> getModelImplementations(Class<?> modelBaseType) {
		synchronized (modelImpls) {
			return modelImpls.get(modelBaseType);
		}
	}
	
	private static <T> void declareModelImplementation(Class<T> base, Class<? extends T> impl) {
		Collection<Class<?>> impls = modelImpls.get(base);
		if (impls == null) {
			impls = new LinkedHashSet<Class<?>>();
			modelImpls.put(base, impls);
		}
		impls.add(impl);
	}

	private Query startQuery() {
		synchronized (modelMonitor) {
			return db.query();
		}
	}
	
	private <T> ObjectSet<T> startQuery(Class<T> type) {
		synchronized (modelMonitor) {
			return db.query(type);
		}
	}

	private <T> ObjectSet<T> startQuery(Predicate<T> pred) {
		synchronized (modelMonitor) {
			return db.query(pred);
		}
	}
}

