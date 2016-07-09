package com.github.gfranks.fitfam.data.api;

public class Endpoint {

    private String url;

    private Endpoint(String url) {
        this.url = url;
    }

    public static Endpoint newFixedEndpoint(String url) {
        return new Endpoint(url);
    }

    public String url() {
        return url;
    }
}
