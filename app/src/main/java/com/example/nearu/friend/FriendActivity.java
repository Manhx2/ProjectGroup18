package com.example.nearu.friend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nearu.BaseActivity;
import com.example.nearu.R;
import com.example.nearu.noti.NotificationHelper;
import com.example.nearu.settings.ThemeManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView tabOnline, tabAll, tabRequests, tabAdd;
    private View includeAddFriend;

    // Khai báo Adapters và Lists
    private ArrayList<Friend> friendList;
    private FriendAdapter friendAdapter;

    private ArrayList<FriendRequest> requestList;
    private FriendRequestAdapter requestAdapter;
    EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        // 1. Khởi tạo danh sách và Adapter TRƯỚC KHI setup Tab
        initData();
        // 2. Ánh xạ View
        initViews();
        // 3. Cài đặt sự kiện Tab
        setupTabs();

        // Mặc định hiện danh sách Online
        filterFriends(true);
    }

    private void initData() {
        // Cực kỳ quan trọng: Phải khởi tạo List và Adapter ở đây để tránh crash
        friendList = new ArrayList<>();
        friendAdapter = new FriendAdapter(friendList);

        requestList = new ArrayList<>();
        requestAdapter = new FriendRequestAdapter(requestList);
    }

    private void initViews() {
        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.rvFriends);
        tabOnline = findViewById(R.id.tabOnline);
        tabAll = findViewById(R.id.tabAll);
        tabRequests = findViewById(R.id.tabRequests);
        tabAdd = findViewById(R.id.tabAdd);
        includeAddFriend = findViewById(R.id.includeAddFriend);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Thiết lập RecyclerView ban đầu
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(friendAdapter);

        // Xử lý layout Thêm bạn
        if (includeAddFriend != null) {
            EditText edtName = includeAddFriend.findViewById(R.id.edtFriendName);
            Button btnSend = includeAddFriend.findViewById(R.id.btnAddFriend);
            btnSend.setOnClickListener(v -> {
                String name = edtName.getText().toString().trim();
                if (!name.isEmpty()) sendFriendRequest(name);
            });
        }
    }

    private void setupTabs() {
        View.OnClickListener tabListener = v -> {
            updateTabUI((TextView) v);
            int id = v.getId();

            // Mặc định hiện RecyclerView, ẩn Layout Add
            searchBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            includeAddFriend.setVisibility(View.GONE);

            if (id == R.id.tabOnline) {
                recyclerView.setAdapter(friendAdapter);
                filterFriends(true);
            } else if (id == R.id.tabAll) {
                recyclerView.setAdapter(friendAdapter);
                filterFriends(false);
            } else if (id == R.id.tabRequests) {
                // Chuyển sang Adapter của Lời mời
                recyclerView.setAdapter(requestAdapter);
                loadFriendRequests();
            } else if (id == R.id.tabAdd) {
                searchBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                includeAddFriend.setVisibility(View.VISIBLE);
            }
        };

        tabOnline.setOnClickListener(tabListener);
        tabAll.setOnClickListener(tabListener);
        tabRequests.setOnClickListener(tabListener);
        tabAdd.setOnClickListener(tabListener);
    }

    private void updateTabUI(TextView selectedTab) {
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
        tabRequests.setBackgroundColor(colorPrimary);
        tabAdd.setBackgroundColor(colorPrimary);

        selectedTab.setBackgroundColor(colorSecondary);
    }

    private void loadFriendRequests() {
        String myId = FirebaseAuth.getInstance().getUid();
        if (myId == null) return;

        FirebaseFirestore.getInstance()
                .collection("friend_requests")
                .whereEqualTo("toId", myId)
                .whereEqualTo("status", "pending")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    // Map để loại trùng theo fromId
                    Map<String, FriendRequest> uniqueMap = new HashMap<>();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        FriendRequest req = doc.toObject(FriendRequest.class);
                        if (req != null && req.getFromId() != null) {
                            uniqueMap.put(req.getFromId(), req); // ghi đè nếu trùng
                        }
                    }

                    requestList.clear();
                    requestList.addAll(uniqueMap.values());
                    requestAdapter.notifyDataSetChanged();
                });
    }

    // Các hàm loadFriendsData, fetchFriendDetails, sendFriendRequest giữ nguyên như bản trước
    // ... (Hãy đảm bảo bạn copy đầy đủ các hàm này từ code cũ)

    private void loadFriendsData(boolean onlyOnline) {
        String myId = FirebaseAuth.getInstance().getUid();
        if (myId == null) return;
        FirebaseFirestore.getInstance().collection("users").document(myId)
                .addSnapshotListener((value, error) -> {
                    if (value != null && value.exists()) {
                        List<String> uids = (List<String>) value.get("friends");
                        if (uids != null) fetchFriendDetails(uids, onlyOnline);
                    }
                });
    }

    private void fetchFriendDetails(List<String> uids, boolean onlyOnline) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Friend> tempList = new ArrayList<>();
        if (uids.isEmpty()) {
            friendList.clear();
            friendAdapter.notifyDataSetChanged();
            return;
        }

        final int[] count = {0};
        for (String uid : uids) {
            db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    boolean isOnline = "online".equals(doc.getString("status"));
                    if (!onlyOnline || isOnline) {
                        Friend f = new Friend(doc.getId(), doc.getString("name"), R.drawable.ic_launcher_foreground, isOnline);
                        f.setAvatarUrl(doc.getString("imageBase64"));
                        tempList.add(f);
                    }
                }
                count[0]++;
                if (count[0] == uids.size()) {
                    friendList.clear();
                    friendList.addAll(tempList);
                    friendAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void filterFriends(boolean onlyOnline) { loadFriendsData(onlyOnline); }

    private void sendFriendRequest(String targetName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String myId = FirebaseAuth.getInstance().getUid();
        db.collection("users").whereEqualTo("name", targetName).get().addOnSuccessListener(query -> {
            if (!query.isEmpty()) {
                DocumentSnapshot targetDoc = query.getDocuments().get(0);
                String targetId = targetDoc.getId();
                if (targetId.equals(myId)) return;

                db.collection("users").document(myId).get().addOnSuccessListener(myDoc -> {
                    String myName = myDoc.getString("name");
                    FriendRequest req = new FriendRequest(myId, myName, targetId, targetName, "pending");
                    db.collection("friend_requests").add(req);
                    NotificationHelper.sendFriendRequestNoti(targetId, myId, myName);
                    Toast.makeText(this, "Đã gửi lời mời", Toast.LENGTH_SHORT).show();
                });
            } else Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
        });
    }
}