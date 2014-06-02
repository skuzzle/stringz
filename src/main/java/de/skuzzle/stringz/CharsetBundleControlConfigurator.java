package de.skuzzle.stringz;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ResourceBundle.Control;

import de.skuzzle.stringz.annotation.ResourceMapping;
import de.skuzzle.stringz.strategy.ControlConfigurationException;
import de.skuzzle.stringz.strategy.ControlConfigurator;

/**
 * ControlConfigurator which creates a {@link CharsetBundleControl} using the charset 
 * provided with {@link ResourceMapping#encoding()}.
 * 
 * @author Simon Taddiken
 */
public class CharsetBundleControlConfigurator implements ControlConfigurator {

    /**
     * {@inheritDoc}
     * 
     * <p>This method will throw {@link ControlConfigurationException} if the passed 
     * <tt>args</tt> are not empty or if the charset specified at 
     * <tt>mapping.encoding()</tt> is not valid.</p>
     * 
     * @throws ControlConfigurationException If <tt>args.length != 0</tt> or the charset 
     *          provided at <tt>mapping.encoding()</tt> is not supported.
     */
    @Override
    public Control configure(ResourceMapping mapping, String[] args) {
        final Charset charset;
        if (args.length != 0) {
            throw new ControlConfigurationException(
                    "This class has no additional parameters");
        } else {
            try {
                charset = Charset.forName(mapping.encoding());
            } catch (UnsupportedCharsetException e) {
                throw new ControlConfigurationException(String.format(
                        "Unsupported charset: %s", args[0]), e);
            }
        }
        return new CharsetBundleControl(charset);
    }
}
