package com.example.nearu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.location.Location;
import com.google.firebase.database.ServerValue;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private MapView map = null;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://device-streaming-d09ad2f9-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorAccent, typedValue, true);
        int icMenuColor = typedValue.data;
        toggle.getDrawerArrowDrawable().setColor(icMenuColor);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION

        });

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);

        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map) {
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                super.onLocationChanged(location, source);
                if (location != null && mAuth.getCurrentUser() != null) {
                    String uid = mAuth.getCurrentUser().getUid();

                    Toast.makeText(getApplicationContext(), "Ghi cho UID: " + uid.substring(0, 5) + "...", Toast.LENGTH_SHORT).show();

                    DatabaseReference userLocationRef = mDatabase.child("locations").child(uid);

                    HashMap<String, Object> locationData = new HashMap<>();
                    locationData.put("latitude", location.getLatitude());
                    locationData.put("longitude", location.getLongitude());
                    locationData.put("timestamp", ServerValue.TIMESTAMP);

                   userLocationRef.setValue(locationData);
                }
            }
        };
        Bitmap myLocationBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location);
        mLocationOverlay.setPersonIcon(myLocationBitmap);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        map.getOverlays().add(mLocationOverlay);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            goToLogin();
        } else {
            String email = currentUser.getEmail();
            // Lưu email vào SharedPreferences để dùng ở Activity khác
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_email", email);
            editor.apply();

            NavigationView navigationView = findViewById(R.id.nav_view);
            TextView headerEmail = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
            headerEmail.setText(email);
            loadUserProfile();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (itemId == R.id.nav_friends) {
            startActivity(new Intent(this, FriendActivity.class));
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (itemId == R.id.nav_logout) {
            mAuth.signOut();
            // Xóa email trong SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            goToLogin();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();

        clearSelection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            ArrayList<String> permsToRequest = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permsToRequest.add(permissions[i]);
                }
            }
            if (!permsToRequest.isEmpty()) {
                Toast.makeText(this, "Permissions are required for this app.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void clearSelection() {
        NavigationView nav = findViewById(R.id.nav_view);
        for (int i = 0; i < nav.getMenu().size(); i++) {
            nav.getMenu().getItem(i).setChecked(false);
        }
    }

    private void loadUserProfile() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txtName = headerView.findViewById(R.id.nav_header_name);
        ImageView avatarImage = headerView.findViewById(R.id.nav_header_avatar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String imgBase64 = document.getString("imageBase64");

                        txtName.setText(name != null ? name : "Chưa đặt tên");

                        if (imgBase64 != null && !imgBase64.isEmpty()) {
                            try {
                                byte[] bytes = android.util.Base64.decode(imgBase64, android.util.Base64.DEFAULT);
                                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                avatarImage.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                avatarImage.setImageResource(R.drawable.default_avatar);
                            }
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
