package io.rsug.sf.compound;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class SFPortlet {
    public final SFPortlet parent;
    public final String name;
    public final LinkedHashMap<String, String> values = new LinkedHashMap<>();
    public final LinkedHashMap<String, String> previous = new LinkedHashMap<>();
    public final List<SFPortlet> children = new ArrayList<>();
    public int deep = -1, seq_number = 0, _internal_seqno = 0;
    public Instant created_on_timestamp = null, last_modified_on = null;
    public LinkedHashMap<String, SFPortlet> links = new LinkedHashMap<>();

    SFPortlet(QName qn, SFPortlet parent) {
        this.name = qn.getLocalPart();
        this.parent = parent;
    }

    void parse(XMLEventReader xr, int deep, LinkedList<QName> stack, CEAPI ceapi) throws XMLStreamException {
        this.deep = deep;
        int len = stack.size();
        assert values.isEmpty() && previous.isEmpty();
        while (xr.hasNext() && stack.size() >= len) {
            XMLEvent o = xr.nextEvent();
            if (o.isEndElement()) {
                QName e = o.asEndElement().getName();
                QName z = stack.pop();
                assert e.equals(z);
                if (XmlNames.previous.equals(e)) {
                    if (!previous.containsKey(stack.get(0).getLocalPart()))
                        previous.put(stack.get(0).getLocalPart(), null);
                }
            } else if (o.isCharacters()) {  //COALESCING is required
                QName q = stack.get(0);
                String x = q.getLocalPart();
                String v = o.asCharacters().getData();
                if (XmlNames.previous.equals(q)) {
                    previous.put(stack.get(1).getLocalPart(), v);
                } else if (!ceapi.isPortlet(q, this)) {
                    String z = values.get(x);
                    z = z != null ? z + v : v;
                    values.put(x, z);
                    try {
                        if (XmlNames.created_on_timestamp.equals(q))
                            created_on_timestamp = Instant.parse(z);
                        else if (XmlNames.last_modified_on.equals(q))
                            last_modified_on = Instant.parse(z);
                        else if (XmlNames.seq_number.equals(q))
                            seq_number = Integer.parseInt(z);
                    } catch (DateTimeParseException dte) {
                        String s = "Invalid on portlet '" + name + "'. Unknown portlet? Error is: " + dte.getMessage();
                        throw new DateTimeParseException(s, dte.getParsedString(), dte.getErrorIndex());
                    }
                }
            } else if (o.isStartElement()) {
                StartElement se = o.asStartElement();
                QName q = se.getName();
                stack.push(q);
                if (ceapi.isPortlet(q, this) && !XmlNames.previous.equals(q)) {
                    SFPortlet sfp = new SFPortlet(q, this);
                    sfp._internal_seqno = children.size();
                    sfp.parse(xr, deep + 1, stack, ceapi);
                    this.children.add(sfp);
                }
            }
        }
    }

    public String prettyPrint() {
        assert deep >= 0;
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(deep).append("]").append(name).append(" ").append(values).append(",prev=").append(previous);
        for (SFPortlet it : children) {
            sb.append("\n").append(it.prettyPrint());
        }
        return sb.toString();
    }

    public void getPortlets(String name, List<SFPortlet> lst) {
        for (SFPortlet x : children) {
            if (x.name.equals(name)) lst.add(x);
            x.getPortlets(name, lst);
        }
    }
} // portlet parser

