package com.example.nearu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView txtName, txtBio;
    ImageView avatarImage;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String email = prefs.getString("user_email", "Không có email");
        TextView userEmail = findViewById(R.id.userEmail);
        userEmail.setText(email);

        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        txtName = findViewById(R.id.userName);
        txtBio = findViewById(R.id.userBio);
        avatarImage = findViewById(R.id.userAvatar);

        // Khởi tạo Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user != null) {
            loadUserProfile();
        }
    }

    private void loadUserProfile() {
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String bio = document.getString("bio");
                        String imageUrl = document.getString("imageUrl");

                        txtName.setText(name != null ? name : "Chưa đặt tên");
                        txtBio.setText(bio != null ? bio : "Chưa có tiểu sử");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .into(avatarImage);
                        } else {
                            avatarImage.setImageResource(R.drawable.default_avatar);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}