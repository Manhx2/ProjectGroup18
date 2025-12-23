package com.example.nearu.settings;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nearu.BaseActivity;
import com.example.nearu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AccountActivity extends BaseActivity {
    FirebaseFirestore db;
    FirebaseUser user;
    EditText txtName;
    EditText edtBirthday;
    ImageView btnPickDate;
    Button btnSave;
    RadioGroup rgGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

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

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        txtName = findViewById(R.id.editName);
        edtBirthday = findViewById(R.id.edtBirthday);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSave = findViewById(R.id.btnSave);
        rgGender = findViewById(R.id.rgGender);

        btnSave.setOnClickListener(v -> saveProfile());
        btnPickDate.setOnClickListener(v -> showDatePicker());
        addBirthdayFormatter();
        loadUserProfile();
    }

    private void loadUserProfile() {
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        txtName.setText(name != null ? name : "");

                        String birthday = document.getString("birthday");
                        edtBirthday.setText(birthday != null ? birthday : "");

                        String gender = document.getString("gender");
                        if (gender != null) {
                            switch (gender) {
                                case "male":
                                    rgGender.check(R.id.rbMale);
                                    break;
                                case "female":
                                    rgGender.check(R.id.rbFemale);
                                    break;
                                case "other":
                                    rgGender.check(R.id.rbOther);
                                    break;
                            }
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void addBirthdayFormatter() {
        edtBirthday.addTextChangedListener(new TextWatcher() {
            boolean isUpdating = false;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                isUpdating = true;

                String input = s.toString().replace("/", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < input.length() && i < 8; i++) {
                    formatted.append(input.charAt(i));
                    if (i == 1 || i == 3) formatted.append("/");
                }

                edtBirthday.setText(formatted.toString());
                edtBirthday.setSelection(formatted.length());
                isUpdating = false;
            }
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, day);

                    SimpleDateFormat sdf =
                            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    edtBirthday.setText(sdf.format(picked.getTime()));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void saveProfile() {
        if (user == null) return;

        String name = txtName.getText().toString().trim();
        String birthday = edtBirthday.getText().toString().trim();

        String gender = null;
        int checkedId = rgGender.getCheckedRadioButtonId();
        if (checkedId == R.id.rbMale) gender = "male";
        else if (checkedId == R.id.rbFemale) gender = "female";
        else if (checkedId == R.id.rbOther) gender = "other";

        if (!birthday.matches("\\d{2}/\\d{2}/\\d{4}")) {
            Toast.makeText(this, "Ngày sinh phải đúng dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("name", name);
        data.put("birthday", birthday);
        if (gender != null) {
            data.put("gender", gender);
        }

        db.collection("users").document(user.getUid())
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Đã lưu thông tin", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}