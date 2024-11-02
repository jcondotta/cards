package com.blitzar.cards.validation.security;

import java.util.regex.Pattern;

public class CommandInjectionPattern implements ThreatInputPattern {

    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
            "(;\\s*rm\\b)|" +                                  // Semicolon followed by 'rm' command
            "(&&\\s*ls\\b)|" +                                 // Double ampersand followed by 'ls' command
            "(\\|\\s*cat\\b)|" +                               // Pipe followed by 'cat' command
            Pattern.CASE_INSENSITIVE
    );


    @Override
    public boolean containsPattern(String value) {
        return COMMAND_INJECTION_PATTERN.matcher(value).find();
    }
}
