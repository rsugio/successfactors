package io.rsug.sf.compound;

import javax.xml.namespace.QName;

public class CELogItem {
    String per_person_uuid, person_id, person_id_external, node_name, field_name, xpath, code, severity, message_text;

    CELogItem() {
    }

    public String toString() {
        return "" + severity + " " + code + "\t" + message_text;
    }

    void put(QName name, String v) {
        assert name != null;
        assert v != null;
        String s = name.getLocalPart();
        if ("per_person_uuid".equals(s)) {
            this.per_person_uuid = v;
        } else if ("person_id".equals(s)) {
            this.person_id = v;
        } else if ("person_id_external".equals(s)) {
            this.person_id_external = v;
        } else if ("node_name".equals(s)) {
            this.node_name = v;
        } else if ("field_name".equals(s)) {
            this.field_name = v;
        } else if ("xpath".equals(s)) {
            this.xpath = v;
        } else if ("code".equals(s)) {
            this.code = v;
        } else if ("severity".equals(s)) {
            this.severity = v;
        } else if ("message_text".equals(s)) {
            this.message_text = v;
        } else {
            assert false : "Unknown log_item field: " + name;
        }
    }
}
