package de.skuzzle.stringz;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ResourceBundle.Control;

import de.skuzzle.stringz.annotation.ResourceControl;
import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.ControlFactoryException;
import de.skuzzle.stringz.strategy.ControlFactory;

/**
 * ControlFactory which creates a {@link CharsetBundleControl} using the charset 
 * provided with {@link ResourceMapping#encoding()}. This factory does not need to 
 * be specified on a message class using {@link ResourceControl}, because it will be used
 * as default when no other <tt>ResourceControl</tt> is given.
 * 
 * @author Simon Taddiken
 */
public class CharsetBundleControlFactory implements ControlFactory {

    /**
     * {@inheritDoc}
     * 
     * <p>This method will throw {@link ControlFactoryException} if the passed 
     * <tt>args</tt> are not empty or if the charset specified at 
     * <tt>mapping.encoding()</tt> is not valid.</p>
     * 
     * @throws ControlFactoryException If <tt>args.length != 0</tt> or the charset 
     *          provided at <tt>mapping.encoding()</tt> is not supported.
     */
    @Override
    public Control create(ResourceMapping mapping, String[] args) {
        final Charset charset;
        if (args.length != 0) {
            throw new ControlFactoryException(
                    "This class has no additional parameters");
        } else {
            try {
                charset = Charset.forName(mapping.encoding());
            } catch (UnsupportedCharsetException e) {
                throw new ControlFactoryException(String.format(
                        "Unsupported charset: %s", args[0]), e);
            }
        }
        return new CharsetBundleControl(charset);
    }
}
