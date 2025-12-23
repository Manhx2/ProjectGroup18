package com.example.nearu.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.nearu.BaseActivity;
import com.example.nearu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnUploadPRFImage, btnSave;
    private Uri imageUri;
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    EditText edtName, edtBio;
    FirebaseUser user;
    ImageView imgPreview;

    private String imageBase64 = null;

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
        imgPreview = findViewById(R.id.imgPreview);
        edtName = findViewById(R.id.editName);
        edtBio = findViewById(R.id.editBio);
        btnSave = findViewById(R.id.btnSave);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        btnUploadPRFImage.setOnClickListener(v -> openFileChooser());

        if (user != null) {
            loadUserProfile();
        }

        btnSave.setOnClickListener(v -> saveUserProfile());
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
            imgPreview.setImageURI(imageUri);

            try {
                InputStream is = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                // Nén ảnh sang Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] bytes = baos.toByteArray();

                imageBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

            } catch (Exception e) {
                Toast.makeText(this, "Lỗi đọc ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserProfile() {
        String name = edtName.getText().toString().trim();
        String bio = edtBio.getText().toString().trim();
        String userId = user.getUid();

        Map<String, Object> profile = new HashMap<>();
        profile.put("name", name);
        profile.put("bio", bio);

        if (imageBase64 != null) {
            profile.put("imageBase64", imageBase64);
        }

        db.collection("users").document(userId)
                .set(profile, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show()
                )
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
                        String imgB64 = document.getString("imageBase64");

                        edtName.setText(name != null ? name : "");
                        edtBio.setText(bio != null ? bio : "");

                        if (imgB64 != null && !imgB64.isEmpty()) {
                            try {
                                byte[] bytes = Base64.decode(imgB64, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                imgPreview.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}