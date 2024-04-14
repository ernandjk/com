package com.onesignal.language;

import java.util.Locale;

public class LanguageProviderDevice implements LanguageProvider
{
    private static final String CHINESE = "zh";
    private static final String HEBREW_CORRECTED = "he";
    private static final String HEBREW_INCORRECT = "iw";
    private static final String INDONESIAN_CORRECTED = "id";
    private static final String INDONESIAN_INCORRECT = "in";
    private static final String YIDDISH_CORRECTED = "yi";
    private static final String YIDDISH_INCORRECT = "ji";
    
    @Override
    public String getLanguage() {
        final String language = Locale.getDefault().getLanguage();
        language.hashCode();
        final int hashCode = language.hashCode();
        int n = -1;
        switch (hashCode) {
            case 3886: {
                if (!language.equals((Object)"zh")) {
                    break;
                }
                n = 3;
                break;
            }
            case 3391: {
                if (!language.equals((Object)"ji")) {
                    break;
                }
                n = 2;
                break;
            }
            case 3374: {
                if (!language.equals((Object)"iw")) {
                    break;
                }
                n = 1;
                break;
            }
            case 3365: {
                if (!language.equals((Object)"in")) {
                    break;
                }
                n = 0;
                break;
            }
        }
        switch (n) {
            default: {
                return language;
            }
            case 3: {
                final StringBuilder sb = new StringBuilder();
                sb.append(language);
                sb.append("-");
                sb.append(Locale.getDefault().getCountry());
                return sb.toString();
            }
            case 2: {
                return "yi";
            }
            case 1: {
                return "he";
            }
            case 0: {
                return "id";
            }
        }
    }
}
