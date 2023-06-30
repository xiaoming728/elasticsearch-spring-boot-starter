package com.echola.elasticsearch.autoconfigure.core;

import org.elasticsearch.rest.RestStatus;

/**
 * @author liuchunming
 * @date 2023-06-30
 */
public class EsearchErrorException extends RuntimeException {

    private String error;

    public EsearchErrorException(String message) {
        super(message);
    }

    public EsearchErrorException(RestStatus status, String error, Throwable cause) {
        super(status.name(), cause);
        this.error = error;
    }

    public EsearchErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        String s = super.toString();
        return (error != null) ? (s + ": " + error) : s;
    }
}
