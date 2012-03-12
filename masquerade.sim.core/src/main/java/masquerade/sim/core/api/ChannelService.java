package masquerade.sim.core.api;

import static masquerade.sim.core.api.RequestTemplate.pathMatches;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.importexport.Importer;
import masquerade.sim.model.repository.ModelRepository;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * API Service for manipulating channels
 * 
 * Supported functions:
 * /channel/id/123   POST, DELETE
 * /channel/all      DELETE
 */
@Component(name="channelServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class ChannelService extends HttpServlet {
	private static final String ID = "/id";
	private final static Pattern CHANNEL_ID_PATTERN = Pattern.compile(ID + "/(.*)");
	private static final String ALL = "/all";

	@Reference ModelRepository modelRepository;
	@Reference Importer importer;
	@Reference ChannelListenerRegistry channelListenerRegistry;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (ID.equals(req.getPathInfo())) {
			ChannelTemplate channelTemplate = new ChannelTemplate(channelListenerRegistry);
			channelTemplate.insert(importer, req.getInputStream(), false);
		} else {
			ResponseTemplate.errorResponse(resp, "Invalid request URL");
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ChannelTemplate channelTemplate = new ChannelTemplate(channelListenerRegistry);
		
		String id;
		if ((id = pathMatches(req.getPathInfo(), CHANNEL_ID_PATTERN)) != null) {
			channelTemplate.deleteChannel(modelRepository, id);
		} else if (ALL.equals(req.getPathInfo())) {
			channelTemplate.deleteChannels(modelRepository);
		} else {
			ResponseTemplate.errorResponse(resp, "Invalid request URL");
		}
	}	
}
