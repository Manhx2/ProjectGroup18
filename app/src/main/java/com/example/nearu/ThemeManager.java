package com.example.nearu;

import android.content.Context;

public class ThemeManager {
    private static final String PREFS = "theme_prefs";
    private static final String KEY_THEME = "selected_theme";

    public static final int THEME_1 = R.style.Theme_MyApp_Theme1;
    public static final int THEME_2 = R.style.Theme_MyApp_Theme2;
    public static final int THEME_3 = R.style.Theme_MyApp_Theme3;

    public static void applyTheme(Context context, int themeResId) {
        // Lưu vào prefs
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_THEME, themeResId)
                .apply();
    }

    public static int getSavedTheme(Context context) {
        int defaultTheme = THEME_1;
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_THEME, defaultTheme);
    }
}
