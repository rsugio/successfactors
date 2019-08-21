package io.rsug.sf.compound;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;

public class XmlNames {
    static String nsSF = "urn:sfobject.sfapi.successfactors.com";
    //    static String nsSFault = "urn:fault.sfapi.successfactors.com";
    static QName sfobject = new QName(nsSF, "sfobject");
    static QName result = new QName(nsSF, "result");
    static QName previous = new QName(nsSF, "previous");
    static QName created_on_timestamp = new QName(nsSF, "created_on_timestamp");
    static QName last_modified_on = new QName(nsSF, "last_modified_on");
    static QName seq_number = new QName(nsSF, "seq_number");
    static QName log = new QName(nsSF, "log");
    static QName log_item = new QName(nsSF, "log_item");

    static QName numResults = new QName(nsSF, "numResults");
    static QName hasMore = new QName(nsSF, "hasMore");
    static QName querySessionId = new QName(nsSF, "querySessionId");
    static QName id = new QName(nsSF, "id");
    static QName type = new QName(nsSF, "type");
    static QName execution_timestamp = new QName(nsSF, "execution_timestamp");
    static QName version_id = new QName(nsSF, "version_id");

    static QName describeSFObjectsExResponse = new QName(nsSF, "describeSFObjectsExResponse");
    static QName field = new QName(nsSF, "field");
    static QName name = new QName(nsSF, "name");
    static QName label = new QName(nsSF, "label");
    static QName value = new QName(nsSF, "value");
    static QName locale = new QName(nsSF, "locale");
    static QName mime_type = new QName(nsSF, "mime-type");
    static QName picklist = new QName(nsSF, "picklist");
    static QName maxlength = new QName(nsSF, "maxlength");
    static QName dataType = new QName(nsSF, "dataType");

    static QName supportedOperators = new QName("supportedOperators");
    static QName supportedOperator = new QName("supportedOperator");

    static QName login = new QName(nsSF, "login");
    static QName credential = new QName(nsSF, "credential");
    static QName companyId = new QName(nsSF, "companyId");
    static QName username = new QName(nsSF, "username");
    static QName password = new QName(nsSF, "password");

    static QName sessionId = new QName(nsSF, "sessionId");
    static QName loginResponse = new QName(nsSF, "loginResponse");
    static QName query = new QName(nsSF, "query");
    static QName queryMore = new QName(nsSF, "queryMore");
    static QName queryString = new QName(nsSF, "queryString");
    static QName param = new QName(nsSF, "param");
    static QName describeSFObjectsEx = new QName(nsSF, "describeSFObjectsEx");

    static String nsSOAP = "http://www.w3.org/2003/05/soap-envelope";
    static QName Envelope = new QName(nsSOAP, "Envelope");
    static QName Header = new QName(nsSOAP, "Header");
    static QName Body = new QName(nsSOAP, "Body");
    XMLStreamWriter xw = null;
    StringWriter w = null;

    /**
     * Looks for matching pattern in stack
     *
     * @param stack  runtime stack
     * @param qnames null for any QName or exact value to compare
     * @return true if matches
     */
    static boolean matches(LinkedList<QName> stack, QName... qnames) {
        if (qnames.length > stack.size()) return false;
        boolean b = true;
        for (int i = 0; i < qnames.length && b; i++) {
            QName qn = qnames[i];
            if (qn != null) b = stack.get(i).equals(qn);
        }
        return b;
    }

    void createSfOutput(boolean withSOAP, boolean withPreamble) throws XMLStreamException {
        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        xmlof.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        w = new StringWriter(1024);
        xw = xmlof.createXMLStreamWriter(w);
        xw.setPrefix("SOAP", XmlNames.nsSOAP);
        xw.setPrefix("SF", XmlNames.nsSF);
        if (withPreamble) xw.writeStartDocument("UTF-8", "1.0");
        if (withSOAP) {
            put(XmlNames.Envelope, null, false);
            put(XmlNames.Header, null, true);
            put(XmlNames.Body, null, false);
        }
    }

    void put(QName qn, String text, boolean closeTag) throws XMLStreamException {
        assert xw != null;
        if (qn != null) xw.writeStartElement(qn.getNamespaceURI(), qn.getLocalPart());
        if (text != null) xw.writeCharacters(text);
        if (closeTag) xw.writeEndElement();
    }

    String closeDocument(boolean withSOAP) throws XMLStreamException, IOException {
        assert xw != null;
        if (withSOAP) {
            put(null, null, true);
            put(null, null, true);
        }
        xw.writeEndDocument();
        w.close();
        String s = w.toString();
        xw = null;
        w = null;
        return s;
    }

}
