package com.example.nearu;

import android.os.Bundle;
import androidx.recyclerview.widget.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.*;

public class NotificationActivity extends BaseActivity {

    RecyclerView rv;
    List<NotificationItem> list = new ArrayList<>();
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_notification);

        rv = findViewById(R.id.rvNotification);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(list);
        rv.setAdapter(adapter);

        loadNotifications();
    }

    void loadNotifications() {
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot s) {
                        list.clear();
                        for (DataSnapshot d : s.getChildren()) {
                            NotificationItem n = d.getValue(NotificationItem.class);
                            if (n != null) {
                                n.id = d.getKey();
                                list.add(n);
                                d.getRef().child("read").setValue(true);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override public void onCancelled(DatabaseError e) {}
                });
    }
}
