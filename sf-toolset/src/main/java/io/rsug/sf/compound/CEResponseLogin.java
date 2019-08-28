package io.rsug.sf.compound;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Reader;
import java.util.LinkedList;

public class CEResponseLogin extends CEResponse {
    public final String sessionId;
    public final long msUntilPwdExpiration;
    public final CEError error;

    public CEResponseLogin(Reader rd, int responseCode, String responseText, String mime) throws XMLStreamException {
        super(responseCode, responseText, mime);
        Object[] rez = parseLoginResponse(rd);
        assert rez != null && rez.length == 4;
        this.fault = (CEFault) rez[0];
        this.sessionId = (String) rez[1];
        String x = (String) rez[2];
        if (x != null && !x.isEmpty())
            this.msUntilPwdExpiration = Long.parseLong(x);
        else
            this.msUntilPwdExpiration = -1L;
        this.error = (CEError) rez[3];
    }

    static Object[] parseLoginResponse(Reader rd) throws XMLStreamException {
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        xmlif.setProperty(XMLInputFactory.IS_COALESCING, true);
        xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        XMLEventReader xr = xmlif.createXMLEventReader(rd);
        LinkedList<QName> stack = new LinkedList<>();
        String sessionId = null, msUntilPwdExpiration = null;
        CEFault fault = null;
        CEError error = null;

        while (xr.hasNext()) {
            XMLEvent xe = xr.nextEvent();
            if (xe.isStartElement()) {
                StartElement se = xe.asStartElement();
                QName qn = se.getName();
                stack.push(qn);
                if (XmlNames.Fault.equals(qn)) {
                    fault = CEFault.parseFault(xr, stack);
                } else if (XmlNames.error2.equals(qn)) {
                    error = CEError.parseError(xr, stack);
                }
            } else if (xe.isEndElement()) {
                EndElement ee = xe.asEndElement();
                QName qe = ee.getName();
                QName q = stack.pop();
                assert qe.equals(q);
            } else if (xe.isCharacters() && stack.size() > 1) {
                QName qn = stack.get(0);
                String v = xe.asCharacters().getData();
                if (XmlNames.matches(stack, XmlNames.sessionId, XmlNames.result, XmlNames.loginResponse)) {
                    sessionId = v;
                } else if (XmlNames.matches(stack, XmlNames.msUntilPwdExpiration, XmlNames.result, XmlNames.loginResponse)) {
                    msUntilPwdExpiration = v;
                }
            }
        }
        assert stack.size() == 0;
        return new Object[]{fault, sessionId, msUntilPwdExpiration, error};
    }
}
