package com.example.nearu;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nearu.settings.ThemeManager;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Lấy theme đã lưu và áp nó
        int themeId = ThemeManager.getSavedTheme(this);
        if (themeId == 0) {
            themeId = R.style.Theme_MyApp_Base; // theme mặc định
        }
        setTheme(themeId);
        super.onCreate(savedInstanceState);
    }
}

