package com.backendlld.movieticketbookingplatform.exception;

public class SeatsNotAvailableException extends RuntimeException {
    public SeatsNotAvailableException(String message) {
        super(message);
    }
}