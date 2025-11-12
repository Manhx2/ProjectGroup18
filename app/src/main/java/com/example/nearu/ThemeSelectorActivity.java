package com.example.nearu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ThemeSelectorActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_selector);

        View card1 = findViewById(R.id.themeCard1);
        View card2 = findViewById(R.id.themeCard2);
        View card3 = findViewById(R.id.themeCard3);

        card1.setOnClickListener(v -> applyAndRestart(ThemeManager.THEME_1));
        card2.setOnClickListener(v -> applyAndRestart(ThemeManager.THEME_2));
        card3.setOnClickListener(v -> applyAndRestart(ThemeManager.THEME_3));
    }

    private void applyAndRestart(int themeResId) {
        ThemeManager.applyTheme(this, themeResId);

        // Khởi động lại MainActivity để áp theme
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
