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
import java.util.*;

public class CEAPI {
    static final Set<String> validActions = new HashSet<>();

    static {
        validActions.add("NO CHANGE");
        validActions.add("INSERT");
        validActions.add("CHANGE");
        validActions.add("DELETE");
    }

    public final Set<String> portletNames = new HashSet<>();
    final List<CEAPIField> fields = new LinkedList<>();
// https://help.sap.com/viewer/5bb9a5b997a843c88e769a105e4af4d4/1908/en-US/48960ce137694e25be5e728c852ae1f6.html
// KBA 2785228 - SuccessFactors Compound Employee API Country Specific fields - SuccessFactors API (SFAPI)

    public static String composeLoginRequest(String companyId, String uname, String password, boolean withSOAP, boolean withPreamble) throws XMLStreamException, IOException {
        XmlNames ex = new XmlNames();
        ex.createSfOutput(withSOAP, withPreamble);
        ex.put(XmlNames.login, null, false);
        ex.put(XmlNames.credential, null, false);
        ex.put(XmlNames.companyId, companyId, true);
        ex.put(XmlNames.username, uname, true);
        ex.put(XmlNames.password, password, true);
        ex.put(null, null, true);
        ex.put(null, null, true);
        return ex.closeDocument(withSOAP);
    }

    public static String composeQuery(String query, LinkedHashMap<String, String> params, boolean withSOAP, boolean withPreamble) throws XMLStreamException, IOException {
        XmlNames ex = new XmlNames();
        ex.createSfOutput(withSOAP, withPreamble);
        ex.put(XmlNames.query, null, false);
        ex.put(XmlNames.queryString, query, true);
        for (Map.Entry<String, String> param : params.entrySet()) {
            ex.put(XmlNames.param, null, false);
            ex.put(XmlNames.name, param.getKey(), true);
            ex.put(XmlNames.value, param.getValue(), true);
            ex.put(null, null, true);
        }
        ex.put(null, null, true);
        return ex.closeDocument(withSOAP);
    }

    public static String composeQueryMore(String querySessionId, boolean withSOAP, boolean withPreamble) throws XMLStreamException, IOException {
        XmlNames ex = new XmlNames();
        ex.createSfOutput(withSOAP, withPreamble);
        ex.put(XmlNames.queryMore, null, false);
        ex.put(XmlNames.querySessionId, querySessionId, true);
        ex.put(null, null, true);
        return ex.closeDocument(withSOAP);
    }

    public static String composeDescribeEx(boolean withSOAP, boolean withPreamble) throws XMLStreamException, IOException {
        XmlNames ex = new XmlNames();
        ex.createSfOutput(withSOAP, withPreamble);
        ex.put(XmlNames.describeSFObjectsEx, null, false);
        ex.put(XmlNames.type, "CompoundEmployee", true);
        ex.put(null, null, true);
        return ex.closeDocument(withSOAP);
    }

    public void loadMetadata(Reader rd) throws XMLStreamException {
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        xmlif.setProperty(XMLInputFactory.IS_COALESCING, true);
        xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        XMLEventReader xr = xmlif.createXMLEventReader(rd);
        LinkedList<QName> stack = new LinkedList<>();
        boolean typeIsCompoundEmployee = false;
        CEAPIField field = null;
        while (xr.hasNext()) {
            XMLEvent xe = xr.nextEvent();
            if (xe.isStartElement()) {
                StartElement se = xe.asStartElement();
                QName qn = se.getName();
                stack.push(qn);
                if (typeIsCompoundEmployee && XmlNames.matches(stack, XmlNames.field, XmlNames.result, XmlNames.describeSFObjectsExResponse)) {
                    field = new CEAPIField();
                }
            } else if (xe.isEndElement()) {
                EndElement ee = xe.asEndElement();
                QName qe = ee.getName();
                if (typeIsCompoundEmployee && XmlNames.matches(stack, XmlNames.field, XmlNames.result, XmlNames.describeSFObjectsExResponse)) {
                    fields.add(field);
                    field = null;
                }
                QName q = stack.pop();
                assert qe.equals(q);
            } else if (xe.isCharacters() && stack.size() > 1) {
                QName qn = stack.get(0);
                String v = xe.asCharacters().getData();
                if (XmlNames.matches(stack, XmlNames.type, XmlNames.result, XmlNames.describeSFObjectsExResponse)) {
                    typeIsCompoundEmployee = v.equals("compoundemployee");
                } else if (typeIsCompoundEmployee && XmlNames.matches(stack, XmlNames.supportedOperator, XmlNames.supportedOperators, XmlNames.field, XmlNames.result)) {
                    assert field != null;
                    field.supportedOperators.add(v);
                } else if (typeIsCompoundEmployee && XmlNames.matches(stack, null, XmlNames.label, XmlNames.field, XmlNames.result)) {
                    assert field != null;
                    if (XmlNames.value.equals(qn)) {
                        field.labelValue = v;
                    } else if (XmlNames.locale.equals(qn)) {
                        field.labelLocale = v;
                    } else if (XmlNames.mime_type.equals(qn)) {
                        field.labelMimeType = v;
                    } else
                        throw new XMLStreamException("Unknown tag: " + qn);
                } else if (typeIsCompoundEmployee && XmlNames.matches(stack, XmlNames.id, XmlNames.picklist, XmlNames.field, XmlNames.result)) {
                    assert field != null;
                    field.picklistId = v;
                } else if (typeIsCompoundEmployee && XmlNames.matches(stack, null, XmlNames.field, XmlNames.result)) {
                    assert field != null;
                    if (XmlNames.name.equals(qn)) {
                        field.name = v;
                    } else if (XmlNames.maxlength.equals(qn)) {
                        field.maxLength = Integer.parseInt(v);
                    } else if (XmlNames.dataType.equals(qn)) {
                        field.dataType = v;
                    } else if (!XmlNames.supportedOperators.equals(qn)) {
                        boolean b = true;
                        if ("false".equals(v))
                            b = false;
                        else if (!"true".equals(v))
                            throw new XMLStreamException("Unknown value " + v + " for element " + qn);
                        field.properties.put(qn.getLocalPart(), b);
                    }
                }
            }
        }
        assert stack.size() == 0;
        // detect portlets
        for (CEAPIField f : fields) {
            String name = f.name;
            if (name.endsWith("/action")) {
                String[] ns = name.split("/");
                for (int i = 0; i < ns.length - 1; i++) {
                    if (ns[i] != null && !ns[i].isEmpty()) portletNames.add(ns[i]);
                }
            }
        }
    }

    public void showMetadataGraph() {
        assert portletNames.size() > 0;

    }

    boolean isPortlet(QName qn, SFPortlet parent) {
        //TODO add SFPortlet check
        return portletNames.contains(qn.getLocalPart());
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder(1024 * 1024);
        for (CEAPIField f : fields) {
            sb.append(f.toString()).append("\n");
        }
        return sb.toString();
    }
}
