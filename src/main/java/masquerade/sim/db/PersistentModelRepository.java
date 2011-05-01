package masquerade.sim.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import masquerade.sim.channel.jms.ActiveMqConnectionFactoryProvider;
import masquerade.sim.channel.jms.ConnectionFactoryProvider;
import masquerade.sim.model.Channel;
import masquerade.sim.model.CopyRequestToResponseStep;
import masquerade.sim.model.NamespacePrefix;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;
import masquerade.sim.model.Settings;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.model.impl.DefaultJmsChannel;
import masquerade.sim.model.impl.FileChannel;
import masquerade.sim.model.impl.FtpChannel;
import masquerade.sim.model.impl.HttpChannel;
import masquerade.sim.model.impl.HttpStandaloneChannel;
import masquerade.sim.model.impl.SequenceScript;
import masquerade.sim.model.impl.SoapFaultStep;
import masquerade.sim.model.impl.WebSphereMqJmsChannel;
import masquerade.sim.model.impl.XPathRequestIdProvider;
import masquerade.sim.model.impl.XPathRequestMapping;
import masquerade.sim.model.impl.step.AddResponseElementStep;
import masquerade.sim.model.impl.step.GroovyScriptStep;
import masquerade.sim.model.impl.step.JavaScriptStep;
import masquerade.sim.model.impl.step.LoadTemplateStep;
import masquerade.sim.model.impl.step.LoadTemplateToVariableStep;
import masquerade.sim.model.impl.step.RenameXmlNodeStep;
import masquerade.sim.model.impl.step.RubyScriptStep;
import masquerade.sim.model.impl.step.ScriptFileStep;
import masquerade.sim.model.impl.step.SetResponseContentStep;
import masquerade.sim.model.impl.step.WaitStep;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.constraints.UniqueFieldValueConstraintViolationException;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

public class PersistentModelRepository implements ModelRepository {

	private static Map<Class<?>, Collection<Class<?>>> modelImpls = new HashMap<Class<?>, Collection<Class<?>>>();
	
	static {
		// Channels
		declareModelImplementation(Channel.class, HttpChannel.class);
		declareModelImplementation(Channel.class, HttpStandaloneChannel.class);
		declareModelImplementation(Channel.class, DefaultJmsChannel.class);
		declareModelImplementation(Channel.class, WebSphereMqJmsChannel.class);
		declareModelImplementation(Channel.class, FileChannel.class);
		declareModelImplementation(Channel.class, FtpChannel.class);
		
		// Scripts
		declareModelImplementation(Script.class, SequenceScript.class);
		
		// Request ID Providers
		declareModelImplementation(RequestIdProvider.class, XPathRequestIdProvider.class);
		
		// Request Mapping
		declareModelImplementation(RequestMapping.class, XPathRequestMapping.class);
		
		// Simulation Steps
		declareModelImplementation(SimulationStep.class, AddResponseElementStep.class);
		declareModelImplementation(SimulationStep.class, CopyRequestToResponseStep.class);
		declareModelImplementation(SimulationStep.class, WaitStep.class);
		declareModelImplementation(SimulationStep.class, LoadTemplateStep.class);
		declareModelImplementation(SimulationStep.class, LoadTemplateToVariableStep.class);
		declareModelImplementation(SimulationStep.class, JavaScriptStep.class);
		declareModelImplementation(SimulationStep.class, GroovyScriptStep.class);
		declareModelImplementation(SimulationStep.class, RubyScriptStep.class);
		declareModelImplementation(SimulationStep.class, ScriptFileStep.class);
		declareModelImplementation(SimulationStep.class, SetResponseContentStep.class);
		declareModelImplementation(SimulationStep.class, SoapFaultStep.class);
		declareModelImplementation(SimulationStep.class, RenameXmlNodeStep.class);
		
		// Namespace prefix
		declareModelImplementation(NamespacePrefix.class, NamespacePrefix.class);
		
		// JMS Connection Factory Provider
		declareModelImplementation(ConnectionFactoryProvider.class, ActiveMqConnectionFactoryProvider.class);
	}

	private ObjectContainer db;
	
	public PersistentModelRepository(ObjectContainer db) {
		this.db = db;
	}

