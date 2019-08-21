import io.rsug.sf.compound.CEAPI;
import io.rsug.sf.compound.CEQueryResults;
import io.rsug.sf.compound.SFObject;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainCompound {
    public static void main(String[] args) throws IOException, XMLStreamException {
        if (args.length == 0) {
            System.out.println("Usage: MainCompound metadata.xml [sample.xml]");
        } else {
            CEAPI ceapi = new CEAPI();
            Path md = Paths.get(args[0]);
            Reader rd;
            if (Files.isRegularFile(md) && Files.size(md) > 0) {
                rd = Files.newBufferedReader(md, StandardCharsets.UTF_8);
                ceapi.loadMetadata(rd);
                rd.close();
            } else {
                System.err.println("Cannot read metadata file: " + md);
            }
            for (int i = 1; i < args.length; i++) {
                Path ex = Paths.get(args[i]);
                rd = Files.newBufferedReader(ex, StandardCharsets.UTF_8);
                CEQueryResults qr = CEQueryResults.parseFromXml(rd, ceapi);
                rd.close();
//                System.out.println(qr);
                for (SFObject sfo : qr.objects) {
                    sfo.checkJobInformationIssue();
//                    System.out.println(sfo.prettyPrint());
                }
            }
        }
    }
}
