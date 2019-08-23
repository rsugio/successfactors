package io.rsug.sf.compound;

public class CEResponse {
    public final int httpRc;
    public final String httpResponseText, httpMime;
    public CEFault fault; // must be final too but initialized later

    CEResponse(int httpRc, String httpResponseText, String httpMime) {
        this.httpRc = httpRc;
        this.httpResponseText = httpResponseText;
        this.httpMime = httpMime;
    }

}
