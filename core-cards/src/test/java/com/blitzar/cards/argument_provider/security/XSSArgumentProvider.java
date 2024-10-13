package com.blitzar.cards.argument_provider.security;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class XSSArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(Named.of("XSS Script Tag", "<script></script>")) //TODO REMOVE THIS LATER
//                Arguments.of(Named.of("XSS Script Tag", "<script>alert(1)")),
//                Arguments.of(Named.of("XSS Encoded <script>", "%3Cscript%3Ealert(1)"))
//                Arguments.of(Named.of("XSS Image Tag", "<img src=x onerr=1>")),
//                Arguments.of(Named.of("XSS Attribute Injection", "\" onclick=alert(1)\"")),
//                Arguments.of(Named.of("XSS Event Handler", "onload=alert(1)")),
//                Arguments.of(Named.of("XSS Inline JS", "javascript:alert(1)")),
//                Arguments.of(Named.of("XSS HTML Comments", "<!--<script>1</s>-->")),
//                Arguments.of(Named.of("XSS Encoded Img Tag", "%3Cimg src=x onerror>")),
//                Arguments.of(Named.of("XSS in CSS", "background:url(j(1))"))
        );
    }
}
