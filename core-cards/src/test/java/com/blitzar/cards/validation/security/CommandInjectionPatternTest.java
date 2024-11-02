package com.blitzar.cards.validation.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CommandInjectionPatternTest {

    private final CommandInjectionPattern commandInjectionPattern = new CommandInjectionPattern();

    @Test
    void shouldDetectCommandInjection_whenInputContainsRmAndLsCommands() {
        String maliciousInput = "rm -rf / && ls";
        Assertions.assertThat(commandInjectionPattern.containsPattern(maliciousInput))
                .withFailMessage("Expected to detect command injection with 'rm' and 'ls' commands")
                .isTrue();
    }

    @Test
    void shouldNotDetectCommandInjection_whenInputIsSafe() {
        String safeInput = "echo 'Hello World'";
        Assertions.assertThat(commandInjectionPattern.containsPattern(safeInput))
                .withFailMessage("Expected not to detect command injection in safe input")
                .isFalse();
    }

    @Test
    void shouldNotDetectCommandInjection_whenInputIsSimpleText() {
        String safeInput = "This is a safe string without command injection.";
        Assertions.assertThat(commandInjectionPattern.containsPattern(safeInput))
                .withFailMessage("Expected not to detect command injection in simple text")
                .isFalse();
    }
}
