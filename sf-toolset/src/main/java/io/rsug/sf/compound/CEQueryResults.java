package io.rsug.sf.compound;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CEQueryResults {
    public final List<SFObject> objects = new ArrayList<>();
    public long numResults = 0;
    public boolean hasMore = false;
    public String querySessionId = null;

    public static CEQueryResults parseFromXml(Reader rd, CEAPI ceapi) throws XMLStreamException {
        if (ceapi == null) throw new XMLStreamException("CEAPI not set");
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        xmlif.setProperty(XMLInputFactory.IS_COALESCING, true);
        xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        XMLEventReader xr = xmlif.createXMLEventReader(rd);
        CEQueryResults rex = new CEQueryResults();
        LinkedList<QName> stack = new LinkedList<>();
        SFObject sfo = null;
        CELogItem log_item = null;
        while (xr.hasNext()) {
            XMLEvent xe = xr.nextEvent();
            if (xe.isStartElement()) {
                StartElement se = xe.asStartElement();
                QName qn = se.getName();
                stack.push(qn);
                if (XmlNames.matches(stack, XmlNames.sfobject, XmlNames.result)) {
                    assert sfo == null;
                    sfo = new SFObject();
                } else if (sfo != null && ceapi.isPortlet(qn, null)) {
                    assert sfo.hier == null;
                    sfo.hier = new SFPortlet(qn, null);
                    sfo.hier.parse(xr, 0, stack, ceapi);
                } else if (XmlNames.matches(stack, XmlNames.log_item, XmlNames.log)) {
                    log_item = new CELogItem();
                }
            } else if (xe.isEndElement()) {
                EndElement ee = xe.asEndElement();
                QName qe = ee.getName();
                if (XmlNames.sfobject.equals(qe) && XmlNames.matches(stack, XmlNames.sfobject, XmlNames.result)) {
                    rex.objects.add(sfo);
                    sfo = null;
                } else if (XmlNames.matches(stack, XmlNames.log_item, XmlNames.log)) {
                    assert log_item != null;
                    assert sfo != null && sfo.logs != null;
                    sfo.logs.add(log_item);
                    log_item = null;
                }
                QName q = stack.pop();
                assert qe.equals(q);
            } else if (xe.isCharacters() && stack.size() > 1) {
                QName qn0 = stack.get(0);
                QName qn1 = stack.get(1);
                String v = xe.asCharacters().getData();
                if (XmlNames.result.equals(qn1)) {
                    if (XmlNames.numResults.equals(qn0)) {
                        rex.numResults = Long.parseLong(v);
                    } else if (XmlNames.hasMore.equals(qn0)) {
                        if (v.equals("true"))
                            rex.hasMore = true;
                        else if (v.equals("false"))
                            rex.hasMore = false;
                        else
                            throw new XMLStreamException("Unknown boolean value: " + v);
                    } else if (XmlNames.querySessionId.equals(qn0)) {
                        rex.querySessionId = v;
                    }
                } else if (XmlNames.sfobject.equals(qn1)) {
                    assert sfo != null;
                    if (XmlNames.id.equals(qn0)) {
                        sfo.id = v;
                    } else if (XmlNames.type.equals(qn0)) {
                        sfo.type = v;
                    } else if (XmlNames.execution_timestamp.equals(qn0)) {
                        sfo.execution_timestamp = Instant.parse(v);
                    } else if (XmlNames.version_id.equals(qn0)) {
                        sfo.version_id = v;
                    } else {
                        assert false : "Unknown SFObject field:" + qn0;
                        throw new XMLStreamException("Unknown SFObject field:" + qn0);
                    }
                } else if (XmlNames.matches(stack, null, XmlNames.log_item, XmlNames.log)) {
                    assert log_item != null;
                    log_item.put(qn0, v);
                }
            }
        } // end while xr.hasNext()
        xr.close();
        return rex;
    }

    public String toString() {
        return "SFCEQueryResults(numResults=" + numResults + ",hasMore=" + hasMore + ",querySessionId=" + querySessionId + ")";
    }

    public String composeQueryMore(CEAPI ceapi, boolean withSOAP, boolean withPreamble) throws IOException, XMLStreamException {
        if (hasMore)
            return CEAPI.composeQueryMore(querySessionId, withSOAP, withPreamble);
        else
            return "";
    }
}
