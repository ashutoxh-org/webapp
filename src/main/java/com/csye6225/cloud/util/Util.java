package com.csye6225.cloud.util;

import org.springframework.http.HttpHeaders;

public class Util {

    private Util() {

    }

    /**
     * Get standard no-cache or related headers
     * @return HttpHeaders
     */
    public static HttpHeaders getRequiredHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        //For compatibility with HTTP/1.0 clients
        headers.add("Pragma", "no-cache");
        //Instructs the browser to block requests that try to "sniff" the MIME type
        headers.add("X-Content-Type-Options", "nosniff");

        return headers;
    }

}
