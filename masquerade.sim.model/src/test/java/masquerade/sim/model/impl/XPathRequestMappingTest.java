package masquerade.sim.model.impl;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;

import masquerade.sim.model.ConverterStub;
import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.RequestContext;
import masquerade.sim.util.DomUtil;

import org.junit.Test;
import org.w3c.dom.Document;

public class XPathRequestMappingTest {

	/**
	 * Test method for {@link masquerade.sim.model.impl.XPathRequestMapping#matches(org.w3c.dom.Document, masquerade.sim.model.RequestContext)}.
	 */
	@Test
	public void testMatches() {
		XPathRequestMapping mapping = new XPathRequestMapping("testMapping");
		mapping.setMatchXpath("/bla");
		
		Map<String, String> nsMap = Collections.emptyMap();
		
		NamespaceResolver ns = createStrictMock(NamespaceResolver.class);
		expect(ns.getKnownNamespaces()).andReturn(nsMap);
		replay(ns);
		
		Document doc = DomUtil.parse("<bla/>");
		RequestContext context = new RequestContextImpl(ns, new ConverterStub());
		
		assertTrue(mapping.matches(doc, context));
		assertTrue(mapping.matches(doc, context));
		
		verify(ns);
	}

}
