import io.rsug.sf.compound.CEAPI;
import io.rsug.sf.odata.EdmxChecker;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainCompound {
    public static void main(String[] args) throws Exception {
        Reader rd;
        InputStream is;
        CEAPI ceapi = new CEAPI();
        EdmxChecker poc = null;
        if (args.length == 0) {
            System.out.println("Usage: MainCompound compoundMetadata.xml odata2meta.xml");
        } else {
            Path md = Paths.get(args[0]);
            if (Files.isRegularFile(md) && Files.size(md) > 0) {
                rd = Files.newBufferedReader(md, StandardCharsets.UTF_8);
                ceapi.loadMetadata(rd);
                rd.close();
            } else {
                System.err.println("Cannot read metadata file: " + md);
            }
        }
        if (args.length > 1) {
            Path md2 = Paths.get(args[1]);
            if (Files.isRegularFile(md2) && Files.size(md2) > 0) {
                is = Files.newInputStream(md2);
                poc = new EdmxChecker(is);
                is.close();
            }
        }
        poc.analyze();
//        for (int i = 2; i < args.length; i++) {
//            Path ex = Paths.get(args[i]);
//            rd = Files.newBufferedReader(ex, StandardCharsets.UTF_8);
//            CEQueryResults qr = CEQueryResults.parseFromXml(rd, ceapi);
//            rd.close();
//            for (SFObject sfo : qr.objects) {
//                sfo.checkJobInformationIssue();
//            }
//        }
    }
}
