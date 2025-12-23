package com.example.nearu.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.nearu.BaseActivity;
import com.example.nearu.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout sectionAccount = findViewById(R.id.section_account);
        LinearLayout accountSubmenu = findViewById(R.id.account_submenu);
        sectionAccount.setOnClickListener(v -> {
            if (accountSubmenu.getVisibility() == View.GONE) {
                accountSubmenu.setVisibility(View.VISIBLE);
            } else {
                accountSubmenu.setVisibility(View.GONE);
            }
        });

        LinearLayout sectionPrivacy = findViewById(R.id.section_privacy);
        LinearLayout privacySubmenu = findViewById(R.id.privacy_submenu);
        sectionPrivacy.setOnClickListener(v -> {
            if (privacySubmenu.getVisibility() == View.GONE) {
                privacySubmenu.setVisibility(View.VISIBLE);
            } else {
                privacySubmenu.setVisibility(View.GONE);
            }
        });

        LinearLayout layout = findViewById(R.id.tv_friend_request_permission);
        TextView selectedText = findViewById(R.id.tv_friend_request_selected);
        layout.setOnClickListener(v -> {
            final String[] options = {
                    "Bất kỳ ai cũng có thể kết bạn",
                    "Chỉ bạn bè của bạn bè",
                    "Không cho phép"
            };

            final int[] selectedIndex = {0};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ai có thể gửi lời mời kết bạn");
            builder.setSingleChoiceItems(options, selectedIndex[0], (dialog, which) -> {
                selectedIndex[0] = which;
            });
            builder.setPositiveButton("OK", (dialog, which) -> {
                switch (selectedIndex[0]) {
                    case 0:
                        selectedText.setText("Bất kỳ ai");
                        break;
                    case 1:
                        selectedText.setText("Bạn của bạn bè");
                        break;
                    case 2:
                        selectedText.setText("Không cho phép");
                        break;
                }
                dialog.dismiss();
            });
            builder.setNegativeButton("Hủy", null);
            builder.show();
        });

        LinearLayout sectionTheme = findViewById(R.id.section_theme);
        LinearLayout themeSubmenu = findViewById(R.id.theme_submenu);
        sectionTheme.setOnClickListener(v -> {
            if (themeSubmenu.getVisibility() == View.GONE) {
                themeSubmenu.setVisibility(View.VISIBLE);
            } else {
                themeSubmenu.setVisibility(View.GONE);
            }
        });

        LinearLayout languageLayout = findViewById(R.id.tv_language);
        TextView selectedLanguage = findViewById(R.id.tv_language_selected);
        languageLayout.setOnClickListener(v -> {
            final String[] options = {
                    "Tiếng Việt",
                    "Tiếng Anh"
            };

            final int[] selectedLanguageIndex = {0};

            AlertDialog.Builder languageBuilder = new AlertDialog.Builder(this);
            languageBuilder.setTitle("Ngôn ngữ");
            languageBuilder.setSingleChoiceItems(options, selectedLanguageIndex[0], (dialog, which) -> {
                selectedLanguageIndex[0] = which;
            });
            languageBuilder.setPositiveButton("OK", (dialog, which) -> {
                switch (selectedLanguageIndex[0]) {
                    case 0:
                        selectedLanguage.setText("Tiếng Việt");
                        break;
                    case 1:
                        selectedLanguage.setText("Tiếng Anh");
                        break;
                }
                dialog.dismiss();
            });
            languageBuilder.setNegativeButton("Hủy", null);
            languageBuilder.show();
        });

        TextView btnAccount = findViewById(R.id.tv_account_info);
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        TextView btnTheme = findViewById(R.id.tv_theme);
        btnTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ThemeSelectorActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout sectionNotification = findViewById(R.id.section_notification);
        LinearLayout notificationSubmenu = findViewById(R.id.notification_submenu);
        sectionNotification.setOnClickListener(v -> {
            if (notificationSubmenu.getVisibility() == View.GONE) {
                notificationSubmenu.setVisibility(View.VISIBLE);
            } else {
                notificationSubmenu.setVisibility(View.GONE);
            }
        });

        LinearLayout sectionSupport = findViewById(R.id.section_support);
        LinearLayout supportSubmenu = findViewById(R.id.support_submenu);
        sectionSupport.setOnClickListener(v -> {
            if (supportSubmenu.getVisibility() == View.GONE) {
                supportSubmenu.setVisibility(View.VISIBLE);
            } else {
                supportSubmenu.setVisibility(View.GONE);
            }
        });

    }
}