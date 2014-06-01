package de.skuzzle.stringz;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ResourceBundle.Control;

public class CharsetBundleControlConfigurator implements ControlConfigurator {

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
                        "Unsupported charst: %s", args[0]), e);
            }
        }
        return new CharsetBundleControl(charset);
    }
}
