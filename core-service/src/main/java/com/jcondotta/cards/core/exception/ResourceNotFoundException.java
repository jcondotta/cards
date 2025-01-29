package com.jcondotta.cards.core.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException{

    private final UUID rejectedIdentifier;

    public ResourceNotFoundException(String message, UUID rejectedIdentifier) {
        super(message);
        this.rejectedIdentifier = rejectedIdentifier;
    }

    public Object getRejectedIdentifier() {
        return rejectedIdentifier;
    }
}
