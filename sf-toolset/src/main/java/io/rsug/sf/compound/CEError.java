package io.rsug.sf.compound;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.LinkedList;

public class CEError {
    public final String errorCode, errorMessage;

    public CEError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static CEError parseError(XMLEventReader xr, LinkedList<QName> stack) throws XMLStreamException {
        int len = stack.size();
        String errorCode = null, errorMessage = null;
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
                if (XmlNames.matches(stack, XmlNames.errorCode2, XmlNames.error2)) {
                    errorCode = v;
                } else if (XmlNames.matches(stack, XmlNames.errorMessage2, XmlNames.error2)) {
                    errorMessage = v;
                }
            }
        }
        assert errorCode != null || errorMessage != null;
        return new CEError(errorCode, errorMessage);
    }
}
