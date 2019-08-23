import io.rsug.sf.SFHost;
import io.rsug.sf.camel.SFCECamelHelper;
import io.rsug.sf.compound.CEAPI;

import java.net.URI;

public class MainCamel {
    public static void main(String[] args) throws Exception {
        SFHost sfHost = new SFHost(args[0], new URI(args[1]));
        sfHost.setCE(args[2], args[3]);
        CEAPI ceapi = new CEAPI();
        SFCECamelHelper helper = new SFCECamelHelper(sfHost, ceapi);
        helper.describeEx();
    }
}
