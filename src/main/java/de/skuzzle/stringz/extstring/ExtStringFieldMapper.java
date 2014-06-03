package de.skuzzle.stringz.extstring;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.skuzzle.stringz.DefaultFieldMapper;
import de.skuzzle.stringz.annotation.NoResource;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.FieldMappingException;

public class ExtStringFieldMapper extends DefaultFieldMapper {

    @Override
    public boolean accept(Field field) {
        final int m = field.getModifiers();
        return Modifier.isStatic(m) &&
                !Modifier.isFinal(m) &&
                Modifier.isPublic(m) &&
                field.isAnnotationPresent(NoResource.class) &&
                ExtString.class.isAssignableFrom(field.getType());
    }

    @Override
    public void mapField(ResourceMapping mapping, Field field, ResourceBundle bundle)
            throws FieldMappingException, MissingResourceException {
        
        field.setAccessible(true);
        final String resourceKey = getResourceKey(field);
        final String value = getValue(mapping, bundle, resourceKey);
        final ExtString extValue = mapping.intern() 
                ? new ExtString(value).intern() 
                : new ExtString(value);
                
        try {
            field.set(null, extValue);
        } catch (IllegalAccessException e) {
            throw new FieldMappingException(String.format(
                    "Resource initialization failed. family=%s, field=%s, value=%s",
                    mapping.value(), field.getName(), value), e);
        }
    }
}
