package masquerade.sim.model.impl.step;

import java.io.InputStream;

import masquerade.sim.model.FileType;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.commons.io.IOUtils;

public class ScriptFileStep extends AbstractScriptedStep {

	private static final StatusLog log = StatusLogger.get(ScriptFileStep.class);
	
	private String scriptFileName = "";

	private transient String scriptContent;
	
	public ScriptFileStep(String name) {
		super(name);
	}

	/**
	 * @return the scriptFileName
	 */
	public String getScriptFileName() {
		return scriptFileName;
	}

	/**
	 * @param scriptFileName the scriptFileName to set
	 */
	public void setScriptFileName(String scriptFileName) {
		this.scriptFileName = scriptFileName;
	}

	@Override
	public void execute(SimulationContext context) throws Exception {
		InputStream is = context.load(FileType.SCRIPT, scriptFileName);
		if (is != null) {
			scriptContent = IOUtils.toString(is);
			super.execute(context);
		} else {		
			log.error("Unable to load script " + scriptFileName);
		}
	}

	@Override
	protected String getScript() {
		return scriptContent;
	}

	@Override
	protected String getScriptLanguage() {
		if (scriptFileName.endsWith(".groovy")) {
			return "groovy";
		} else if (scriptFileName.endsWith(".rb")) {
			return "jruby";
		} else if (scriptFileName.endsWith(".js")) {
			return "JavaScript";
		} else {
			return null;
		}
	}
}
