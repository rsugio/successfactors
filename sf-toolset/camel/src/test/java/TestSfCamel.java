import io.rsug.sf.SFHost;
import io.rsug.sf.camel.SFCECamelHelper;
import io.rsug.sf.compound.CEAPI;
import org.junit.Test;

import java.net.URI;

public class TestSfCamel {
    @Test
    public void metadata() throws Exception {
        SFHost sfHost = new SFHost("ooogruppakT2", new URI("https://api18preview.sapsf.com"))
                .setCE("ilya.kuznetsov@sap.com", "1qaz#EDC");
//        SFHost sfHost = new SFHost("companyId", new URI("https://api12preview.sapsf.com"))
//                .setCE("login", "password");
        CEAPI ceapi = new CEAPI();
        SFCECamelHelper helper = new SFCECamelHelper(sfHost, ceapi);
        helper.describeEx();
    }
}
