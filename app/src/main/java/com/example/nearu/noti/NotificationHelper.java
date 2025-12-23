package com.example.nearu.noti;

import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {

    public static void sendFriendRequestNoti(
            String toUid, String fromUid, String fromName) {

        String id = FirebaseDatabase.getInstance()
                .getReference().push().getKey();

        Map<String, Object> map = new HashMap<>();
        map.put("title", "Lời mời kết bạn");
        map.put("content", fromName + " đã gửi lời mời kết bạn");
        map.put("type", "friend_request");
        map.put("fromUid", fromUid);
        map.put("read", false);
        map.put("time", System.currentTimeMillis());

        FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(toUid)
                .child(id)
                .setValue(map);
    }
}
