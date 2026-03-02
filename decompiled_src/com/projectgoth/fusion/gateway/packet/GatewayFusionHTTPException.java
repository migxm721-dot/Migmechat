/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

class GatewayFusionHTTPException
extends Exception {
    private static final String SEPARATOR = ";";
    private String url;
    private int httpResponseCode;

    public GatewayFusionHTTPException(String message, String url, int httpResponseCode) {
        super(message);
        this.url = url;
        this.httpResponseCode = httpResponseCode;
    }

    public int getHttpResponseCode() {
        return this.httpResponseCode;
    }

    public String toString() {
        return this.httpResponseCode + SEPARATOR + this.url + SEPARATOR + this.getMessage();
    }
}

