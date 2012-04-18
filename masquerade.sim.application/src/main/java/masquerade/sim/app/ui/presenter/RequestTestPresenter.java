package masquerade.sim.app.ui.presenter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import masquerade.sim.app.ui.Refreshable;
import masquerade.sim.app.ui.view.RequestTestView;
import masquerade.sim.app.ui.view.RequestTestView.RequestTestViewCallback;
import masquerade.sim.model.ResponseCallback;
import masquerade.sim.model.ResponseDestination;
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.repository.ModelRepository;

public class RequestTestPresenter implements RequestTestViewCallback, Refreshable {

	private final SimulationRunner simulationRunner;
	private final ModelRepository modelRepository;
	private final RequestTestView view;

	public RequestTestPresenter(SimulationRunner simulationRunner, ModelRepository modelRepository, RequestTestView requestTestView) {
		this.simulationRunner = simulationRunner;
		this.modelRepository = modelRepository;
		this.view = requestTestView;
	}
	
	@Override 
	public String onSendTestRequest(String channelId, String content) throws Exception {
		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
		ResponseDestination responseDestination = new ByteArrayResponseDestination(responseOutput);
		
		simulationRunner.runSimulation(responseDestination, channelId, "Request Test UI", content, new Date());
		
		return new String(responseOutput.toByteArray());
	}
	
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

	@Override
	public void onRefresh() {
		view.setChannels(modelRepository.getAllChannelIds());
	}
}
