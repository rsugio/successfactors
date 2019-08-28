package io.rsug.sf.compound;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;

public class XmlNames {
    static final String nsSF = "urn:sfobject.sfapi.successfactors.com";
    static final QName sfobject = new QName(nsSF, "sfobject");
    static final QName result = new QName(nsSF, "result");
    static final QName previous = new QName(nsSF, "previous");
    static final QName created_on_timestamp = new QName(nsSF, "created_on_timestamp");
    static final QName last_modified_on = new QName(nsSF, "last_modified_on");
    static final QName seq_number = new QName(nsSF, "seq_number");
    static final QName log = new QName(nsSF, "log");
    static final QName log_item = new QName(nsSF, "log_item");

    static final QName numResults = new QName(nsSF, "numResults");
    static final QName hasMore = new QName(nsSF, "hasMore");
    static final QName querySessionId = new QName(nsSF, "querySessionId");
    static final QName id = new QName(nsSF, "id");
    static final QName type = new QName(nsSF, "type");
    static final QName execution_timestamp = new QName(nsSF, "execution_timestamp");
    static final QName version_id = new QName(nsSF, "version_id");

    static final QName describeSFObjectsExResponse = new QName(nsSF, "describeSFObjectsExResponse");
    static final QName field = new QName(nsSF, "field");
    static final QName name = new QName(nsSF, "name");
    static final QName label = new QName(nsSF, "label");
    static final QName value = new QName(nsSF, "value");
    static final QName locale = new QName(nsSF, "locale");
    static final QName mime_type = new QName(nsSF, "mime-type");
    static final QName picklist = new QName(nsSF, "picklist");
    static final QName maxlength = new QName(nsSF, "maxlength");
    static final QName dataType = new QName(nsSF, "dataType");

    static final QName supportedOperators = new QName("supportedOperators");
    static final QName supportedOperator = new QName("supportedOperator");

    static final QName login = new QName(nsSF, "login");
    static final QName credential = new QName(nsSF, "credential");
    static final QName companyId = new QName(nsSF, "companyId");
    static final QName username = new QName(nsSF, "username");
    static final QName password = new QName(nsSF, "password");

    static final QName sessionId = new QName(nsSF, "sessionId");
    static final QName msUntilPwdExpiration = new QName(nsSF, "msUntilPwdExpiration");
    static final QName loginResponse = new QName(nsSF, "loginResponse");
    static final QName error2 = new QName(nsSF, "error");
    static final QName errorCode2 = new QName(nsSF, "errorCode");
    static final QName errorMessage2 = new QName(nsSF, "errorMessage");
    static final QName query = new QName(nsSF, "query");
    static final QName queryMore = new QName(nsSF, "queryMore");
    static final QName queryString = new QName(nsSF, "queryString");
    static final QName param = new QName(nsSF, "param");
    static final QName describeSFObjectsEx = new QName(nsSF, "describeSFObjectsEx");

    static final String nsSOAP = "http://www.w3.org/2003/05/soap-envelope";
    static final QName Envelope = new QName(nsSOAP, "Envelope");
    static final QName Header = new QName(nsSOAP, "Header");
    static final QName Body = new QName(nsSOAP, "Body");

    static final QName Fault = new QName(nsSOAP, "Fault");
    static final QName Code = new QName(nsSOAP, "Code");
    static final QName Value = new QName(nsSOAP, "Value");
    static final QName Reason = new QName(nsSOAP, "Reason");
    static final QName Text = new QName(nsSOAP, "Text");
    static final QName Detail = new QName(nsSOAP, "Detail");
    static final String nsSFFault = "urn:fault.sfapi.successfactors.com";
    static final QName SFWebServiceFault = new QName(nsSFFault, "SFWebServiceFault");
    static final QName errorCode = new QName(nsSFFault, "errorCode");
    static final QName errorMessage = new QName(nsSFFault, "errorMessage");

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
