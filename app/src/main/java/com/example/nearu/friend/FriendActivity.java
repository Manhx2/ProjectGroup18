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
<<<<<<< Updated upstream
import com.example.nearu.ThemeManager;
=======
import com.example.nearu.noti.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
>>>>>>> Stashed changes

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView tabOnline, tabAll, tabRequests, tabAdd;
    private View includeAddFriend;

    // Khai báo Adapters và Lists
    private ArrayList<Friend> friendList;
    private FriendAdapter friendAdapter;

    private ArrayList<FriendRequest> requestList;
    private FriendRequestAdapter requestAdapter;

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
        // Sử dụng ContextCompat để lấy màu từ R.color (Thay tên màu đúng của bạn)
        int active = ContextCompat.getColor(this, R.color.colorSecondary);
        int inactive = ContextCompat.getColor(this, R.color.colorPrimary);

        tabOnline.setBackgroundColor(inactive);
        tabAll.setBackgroundColor(inactive);
        tabRequests.setBackgroundColor(inactive);
        tabAdd.setBackgroundColor(inactive);

        selectedTab.setBackgroundColor(active);
    }

    private void loadFriendRequests() {
        String myId = FirebaseAuth.getInstance().getUid();
        if (myId == null) return;

        FirebaseFirestore.getInstance().collection("friend_requests")
                .whereEqualTo("toId", myId)
                .whereEqualTo("status", "pending")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", error.getMessage());
                        return;
                    }
                    if (value != null) {
                        requestList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            FriendRequest req = doc.toObject(FriendRequest.class);
                            if (req != null) requestList.add(req);
                        }
                        requestAdapter.notifyDataSetChanged();
                    }
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