package com.khulnasoft.cover.plugin;

public class CoverError extends Exception {
    public CoverError(String message, Throwable cause) {
        super(message, cause);
    }

    public CoverError(String message) {
    }
}
