package de.skuzzle.stringz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

/**
 * This is a marker annotation for {@code public static String} fields within a 
 * message class which should not be mapped to a resource.
 * 
 * @author Simon Taddiken
 * @see de.skuzzle.stringz.Stringz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoResource {}