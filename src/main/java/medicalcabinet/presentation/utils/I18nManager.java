package medicalcabinet.presentation.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18nManager {
    private static ResourceBundle bundle;

    static {
        setLocale("ro", "RO");
    }

    public static void setLocale(String lang, String country) {
        try {
            Locale locale = new Locale(lang, country);
            bundle = ResourceBundle.getBundle("lang.messages", locale);
        } catch (Exception e) {
            bundle = null;
        }
    }

    public static String getString(String key, String defaultText) {
        if (bundle != null) {
            try {
                return new String(bundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
            } catch (Exception e) {
                return defaultText;
            }
        }
        return defaultText;
    }
}