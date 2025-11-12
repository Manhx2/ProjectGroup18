package com.example.nearu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

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
                    default:
                        selectedText.setText("Không cho phép");
                        break;
                }
                dialog.dismiss();
            });
            builder.setNegativeButton("Hủy", null);
            builder.show();
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