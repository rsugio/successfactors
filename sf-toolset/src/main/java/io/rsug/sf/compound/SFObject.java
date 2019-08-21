package io.rsug.sf.compound;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class SFObject {
    public String id = null, type = null, version_id = null;
    public SFPortlet hier = null;
    public Instant execution_timestamp = null;
    public List<CELogItem> logs = new LinkedList<>();

    public String prettyPrint() {
        String hd = "SFObject(" + type + ",id=" + id + ")\n";
        if (hier != null)
            hd += hier.prettyPrint();
        return hd;
    }

    public void checkJobInformationIssue() {
        if (hier == null) return;
        List<SFPortlet> lst1 = new LinkedList<>();
        List<SFPortlet> lst2 = new LinkedList<>();
        hier.getPortlets("job_information", lst1);
        hier.getPortlets("job_event_information", lst2);
        assert lst2.size() >= lst1.size();
        for (SFPortlet ji : lst1) {
            if (ji.seq_number > 1) {
                System.out.println(ji.prettyPrint());
                for (SFPortlet ei : lst2) {
                    System.out.println(ei.prettyPrint());
                }
            }
        }
    }
}