package de.skuzzle.stringz;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ResourceBundle;

import de.skuzzle.stringz.annotation.Delimiter;
import de.skuzzle.stringz.annotation.NoResource;
import de.skuzzle.stringz.annotation.ResourceCollection;
import de.skuzzle.stringz.annotation.ResourceKey;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.FieldMapper;
import de.skuzzle.stringz.strategy.FieldMapperException;

/**
 * This is the default field mapping strategy for Stringz. See description of
 * {@link #accept(Field) accept} and
 * {@link #mapField(ResourceMapping, Field, ResourceBundle) mapField} to learn
 * how values are assigned to which fields.
 * 
 * @author Simon Taddiken
 */
public class DefaultFieldMapper implements FieldMapper {

    private static final String DEFAULT_DELIMITER = ";";

    /**
     * {@inheritDoc}
     * <p>
     * This method implements the default filtering of fields for
     * <tt>Stringz</tt>. It will return <code>true</code> if, and only if the
     * passed field
     * </p>
     * <ul>
     * <li>is static,</li>
     * <li>is public,</li>
     * <li>is <em>not</em> final,</li>
     * <li>is declared as either String or String[] and</li>
     * <li>is not marked with {@link NoResource}</li>
     * </ul>
     */
    @Override
    public boolean accept(Field field) {
        return Modifier.isStatic(field.getModifiers()) &&
                Modifier.isPublic(field.getModifiers()) &&
                !Modifier.isFinal(field.getModifiers()) &&
                (field.getType() == String.class || field.getType() == String[].class) &&
                !field.isAnnotationPresent(NoResource.class);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Implements the default field assignment strategy for <tt>Stringz</tt>. If field 
     * has any other type than <tt>String</tt> or <tt>String[]</tt>, an exception will be
     * caused. The value for the field will be
     * retrieved using {@link ResourceBundle#getString(String)}. If the passed
     * ResourceMapping's <tt>intern</tt> attribute is <code>true</code>,
     * {@link String#intern()} will be called before assigning the value to the
     * field.</p>
     * 
     * <p>This method also handles the annotations {@link ResourceCollection} and 
     * {@link Delimiter} for String array resources.</p>
     * 
     * @throws java.util.MissingResourceException {@inheritDoc}
     * @throws FieldMapperException If the {@link Field#set(Object, Object)}
     *             method fails with an {@link IllegalAccessException}.
     */
    @Override
    public void mapField(ResourceMapping mapping, Field field, ResourceBundle bundle) {
        field.setAccessible(true);
        
        final Object value;
        if (field.getType() == String.class) {
        
            final String resourceKey = getResourceKey(field);
            value = getValue(mapping, bundle, resourceKey);

        
        } else if (field.getType() == String[].class) {
            if (field.isAnnotationPresent(ResourceCollection.class)) {
                final ResourceCollection rc = field.getAnnotation(
                        ResourceCollection.class);
                final String[] v = new String[rc.value().length];
                for (int i = 0; i < v.length; ++i) {
                    final String resourceKey = rc.value()[i];
                    v[i] = getValue(mapping, bundle, resourceKey);
                }
                value = v;
            } else {
                
                final String delimiterPattern;
                if (field.isAnnotationPresent(Delimiter.class)) {
                    final Delimiter delimiter = field.getAnnotation(Delimiter.class);
                    delimiterPattern = delimiter.value();
                } else {
                    delimiterPattern = DEFAULT_DELIMITER;
                }
                
                final String resourceKey = getResourceKey(field);
                final String resource = getValue(mapping, bundle, resourceKey);
                value = resource.split(delimiterPattern);
            }
        } else {
            // should not be reachable as by #accept method
            throw new IllegalStateException();
        }
        
        try {
            field.set(null, value);
        } catch (IllegalAccessException e) {
            throw new FieldMapperException(String.format(
                    "Resource initialization failed. family=%s, field=%s, value=%s",
                    mapping.value(), field.getName(), value), e);
        }
    }
    
    protected String getDefaultDelimiter() {
        return DEFAULT_DELIMITER;
    }

    protected String getValue(ResourceMapping mapping, ResourceBundle bundle,
            String resourceKey) {
        final String value = bundle.getString(resourceKey);
        return mapping.intern() ? value.intern() : value;
    }

    protected String getResourceKey(Field field) {
        if (field.isAnnotationPresent(ResourceKey.class)) {
            final ResourceKey rk = field.getAnnotation(ResourceKey.class);
            return rk.value();
        } else {
            return field.getName();
        }
    }
}
