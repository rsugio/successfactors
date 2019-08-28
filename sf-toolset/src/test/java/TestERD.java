import io.rsug.sf.odata.EdmxChecker;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.*;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.core.edm.provider.EdmxProvider;
import org.junit.Test;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

class A {
    private final static String xmlnsy = "http://www.yworks.com/xml/graphml";
    private final static String xmlns = "http://graphml.graphdrawing.org/xmlns";
    private final XMLStreamWriter xw;
    private final StringWriter w = new StringWriter(1024);

    A() throws XMLStreamException {
        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
//        xmlof.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        xw = xmlof.createXMLStreamWriter(w);
        xw.setPrefix("y", xmlnsy);
        xw.setDefaultNamespace(xmlns);

        xw.writeStartDocument("UTF-8", "1.0");
        xw.writeCharacters("\n");
        xw.writeStartElement("graphml");
        xw.writeNamespace("y", xmlnsy);
        xw.writeDefaultNamespace(xmlns);
        xw.writeCharacters("\n");
    }

    private void putY(String name, boolean empty, String... attrValues) throws XMLStreamException {
        assert name != null;
        if (empty)
            xw.writeEmptyElement(name);
        else
            xw.writeStartElement(name);
        for (int i = 0; i < attrValues.length; i = i + 2) {
            xw.writeAttribute(attrValues[i], attrValues[i + 1]);
        }
    }

    void feed(EdmxProvider edmx) throws XMLStreamException, ODataException {
        putY("key", true, "attr.name", "Description", "attr.type", "string", "for", "graph", "id", "d0");
        xw.writeCharacters("\n");

        xw.writeEmptyElement("key");
        xw.writeAttribute("attr.name", "description");
        xw.writeAttribute("attr.type", "string");
        xw.writeAttribute("for", "node");
        xw.writeAttribute("id", "d5");
        xw.writeCharacters("\n");

        xw.writeEmptyElement("key");
        xw.writeAttribute("for", "node");
        xw.writeAttribute("id", "d6");
        xw.writeAttribute("yfiles.type", "nodegraphics");
        xw.writeCharacters("\n");

        xw.writeEmptyElement("key");
        xw.writeAttribute("for", "graphml");
        xw.writeAttribute("id", "d7");
        xw.writeAttribute("yfiles.type", "resources");
        xw.writeCharacters("\n");

        xw.writeStartElement("graph");
        xw.writeAttribute("edgedefault", "directed");
        xw.writeAttribute("id", "G");
        xw.writeEmptyElement("data");
        xw.writeAttribute("key", "d0");
        xw.writeCharacters("\n");

        int n = 0;
        for (Schema schema : edmx.getSchemas()) {
            for (EntityContainer ec : schema.getEntityContainers()) {
                for (EntitySet es : ec.getEntitySets()) {
                    String name = es.getName();
                    FullQualifiedName typeName = es.getEntityType();
//                    ComplexType ctype = edmx.getComplexType(typeName);
                    EntityType etype = edmx.getEntityType(typeName);
                    assert etype != null : typeName;
                    xw.writeEmptyElement("node");
                    xw.writeAttribute("id", "n" + n);
//                    xw.writeEmptyElement("data");
//                    xw.writeAttribute("key", "d5");
                    xw.writeStartElement("data");
                    xw.writeAttribute("key", "d6");

                    xw.writeStartElement("y", "GenericNode", xmlnsy);
                    xw.writeAttribute("configuration", "com.yworks.entityRelationship.small_entity");

                    xw.writeEmptyElement("y", "Geometry", xmlnsy);
                    xw.writeAttribute("height", "40.0");
                    xw.writeAttribute("width", "80.0");
                    xw.writeAttribute("x", "0.0");
                    xw.writeAttribute("y", "0.0");

                    xw.writeStartElement("y", "NodeLabel", xmlnsy);
//                    xw.writeAttribute("configuration", "com.yworks.entityRelationship.label.name");
                    xw.writeCharacters(name);
                    xw.writeEndElement();
                    xw.writeCharacters("\n");

                    xw.writeEndElement();
                    xw.writeCharacters("\n");
                    xw.writeEndElement();
                    xw.writeCharacters("\n");

                    n++;

                }
                for (AssociationSet o : ec.getAssociationSets()) {

                }
            }
        }

        xw.writeEndElement();   // graph
        xw.writeStartElement("data");
        xw.writeAttribute("key", "d7");
        xw.writeEmptyElement("y", "Resources", xmlnsy);
        xw.writeEndElement();   // graph
    }

    StringBuffer finish() throws XMLStreamException, IOException {
        xw.writeEndElement(); // graphml
        xw.writeCharacters("\n");
        xw.writeEndDocument();
        w.close();
        return w.getBuffer();
    }
}

public class TestERD {
    @Test
    public void parseOData() throws Exception {
        InputStream is = getClass().getResourceAsStream("/ODataV2-Metadata.xml");
        EdmxChecker poc = new EdmxChecker(is);
        is.close();

        A a = new A();
        a.feed(poc.edmx);
        StringBuffer b = a.finish();
        Writer w = Files.newBufferedWriter(Paths.get("tmp/erd.graphml"), StandardCharsets.UTF_8);
        w.append(b);
        w.close();
    }

}
