package medicalcabinet.presentation.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nManager {
    // The single instance of this class (Singleton Pattern)
    private static I18nManager instance;
    private ResourceBundle bundle;

    private I18nManager() {
        setLocale("en", "US");
    }

    public static I18nManager getInstance() {
        if (instance == null) {
            instance = new I18nManager();
        }
        return instance;
    }

    public void setLocale(String language, String country) {
        Locale locale = new Locale(language, country);
        bundle = ResourceBundle.getBundle("lang.messages", locale);
    }

    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            System.err.println("Translation key not found: " + key);
            return "!" + key + "!";
        }
    }
}