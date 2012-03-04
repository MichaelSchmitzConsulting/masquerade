package masquerade.sim.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ResponseCallback;
import masquerade.sim.model.ResponseDestination;
import masquerade.sim.model.SimulationRunner;

public final class SendTestRequestActionImpl implements SendTestRequestAction {
	
	private SimulationRunner simulationRunner;
	
	private final static class ByteArrayResponseDestination implements ResponseDestination {
		private final ByteArrayOutputStream responseOutput;
		
		public ByteArrayResponseDestination(ByteArrayOutputStream responseOutput) {
			this.responseOutput = responseOutput;
		}
		
		@Override
		public OutputStream getResponseOutputStream() {
			return responseOutput;
		}

		@Override
		public void sendIntermediateResponse(ResponseCallback callback) throws IOException {
			responseOutput.write("--Intermediate Response Starts--".getBytes());
			callback.withResponse(responseOutput);
			responseOutput.write("--Intermediate Response Ends--".getBytes());
		}
	}
	
	public SendTestRequestActionImpl(SimulationRunner simulationRunner) {
		this.simulationRunner = simulationRunner;
	}

	@Override 
	public String onSendTestRequest(Channel channel, Object content) throws Exception {
		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
		ResponseDestination responseDestination = new ByteArrayResponseDestination(responseOutput);
		
		simulationRunner.runSimulation(responseDestination, channel.getName(), "Request Test UI", content, new Date());
		
		return new String(responseOutput.toByteArray());
	}
}