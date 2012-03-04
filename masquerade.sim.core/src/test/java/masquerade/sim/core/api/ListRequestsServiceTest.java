package masquerade.sim.core.api;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import masquerade.sim.model.history.HistoryEntry;
import masquerade.sim.model.history.RequestHistory;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link ListRequestsService}
 */
public class ListRequestsServiceTest {
	private static final String FIRST_PART_CONTENT = "abc\ndef\rxyz\n<bla/>";
	private static final String PREFIX = "foo";
	private ListRequestsService service;
	private RequestHistory requestHistory;

	@Test
	public void testListRequests() throws Exception {
		List<HistoryEntry> list = new ArrayList<HistoryEntry>();
		list.add(new HistoryEntryStub(FIRST_PART_CONTENT.getBytes()));
		list.add(new HistoryEntryStub("def".getBytes()));
		expect(requestHistory.getRequestsForIdPrefix(PREFIX)).andReturn(list);
		replay(requestHistory);
		
		// Test request listing
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		service.listRequests(PREFIX, buffer);
		
		verify(requestHistory);
		
		// Verify content
		MimeMultipart multipart = new MimeMultipart(new ByteArrayDataSource(buffer.toByteArray(), "multipart/mixed"));
		assertTrue(multipart.isComplete());
		assertEquals(2, multipart.getCount());
		
		BodyPart firstPart = multipart.getBodyPart(0);
		assertEquals(FIRST_PART_CONTENT, firstPart.getContent());
		assertEquals(HistoryEntryStub.TIMESTAMP, firstPart.getHeader(RequestHistoryService.HEADER_REQUEST_TIMESTAMP)[0]);
		assertEquals(HistoryEntryStub.CHANNEL_NAME, firstPart.getHeader(RequestHistoryService.HEADER_CHANNEL_NAME)[0]);
		assertEquals(HistoryEntryStub.CLIENT_INFO, firstPart.getHeader(RequestHistoryService.HEADER_CLIENT_INFO)[0]);
		assertEquals(String.valueOf(HistoryEntryStub.PROCESSING_PERIOD), firstPart.getHeader(RequestHistoryService.HEADER_PROCESSING_PERIOD)[0]);
		assertEquals("0", firstPart.getHeader(RequestHistoryService.HEADER_RECEIVE_TIME)[0]);
		assertEquals("0", firstPart.getHeader(RequestHistoryService.HEADER_REQUEST_TIME)[0]);
		assertEquals(HistoryEntryStub.REQUEST_ID, firstPart.getHeader(ListRequestsService.HEADER_REQUEST_ID)[0]);
		
		assertEquals("def", multipart.getBodyPart(1).getContent());
	}
	
	@Before
	public void setUp() {
		service = new ListRequestsService();
		requestHistory = createStrictMock(RequestHistory.class);
		service.requestHistory = requestHistory;
	}
}
