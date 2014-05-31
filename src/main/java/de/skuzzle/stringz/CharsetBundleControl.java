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
class CharsetBundleControl extends Control {

    private final Charset charset;

    public CharsetBundleControl(String charset) {
        if (charset == null) {
            throw new IllegalArgumentException("charset is null");
        }
        this.charset = Charset.forName(charset);
    }

    public CharsetBundleControl(Charset charset) {
        if (charset == null) {
            throw new IllegalArgumentException("charset is null");
        }
        this.charset = charset;
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale,
            String format,
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
                // Only this line is changed to make it to read properties files
                // as UTF-8.
                bundle = new PropertyResourceBundle(
                        new InputStreamReader(stream, this.charset)); //$NON-NLS-1$
            } finally {
                stream.close();
            }
        }
        return bundle;
    }
}