	/**
	 * Checks if the object of the given type can be created without violating any
	 * constraints.
	 */
	@Override
	public boolean canCreate(Class<?> type, String name, StringBuilder errorMsg) {
		if (Channel.class.isAssignableFrom(type)) {
			if (getChannelByName(name) != null) {
				errorMsg.append("Channel with name " + name + " already exists");
				return false;
			}
		}
			
		return true;
	}

	/**
	 * Checks if the object of the given type can be delete without violating any
	 * constraints.
	 */
	@Override
	public boolean canDelete(Object obj, StringBuilder errorMsg) {
		Class<?> type = obj.getClass();
		// Request mapping still referenced in channel?
		if (RequestMapping.class.isAssignableFrom(type)) {
			Collection<Channel> channels = getChannels();
			for (Channel channel : channels) {
				if (channel.getRequestMappings().contains(obj)) {
					errorMsg.append("This request mapping is still active for channel " + channel.getName());
					return false;
				}
			}
		// Response script still referenced by request mapping?
		} else if (Script.class.isAssignableFrom(type)) {
			Collection<RequestMapping<?>> mappings = getRequestMappings();
			for (RequestMapping<?> mapping : mappings) {
				if (mapping.getScript() == obj) {
					errorMsg.append("This script is still active in request mapping " + mapping.getName());
					return false;
				}
			}
		}
		
		// No constraints violated - allow deletion
		return true;
	}

	@Override
	public void notifyCreate(Object value) {
		checkConstraintsOnCreate(value);
		notifyUpdated(value);
	}

	@Override
	public void notifyUpdated(Object modelObject) {
		db.store(modelObject);
		db.commit();
	}

	@Override
	public void notifyDelete(Object obj) {
		db.delete(obj);
		db.commit();
	}

	@Override
	public void endSession() {
		db.close();
	}
	
	@Override
	public List<Channel> getChannels() {
		return db.query(Channel.class);
	}
	
	@Override
	public <T> Collection<? extends T> getByName(Class<? extends T> baseType, String usedName) {
		Query query = db.query();
		query.constrain(baseType);
		query.descend("name").constrain(usedName);
		ObjectSet<T> result = query.execute();
		return result;
	}

	@Override
	public Channel getChannelByName(final String name) {
		return singleResult(new Predicate<Channel>() {
			@Override
            public boolean match(Channel channel) {
	            return name.equals(channel.getName());
            }
		});
	}

	@Override
	public List<RequestMapping<?>> getRequestMappings() {
		Query query = startQuery();
		query.constrain(RequestMapping.class);
		return query.execute();
	}

	@Override
	public Collection<RequestIdProvider<?>> getRequestIdProviders() {
		Query query = startQuery();
		query.constrain(RequestIdProvider.class);
		return query.execute();
	}

	@Override
	public Collection<Script> getScripts() {
		return startQuery(Script.class);
	}
	
	@Override
	public Settings getSettings() {
		ObjectSet<Settings> result = db.query(Settings.class);
		return result.size() > 0 ? result.get(0) : new Settings();
	}

	@Override
	public <T> Collection<T> getAll(Class<T> type) {
		return startQuery(type);
    }

	@Override
	public Collection<Class<?>> getModelImplementations(Class<?> modelBaseType) {
		synchronized (modelImpls) {
			return modelImpls.get(modelBaseType);
		}
	}
	
	private void checkConstraintsOnCreate(Object value) {
		// Unique channel name constraint is checked here as DB4O does not support
		// unique constraints on getters, or on inherited fields.
		if (value instanceof Channel) {
			Channel channel = (Channel) value;
			if (getChannelByName(channel.getName()) != null) {
				throw new UniqueFieldValueConstraintViolationException(Channel.class.getName(), "name");
			}
		}
	}

	private <T> T singleResult(Predicate<T> predicate) {
		ObjectSet<T> result = startQuery(predicate);
		return result.size() > 0 ? result.get(0) : null;
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
		return db.query();
	}
	
	private <T> ObjectSet<T> startQuery(Class<T> type) {
		return db.query(type);
	}

	private <T> ObjectSet<T> startQuery(Predicate<T> pred) {
		return db.query(pred);
	}
}

