package com.khulnasoft.coverage.plugin;

public class CoverError extends Exception {
    public CoverError(String message, Throwable cause) {
        super(message, cause);
    }

    public CoverError(String message) {
    }
}
