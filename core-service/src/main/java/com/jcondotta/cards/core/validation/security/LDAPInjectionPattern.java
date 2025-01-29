package com.jcondotta.cards.core.validation.security;

import java.util.regex.Pattern;

public class LDAPInjectionPattern implements ThreatInputPattern {

    private static final Pattern LDAP_INJECTION_PATTERN = Pattern.compile(
            "([&|!()=*]{2,})" +                     // Special LDAP characters in combinations
            "|(\\badmin\\b.*)" +                    // Detects 'admin' keyword followed by any characters
            "|(\\|\\(.*?\\=\\*\\))" +               // Detects constructs like (uid=*) for wildcard LDAP searches
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean containsPattern(String value) {
        return LDAP_INJECTION_PATTERN.matcher(value).find();
    }
}