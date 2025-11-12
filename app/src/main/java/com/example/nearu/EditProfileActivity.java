package com.example.nearu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnUploadPRFImage, btnSave;
    private Uri imageUri;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    EditText edtName, edtBio;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUploadPRFImage = findViewById(R.id.btnUploadPRFImage);
        btnSave = findViewById(R.id.btnSave);

        edtName = findViewById(R.id.editName);
        edtBio = findViewById(R.id.editBio);
        btnSave = findViewById(R.id.btnSave);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        btnUploadPRFImage.setOnClickListener(v -> openFileChooser());

        if (user != null) {
            loadUserProfile();
        }

        btnSave.setOnClickListener(v -> {
            // Upload Tên / Bio
            String name = edtName.getText().toString().trim();
            String bio = edtBio.getText().toString().trim();
            saveUserProfile(name, bio);
            // Upload Avatar
            if (imageUri != null) {
                uploadImageToFirebase();
            }
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    private void uploadImageToFirebase() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        StorageReference fileRef = storage.getReference("users/" + userId + "/profile.jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveUserInfoToFirestore(userId, imageUrl);
                        }))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Tải ảnh thất bại!", Toast.LENGTH_SHORT).show());
    }

    private void saveUserInfoToFirestore(String userId, String imageUrl) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("imageUrl", imageUrl);

        db.collection("users").document(userId)
                .set(userInfo)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi khi lưu!", Toast.LENGTH_SHORT).show());
    }

    private void saveUserProfile(String name, String bio) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", name);
        profile.put("bio", bio);

        db.collection("users").document(user.getUid())
                .set(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void loadUserProfile() {
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String bio = document.getString("bio");

                        edtName.setText(name != null ? name : "");
                        edtBio.setText(bio != null ? bio : "");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}