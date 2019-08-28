package io.rsug.sf.compound;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.Reader;
import java.util.LinkedList;

public class CEResponse {
    public final int httpRc;
    public final String httpResponseText, httpMime;
    public CEFault fault; // must be final too but initialized later

    public CEResponse(int httpRc, String httpResponseText, String httpMime) {
        this.httpRc = httpRc;
        this.httpResponseText = httpResponseText;
        this.httpMime = httpMime;
    }

    public CEResponse(int httpRc, String httpResponseText, String httpMime, Reader rd) throws XMLStreamException {
        this.httpRc = httpRc;
        this.httpResponseText = httpResponseText;
        this.httpMime = httpMime;

        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        xmlif.setProperty(XMLInputFactory.IS_COALESCING, true);
        xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        XMLEventReader xr = xmlif.createXMLEventReader(rd);
        LinkedList<QName> stack = new LinkedList<>();
        fault = CEFault.parseFault(xr, stack);
        assert stack.size() == 0;
    }
}
