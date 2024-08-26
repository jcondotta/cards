package com.blitzar.cards.exception;

public class ResourceNotFoundException extends RuntimeException{

    private Object rejectedIdentifier;

    public ResourceNotFoundException(String message, Object rejectedIdentifier) {
        super(message);
        this.rejectedIdentifier = rejectedIdentifier;
    }

    public Object getRejectedIdentifier() {
        return rejectedIdentifier;
    }
}
