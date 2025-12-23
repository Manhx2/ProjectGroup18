package com.example.nearu.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.nearu.BaseActivity;
import com.example.nearu.MainActivity;
import com.example.nearu.R;

public class ThemeSelectorActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_selector);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
