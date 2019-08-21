package io.rsug.sf;

import io.rsug.sf.compound.CEAPI;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SFHost {
    public final String companyId;
    private final URI apiHost;
    public URI endpointSFAPI;
    public URI endpointOData2;
    public URI endpointOData4;
    // Compound Employee
    private boolean ce = false;
    private String unameCE = null, pwdCE = null;
    // BizX OData
    private boolean odata = false;
    private String unameOData = null, pwdOData = null;

    public SFHost(String companyId, URI apiHost) {
        assert companyId != null && !companyId.isEmpty() && !companyId.isBlank();
        assert apiHost != null;
        assert apiHost.getScheme().equals("https");
        String hn = apiHost.getHost();
        assert hn.startsWith("api") && (hn.contains("sap") || hn.contains("sf") || hn.contains("success"));
        assert apiHost.isAbsolute();
        this.companyId = companyId;
        this.apiHost = apiHost;
    }

    public SFHost setCE(String user, String password) {
        assert user != null && !user.isEmpty();
        assert password != null && !password.isEmpty();
        ce = true;
        unameCE = user;
        pwdCE = password;
        endpointSFAPI = apiHost.resolve("/sfapi/v1/soap12");
        return this;
    }

    public SFHost setOData(String user, String password) {
        assert user != null && !user.isEmpty();
        assert password != null && !password.isEmpty();
        odata = true;
        unameOData = user;
        pwdOData = password;
        endpointOData2 = apiHost.resolve("/odata/v2");
        endpointOData4 = apiHost.resolve("/odata/v4");
        return this;
    }

    public String ceLoginRequest(boolean withSOAP, boolean withPreamble) throws IOException, XMLStreamException {
        assert ce;
        return CEAPI.composeLoginRequest(companyId, unameCE, pwdCE, withSOAP, withPreamble);
    }

    public String getBasicAuthOData() {
        assert odata;
        String s = unameOData + "@" + companyId + ":" + pwdOData;
        Base64.Encoder enc = Base64.getEncoder();
        return "Basic " + enc.encodeToString(s.getBytes(StandardCharsets.US_ASCII));
    }
}
