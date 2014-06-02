package de.skuzzle.stringz;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ResourceBundle;

import de.skuzzle.stringz.annotation.NoResource;
import de.skuzzle.stringz.annotation.ResourceKey;
import de.skuzzle.stringz.annotation.ResourceMapping;

/**
 * This is the default field mapping strategy for Stringz. See description of 
 * {@link #accept(Field) accept} and 
 * {@link #mapField(ResourceMapping, Field, ResourceBundle) mapField} to learn how values
 * are assigned to which fields.
 * 
 * @author Simon Taddiken
 */
public class DefaultFieldMapper implements FieldMapper {

    /**
     * {@inheritDoc}
     * <p>This method implements the default filtering of fields for <tt>Stringz</tt>. It
     * will return <code>true</code> if, and only if the passed field</p>
     * <ul>
     *  <li>is static,</li>
     *  <li>is public,</li>
     *  <li>is <em>not</em> final,</li>
     *  <li>is declared as String and</li>
     *  <li>is not marked with {@link NoResource}</li>
     * </ul>
     */
    @Override
    public boolean accept(Field field) {
        return Modifier.isStatic(field.getModifiers()) &&
                Modifier.isPublic(field.getModifiers()) &&
                !Modifier.isFinal(field.getModifiers()) &&
                field.getType() == String.class &&
                !field.isAnnotationPresent(NoResource.class);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>Implements the default field assignment strategy for <tt>Stringz</tt>. If the
     * passed field is annotated with {@link ResourceKey}, the annotation's value is
     * used as key to reference the resource value. Otherwise, the field's name will be
     * used as key. The value for the field will be retrieved using 
     * {@link ResourceBundle#getString(String)}. If the passed ResourceMapping's 
     * <tt>intern</tt> attribute is <code>true</code>, {@link String#intern()} will be 
     * called before assigning the value to the field.</p>
     * 
     * @throws java.util.MissingResourceException {@inheritDoc}
     * @throws FieldMappingException If the {@link Field#set(Object, Object)} method fails
     *          with an {@link IllegalAccessException}.
     */
    @Override
    public void mapField(ResourceMapping mapping, Field field, ResourceBundle bundle) {
        field.setAccessible(true);
        final String resourceKey;
        if (field.isAnnotationPresent(ResourceKey.class)) {
            final ResourceKey rk = field.getAnnotation(ResourceKey.class);
            resourceKey = rk.value();
        } else {
            resourceKey = field.getName();
        }
        String value = bundle.getString(resourceKey);
        if (mapping.intern()) {
            value = value.intern();
        }
        try {
            field.set(null, value);
        } catch (IllegalAccessException e) {
            throw new FieldMappingException(String.format(
                    "Resource initialization failed. family=%s, field=%s, value=%s",
                    mapping.value(), field.getName(), value), e);
        }
    }
}
