import io.rsug.sf.SFHost;
import io.rsug.sf.compound.CEAPI;
import io.rsug.sf.compound.CEQueryResults;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class TestSf {
    Reader ceMetadata = null, ceQueryResponse = null, ceLoginResponse = null;

    @Before
    public void initialize() {
        ceMetadata = new InputStreamReader(getClass().getResourceAsStream("/CompoundEmployee-Metadata.xml"), StandardCharsets.UTF_8);
        ceQueryResponse = new InputStreamReader(getClass().getResourceAsStream("/CompoundEmployee-QueryResponse-OK.xml"), StandardCharsets.UTF_8);
        ceLoginResponse = new InputStreamReader(getClass().getResourceAsStream("/CompoundEmployee-LoginResponse-OK.xml"), StandardCharsets.UTF_8);
    }

    @After
    public void finish() throws IOException {
        ceMetadata.close();
        ceQueryResponse.close();
        ceLoginResponse.close();
    }

    @Test
    public void sfHost() throws URISyntaxException, IOException, XMLStreamException {
        SFHost hostsf = new SFHost("ooo", new URI("https://api18.sapsf.com/../123/.."));
        hostsf.setCE("aaaa", "bbb");
        assertEquals("https://api18.sapsf.com/sfapi/v1/soap12", hostsf.endpointSFAPI.toString());
        hostsf.setOData("aaaa", "bbb");
        assertEquals("https://api18.sapsf.com/odata/v2", hostsf.endpointOData2.toString());
        assertEquals("https://api18.sapsf.com/odata/v4", hostsf.endpointOData4.toString());
        String s1 = hostsf.ceLoginRequest(false, false);
        assertEquals("<SF:login xmlns:SOAP=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:SF=\"urn:sfobject.sfapi.successfactors.com\"><SF:credential><SF:companyId>ooo</SF:companyId><SF:username>aaaa</SF:username><SF:password>bbb</SF:password></SF:credential></SF:login>", s1);
        String s2 = hostsf.ceLoginRequest(true, true);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOAP:Envelope xmlns:SOAP=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:SF=\"urn:sfobject.sfapi.successfactors.com\"><SOAP:Header></SOAP:Header><SOAP:Body><SF:login><SF:credential><SF:companyId>ooo</SF:companyId><SF:username>aaaa</SF:username><SF:password>bbb</SF:password></SF:credential></SF:login></SOAP:Body></SOAP:Envelope>", s2);
    }


    @Test
    public void metadata() throws XMLStreamException, IOException {
        CEAPI ceapi = new CEAPI();
        ceapi.loadMetadata(ceMetadata);
        ceMetadata.close();
    }

    @Test
    public void payload() throws XMLStreamException, IOException {
        CEAPI ceapi = new CEAPI();
        ceapi.loadMetadata(ceMetadata);
        CEQueryResults.parseFromXml(ceQueryResponse, ceapi);
        ceMetadata.close();
    }

    @Test
    public void loginRequest() throws IOException, XMLStreamException {
        assertEquals("<SOAP:Envelope xmlns:SOAP=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:SF=\"urn:sfobject.sfapi.successfactors.com\"><SOAP:Header></SOAP:Header><SOAP:Body><SF:login><SF:credential><SF:companyId>companyId</SF:companyId><SF:username>user</SF:username><SF:password>password</SF:password></SF:credential></SF:login></SOAP:Body></SOAP:Envelope>",
                CEAPI.composeLoginRequest("companyId", "user", "password", true, false));
    }

    @Test
    public void loginResponse() throws XMLStreamException {
        //TODO add error samples and change CEAPI.parseLoginResponse result
        assertEquals("7ED171D9E00649642FF24FFA1329BBCD.eu-abef740eb", CEAPI.parseLoginResponse(ceLoginResponse));
    }

    @Test
    public void composeQueries() throws IOException, XMLStreamException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("param1", "value1");
        map.put("param0", "value0");
        assertEquals("<SOAP:Envelope xmlns:SOAP=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:SF=\"urn:sfobject.sfapi.successfactors.com\"><SOAP:Header></SOAP:Header><SOAP:Body><SF:query><SF:queryString>SELECT 123 FROM AAA</SF:queryString><SF:param><SF:name>param1</SF:name><SF:value>value1</SF:value></SF:param><SF:param><SF:name>param0</SF:name><SF:value>value0</SF:value></SF:param></SF:query></SOAP:Body></SOAP:Envelope>",
                CEAPI.composeQuery("SELECT 123 FROM AAA", map, true, false));
        assertEquals("<SOAP:Envelope xmlns:SOAP=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:SF=\"urn:sfobject.sfapi.successfactors.com\"><SOAP:Header></SOAP:Header><SOAP:Body><SF:describeSFObjectsEx><SF:type>CompoundEmployee</SF:type></SF:describeSFObjectsEx></SOAP:Body></SOAP:Envelope>",
                CEAPI.composeDescribeEx(true, false));
    }
}
