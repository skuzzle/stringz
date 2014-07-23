package de.skuzzle.stringz;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

// Class adapted from: http://stackoverflow.com/a/4660195/2489557
/**
 * Simple {@link Control} instance which creates a {@link PropertyResourceBundle} that is
 * read using a specified encoding.
 *
 * @author Simon Taddiken
 */
public class CharsetBundleControl extends Control {

    /** The charset in which the resource should be read */
    protected final Charset charset;

    /**
     * Creates a new CharsetBundleControl.
     *
     * @param charset Name of the charset to use.
     * @throws IllegalArgumentException If <tt>charset</tt> is <code>null</code>
     */
    public CharsetBundleControl(String charset) {
        if (charset == null) {
            throw new IllegalArgumentException("charset is null");
        }
        this.charset = Charset.forName(charset);
    }

    /**
     * Creates a new CharsetBundleControl.
     *
     * @param charset The charset to use.
     * @throws IllegalArgumentException If <tt>charset</tt> is <code>null</code>
     */
    public CharsetBundleControl(Charset charset) {
        if (charset == null) {
            throw new IllegalArgumentException("charset is null");
        }
        this.charset = charset;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format,
            ClassLoader loader, boolean reload)
                    throws IllegalAccessException, InstantiationException, IOException {
        // The below is a copy of the default implementation.
        final String bundleName = toBundleName(baseName, locale);
        final String resourceName = toResourceName(bundleName, "properties"); //$NON-NLS-1$
        ResourceBundle bundle = null;
        InputStream stream = null;
        if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null) {
                URLConnection connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }
        } else {
            stream = loader.getResourceAsStream(resourceName);
        }
        if (stream != null) {
            try {
                bundle = new PropertyResourceBundle(
                        new InputStreamReader(stream, this.charset));
            } finally {
                stream.close();
            }
        }
        return bundle;
    }
}
