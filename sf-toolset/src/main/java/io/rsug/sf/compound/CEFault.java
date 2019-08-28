package io.rsug.sf.compound;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.LinkedList;

public class CEFault {
    public final String codeValue, reasonText, errorCode, errorMessage;

    CEFault(String codeValue, String reasonText, String errorCode, String errorMessage) {
        this.codeValue = codeValue;
        this.reasonText = reasonText;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static CEFault parseFault(XMLEventReader xr, LinkedList<QName> stack) throws XMLStreamException {
        int len = stack.size();
        String codeValue = null, reasonText = null, errorCode = null, errorMessage = null;
        while (xr.hasNext() && stack.size() >= len) {
            XMLEvent xe = xr.nextEvent();
            if (xe.isStartElement()) {
                StartElement se = xe.asStartElement();
                QName qn = se.getName();
                stack.push(qn);
            } else if (xe.isEndElement()) {
                EndElement ee = xe.asEndElement();
                QName qe = ee.getName();
                QName q = stack.pop();
                assert qe.equals(q);
            } else if (xe.isCharacters() && stack.size() > 1) {
                QName qn = stack.get(0);
                String v = xe.asCharacters().getData();
                if (XmlNames.matches(stack, XmlNames.Value, XmlNames.Code, XmlNames.Fault)) {
                    codeValue = v;
                } else if (XmlNames.matches(stack, XmlNames.Text, XmlNames.Reason, XmlNames.Fault)) {
                    reasonText = v;
                } else if (XmlNames.matches(stack, XmlNames.errorCode, XmlNames.SFWebServiceFault, XmlNames.Detail, XmlNames.Fault)) {
                    errorCode = v;
                } else if (XmlNames.matches(stack, XmlNames.errorMessage, XmlNames.SFWebServiceFault, XmlNames.Detail, XmlNames.Fault)) {
                    errorMessage = v;
                }
            }
        }
        assert codeValue != null || reasonText != null;
        return new CEFault(codeValue, reasonText, errorCode, errorMessage);
    }

    public String toString() {
        if (errorCode != null)
            return "CEFault(" + errorCode + "," + errorMessage + ")";
        else
            return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
    }

    public boolean isInvalidSession() {
        return "INVALID_SESSION".equals(errorCode);
    }
}
