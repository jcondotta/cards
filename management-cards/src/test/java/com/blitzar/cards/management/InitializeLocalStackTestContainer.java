package com.blitzar.cards.management;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@ExtendWith(LocalStackTestContainerInitializer.class)
public @interface InitializeLocalStackTestContainer {
}