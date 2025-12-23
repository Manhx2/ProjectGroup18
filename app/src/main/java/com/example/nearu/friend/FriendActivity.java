package com.example.nearu.friend;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;


import com.example.nearu.BaseActivity;
import com.example.nearu.R;
import com.example.nearu.settings.ThemeManager;

import java.util.ArrayList;

public class FriendActivity extends BaseActivity {

    RecyclerView recyclerView;
    ArrayList<Friend> friendList;
    FriendAdapter adapter;

    TextView tabOnline, tabAll, tabAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        // Back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Tabs
        setupTabs();

        // RecyclerView
        recyclerView = findViewById(R.id.rvFriends);
        friendList = new ArrayList<>();
        friendList.add(new Friend("Name", R.drawable.ic_launcher_foreground, true));

        adapter = new FriendAdapter(friendList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupTabs() {
        tabOnline = findViewById(R.id.tabOnline);
        tabAll = findViewById(R.id.tabAll);
        tabAdd = findViewById(R.id.tabAdd);

        View.OnClickListener listener = v -> {
            int currentTheme = ThemeManager.getSavedTheme(this);
            // Mặc định
            int colorPrimary = ContextCompat.getColor(this, R.color.theme1_primary);
            int colorSecondary = ContextCompat.getColor(this, R.color.theme1_secondary);
            // Đổi theme
            if (currentTheme == ThemeManager.THEME_1) {
                colorPrimary = ContextCompat.getColor(this, R.color.theme1_primary);
                colorSecondary = ContextCompat.getColor(this, R.color.theme1_secondary);
            } else if (currentTheme == ThemeManager.THEME_2) {
                colorPrimary = ContextCompat.getColor(this, R.color.theme2_primary);
                colorSecondary = ContextCompat.getColor(this, R.color.theme2_secondary);
            } else if (currentTheme == ThemeManager.THEME_3) {
                colorPrimary = ContextCompat.getColor(this, R.color.theme3_primary);
                colorSecondary = ContextCompat.getColor(this, R.color.theme3_secondary);
            }

            tabOnline.setBackgroundColor(colorPrimary);
            tabAll.setBackgroundColor(colorPrimary);
            tabAdd.setBackgroundColor(colorPrimary);

            v.setBackgroundColor(colorSecondary);
        };

        tabOnline.setOnClickListener(listener);
        tabAll.setOnClickListener(listener);
        tabAdd.setOnClickListener(listener);
    }
}

