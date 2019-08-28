import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Before;
import org.junit.Test;

public class TestCamelErrorsExceptions {
    DefaultCamelContext ctx;
    ProducerTemplate producer;
    ConsumerTemplate consumer;

    @Before
    public void init() {
        ctx = new DefaultCamelContext();
        ctx.setTracing(true);
        producer = ctx.createProducerTemplate();
        consumer = ctx.createConsumerTemplate();
    }

    @Test
    public void hypothesis1() throws Exception {
        ctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                        .to("log:DEBUG");
            }
        });
        ctx.start();


//        while (!ctx.isStopped()) {
//            Thread.sleep(10);
//        }
        ctx.stop();

    }
}
