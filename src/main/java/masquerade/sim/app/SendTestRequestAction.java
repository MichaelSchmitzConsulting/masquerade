package masquerade.sim.app;

import java.io.ByteArrayOutputStream;

import masquerade.sim.ApplicationContext;
import masquerade.sim.model.Channel;
import masquerade.sim.model.impl.SimulationRunnerImpl;
import masquerade.sim.ui.ActionListener;

public final class SendTestRequestAction implements ActionListener<Channel, String, Object> {
	private final ApplicationContext context;

	SendTestRequestAction(ApplicationContext context) {
		this.context = context;
	}

	@Override public String onAction(Channel channel, Object content) throws Exception {
		SimulationRunnerImpl runner = 
			new SimulationRunnerImpl(
				context.getRequestHistoryFactory(), 
				context.getConverter(), 
				context.getFileLoader(),
				context.getNamespaceResolver(),
				context.getConfigurationVariableHolder());
		
		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
		runner.runSimulation(responseOutput, channel.getName(), "Request Test UI", channel.getRequestMappings(), content);
		String response = new String(responseOutput.toByteArray());
		return response;
	}
}