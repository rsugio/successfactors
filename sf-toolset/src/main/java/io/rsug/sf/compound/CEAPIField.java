package io.rsug.sf.compound;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

class CEAPIField {
    String name = null, labelValue = null, labelLocale = null, labelMimeType = null, dataType = null, picklistId = null;
    final LinkedHashMap<String, Boolean> properties = new LinkedHashMap<>();
    int maxLength = 0;
    final List<String> supportedOperators = new LinkedList<>();

    public String toString() {
        return "" + name + ":" + dataType + "";
    }
}
