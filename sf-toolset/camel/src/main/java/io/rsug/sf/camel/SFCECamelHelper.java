package io.rsug.sf.camel;

import io.rsug.sf.SFHost;
import io.rsug.sf.compound.CEAPI;
import io.rsug.sf.compound.CEFault;
import io.rsug.sf.compound.CEResponse;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import javax.xml.stream.XMLStreamException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

class Routes extends RouteBuilder {
    final String https4;
    private final SFCECamelHelper helper;
    boolean finished = false;

    Routes(SFCECamelHelper helper) {
        this.helper = helper;
        https4 = helper.sfhost.endpointSFAPI.toString().replace("https://", "https4://");
    }

    @Override
    public void configure() throws Exception {
        from("direct:describeEx")
                .onException(HttpOperationFailedException.class)
                .maximumRedeliveries(0)
                .onWhen(exchange -> {
                    HttpOperationFailedException exe = exchange.getException(HttpOperationFailedException.class);
                    CEResponse resp;
                    CEFault fault = null;
                    try {
                        resp = new CEResponse(exe.getStatusCode(), exe.getStatusText()
                                , exe.getResponseHeaders().get("Content-Type")
                                , new StringReader(exe.getResponseBody()));
                        fault = resp.fault;
                    } catch (XMLStreamException e) {
                        throw new IllegalArgumentException("Compound Employee API response is not well-formed XML");
                    }
                    assert fault != null;
                    if (fault.isInvalidSession()) {

                    } else {

                    }
                    finished = true;
                    return true;
                })
                .handled(true)
//                .continued(true)
                .setBody(constant(null))
                .end()

                .process(ex -> {
                    ex.getOut().setHeaders(helper.postHeaders);
                    ex.getOut().setBody(CEAPI.composeDescribeEx(true, false));
                })
                .to(https4)
                .choice()
                .when(simple("${header.CamelHttpResponseCode} > 299"))
                .process(ex -> System.out.println("error " + ex))
                .otherwise()
                .process(ex -> System.out.println("good " + ex))
                .end()
                .process(ex -> finished = true);
    }
}

public class SFCECamelHelper {
    final CEAPI ceapi;
    final SFHost sfhost;
    final CookieStore cookieStore;
    final LinkedHashMap<String, Object> postHeaders;
    final CamelContext ctx;

    public SFCECamelHelper(SFHost sfhost, CEAPI ceapi) {
        assert sfhost != null;
        assert sfhost.companyId != null;
        this.sfhost = sfhost;
        this.ceapi = ceapi;
        cookieStore = new BasicCookieStore();

        ctx = new DefaultCamelContext();
        ctx.setTracing(true);
        HttpComponent httpComponent = ctx.getComponent("https4", HttpComponent.class);
        httpComponent.setCookieStore(cookieStore);

        postHeaders = new LinkedHashMap<>();
        postHeaders.put("User-Agent", "Camel" + ctx.getVersion());
        postHeaders.put(Exchange.HTTP_METHOD, "POST");
        postHeaders.put(Exchange.CONTENT_TYPE, "application/soap+xml");
    }

    public void describeEx() throws Exception {
        Routes dex = new Routes(this);
        ctx.addRoutes(dex);
        ctx.start();
        ProducerTemplate sender = ctx.createProducerTemplate();
        Map<String, Object> headers = new LinkedHashMap<>();
        Object o = sender.requestBodyAndHeaders("direct:describeEx", "", headers);
        while (!dex.finished) {
            Thread.sleep(50L);
        }
        ctx.stop();
        System.out.println(o);
        System.out.println(headers);


    }
}
