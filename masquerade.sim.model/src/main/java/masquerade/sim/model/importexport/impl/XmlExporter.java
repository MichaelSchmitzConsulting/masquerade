package masquerade.sim.model.importexport.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import masquerade.sim.model.importexport.Exporter;
import masquerade.sim.util.XStreamMarshallerFactory;

public class XmlExporter implements Exporter {
	private static final String XML_DECL = "<?xml version=\"1.0\"?>\n";

	private final XStreamMarshallerFactory factory = new XStreamMarshallerFactory();

	@Override
	public void exportModelObject(Object modelObject, OutputStream stream) throws IOException {
		marshal(modelObject, stream);
	}

	private void marshal(Object object, OutputStream stream) {
		PrintStream printStream = new PrintStream(stream);

		writeHeader(printStream);
		factory.createXStream().toXML(object, printStream);

		printStream.flush();
		printStream.close();
	}

	private static void writeHeader(PrintStream out) {
		out.println(XML_DECL);
	}
}
