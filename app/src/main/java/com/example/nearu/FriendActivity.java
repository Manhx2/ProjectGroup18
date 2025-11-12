package com.example.nearu;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;


import java.util.ArrayList;

public class FriendActivity extends AppCompatActivity {

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
            resetTabs();
            v.setBackgroundColor(Color.parseColor("#456882"));
        };

        tabOnline.setOnClickListener(listener);
        tabAll.setOnClickListener(listener);
        tabAdd.setOnClickListener(listener);
    }

    private void resetTabs() {
        tabOnline.setBackgroundColor(Color.parseColor("#1B3C53"));
        tabAll.setBackgroundColor(Color.parseColor("#1B3C53"));
        tabAdd.setBackgroundColor(Color.parseColor("#1B3C53"));
    }
}

