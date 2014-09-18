package de.skuzzle.stringz;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ResourceBundle;

import de.skuzzle.stringz.annotation.Delimiter;
import de.skuzzle.stringz.annotation.NoResource;
import de.skuzzle.stringz.annotation.ResourceCollection;
import de.skuzzle.stringz.annotation.ResourceKey;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.annotation.Validate;
import de.skuzzle.stringz.annotation.ValidateArray;
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

    protected final FormatStringValidator validator = new FormatStringValidator();

    /**
     * {@inheritDoc}
     * <p>
     * This method implements the default filtering of fields for
     * {@code Stringz}. It will return <code>true</code> if, and only if the
     * passed field
     * </p>
     * <ul>
     * <li>is public,</li>
     * <li>is <em>not</em> final,</li>
     * <li>is declared as either String or String[] and</li>
     * <li>is not marked with {@link NoResource}</li>
     * </ul>
     */
    @Override
    public boolean accept(Field field) {
        return Modifier.isPublic(field.getModifiers()) &&
                !Modifier.isFinal(field.getModifiers()) &&
                (field.getType() == String.class || field.getType() == String[].class) &&
                !field.isAnnotationPresent(NoResource.class);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implements the default field assignment strategy for {@code Stringz}. If
     * field has any other type than {@code String} or {@code String[]}, an
     * exception will be caused. The value for the field will be retrieved using
     * {@link ResourceBundle#getString(String)}. If the passed ResourceMapping's
     * {@code intern} attribute is <code>true</code>, {@link String#intern()}
     * will be called before assigning the value to the field.
     * </p>
     *
     * <p>
     * This method also handles the annotations {@link ResourceCollection} and
     * {@link Delimiter} for String array resources.
     * </p>
     *
     * @throws java.util.MissingResourceException {@inheritDoc}
     * @throws FieldMapperException If the field could not be mapped (could have
     *             multiple reasons. Check message and nested exception).
     * @throws FormatValidationException If validation is enabled for the field
     *             but failed.
     */
    @Override
    public void mapField(ResourceMapping mapping, Field field, ResourceBundle bundle) {
        field.setAccessible(true);

        final Object value;
        if (field.getType() == String.class) {

            final String resourceKey = getResourceKey(field);
            final Validate validate = field.getAnnotation(Validate.class);
            value = getValue(mapping, bundle, validate, resourceKey);


        } else if (field.getType() == String[].class) {
            if (field.isAnnotationPresent(ResourceCollection.class)) {
                final ResourceCollection rc = field.getAnnotation(
                        ResourceCollection.class);

                value = handleResourceCollection(field, mapping, bundle, rc);
            } else {
                value = handleDelimittedString(field, mapping, bundle);
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

    /**
     * Maps an array to the resources listed in a {@link ResourceCollection}
     * annotation.
     *
     * @param field The target field.
     * @param mapping The ResourceMapping annotation of the currently processed
     *            message class.
     * @param bundle The resolved ResourceBundle for that class.
     * @param rc The ResourceCollection annotation of that field.
     * @return The created String array.
     * @since 0.3.0
     */
    protected String[] handleResourceCollection(Field field, ResourceMapping mapping,
            ResourceBundle bundle, ResourceCollection rc) {
        if (rc.value().length == 0) {
            throw new FieldMapperException(
                    "@ResourceCollection must specify at least one mapping key");
        }

        Validate[] validators = null;
        if (field.isAnnotationPresent(ValidateArray.class)) {
            validators = field.getAnnotation(ValidateArray.class).value();
        }

        if (validators != null && validators.length != rc.value().length) {
            // when specifying validators, there must be as many as
            // there are values
            throw new FieldMapperException(String.format(
                    "@ResourceCollection contains %d values, but %d validators",
                    rc.value().length, validators.length));
        }

        final String[] v = new String[rc.value().length];
        for (int i = 0; i < v.length; ++i) {
            Validate validate = null;
            if (validators != null) {
                validate = validators[i];
            }
            final String resourceKey = rc.value()[i];
            v[i] = getValue(mapping, bundle, validate, resourceKey);
        }
        return v;
    }

    /**
     * Maps an array to a resource which is split using a delimiter.
     *
     * @param field The target field.
     * @param mapping The ResourceMapping annotation of the currently processed
     *            message class.
     * @param bundle The resolved ResourceBundle for that class.
     * @return The resulting array.
     */
    protected String[] handleDelimittedString(Field field, ResourceMapping mapping,
            ResourceBundle bundle) {
        final String delimiterPattern;
        if (field.isAnnotationPresent(Delimiter.class)) {
            final Delimiter delimiter = field.getAnnotation(Delimiter.class);
            delimiterPattern = delimiter.value();
        } else {
            delimiterPattern = getDefaultDelimiter();
        }

        final String resourceKey = getResourceKey(field);
        final String resource = getValue(mapping, bundle, null, resourceKey);
        final String[] splitted = resource.split(delimiterPattern);

        if (field.isAnnotationPresent(ValidateArray.class)) {
            final ValidateArray validateArr = field.getAnnotation(ValidateArray.class);
            if (validateArr.value().length != splitted.length) {
                throw new FieldMapperException(String.format(
                        "Splitted resource contains %d values, but %d validators",
                        splitted.length, validateArr.value().length));
            }
            for (int i = 0; i < splitted.length; ++i) {
                final Validate validate = validateArr.value()[i];
                this.validator.parseFormatString(splitted[i], validate.value());
            }
        }
        return splitted;
    }

    /**
     * Gets the delimiter pattern which will be used as default to split strings which
     * will be assigned to {@code public static String[]} variables.
     *
     * @return The delimiter pattern.
     */
    protected String getDefaultDelimiter() {
        return DEFAULT_DELIMITER;
    }

    /**
     * Gets a resource value for the provided {@code key} from the provided
     * {@code bundle}. If the mapping's {@link ResourceMapping#intern() intern}
     * attribute is <code>true</code>, then the retrieved resource value will be
     * interned in terms of {@link String#intern()}.
     *
     * <p>
     * Addtionally, if the {@code validate} parameter is not <code>null</code>,
     * this method validates the retrieved resource String (see {@link Validate}
     * for more information)
     * </p>
     *
     * @param mapping The {@link ResourceMapping} annotation of the processed
     *            message class
     * @param bundle The resolved {@link ResourceBundle} for that message class.
     * @param validate Validation information for the value. May be
     *            <code>null</code> if none are present for the field to map.
     * @param resourceKey The key of the resource value to retrieve.
     * @return The resource value for the given {@code key}.
     * @throws FormatValidationException If validation failed for the retrieved
     *             value.
     */
    protected String getValue(ResourceMapping mapping, ResourceBundle bundle,
            Validate validate, String resourceKey) {
        final String value = bundle.getString(resourceKey);
        if (validate != null) {
            this.validator.parseFormatString(value, validate.value());
        }
        return mapping.intern() ? value.intern() : value;
    }

    /**
     * Gets the key which will be used to reference a resource value for a field which is
     * to be assigned. If a {@link ResourceKey} annotation is present on the provided
     * field, its {@link ResourceKey#value() value} will be used as key, otherwise,
     * the field's name is returned.
     *
     * @param field The field for which to retrieved the resource key.
     * @return The key to use for resource look up.
     */
    protected String getResourceKey(Field field) {
        if (field.isAnnotationPresent(ResourceKey.class)) {
            final ResourceKey rk = field.getAnnotation(ResourceKey.class);
            return rk.value();
        } else {
            return field.getName();
        }
    }
}
