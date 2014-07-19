package de.skuzzle.stringz.rstring;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.skuzzle.stringz.DefaultFieldMapper;
import de.skuzzle.stringz.annotation.NoResource;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.FieldMapperException;

public class RStringFieldMapper extends DefaultFieldMapper {

    @Override
    public boolean accept(Field field) {
        final int m = field.getModifiers();
        return Modifier.isStatic(m) &&
                !Modifier.isFinal(m) &&
                Modifier.isPublic(m) &&
                !field.isAnnotationPresent(NoResource.class) &&
                RString.class.isAssignableFrom(field.getType());
    }

    @Override
    public void mapField(ResourceMapping mapping, Field field, ResourceBundle bundle)
            throws FieldMapperException, MissingResourceException {

        field.setAccessible(true);
        final String resourceKey = getResourceKey(field);
        final String value = getValue(mapping, bundle, resourceKey);
        final RString extValue = mapping.intern()
                ? RString.intern(value)
                : new RString(value);

        try {
            field.set(null, extValue);
        } catch (IllegalAccessException e) {
            throw new FieldMapperException(String.format(
                    "Resource initialization failed. family=%s, field=%s, value=%s",
                    mapping.value(), field.getName(), value), e);
        }
    }
}
