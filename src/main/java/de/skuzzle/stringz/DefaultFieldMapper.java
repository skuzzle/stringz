package de.skuzzle.stringz;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ResourceBundle;

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

    @Override
    public void mapField(ResourceMapping mapping, Field field, ResourceBundle bundle) {
        field.setAccessible(true);
        String value = bundle.getString(field.getName());
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
