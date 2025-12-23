package com.example.nearu.friend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nearu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private List<FriendRequest> requestList;

    public FriendRequestAdapter(List<FriendRequest> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequest request = requestList.get(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(request.getFromId())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        holder.tvName.setText(doc.getString("username"));
                    }
                });

        holder.btnAccept.setOnClickListener(v -> {
            db.collection("users").document(myId)
                    .update("friends", FieldValue.arrayUnion(request.getFromId()));

            db.collection("users").document(request.getFromId())
                    .update("friends", FieldValue.arrayUnion(myId));

            db.collection("friend_requests")
                    .whereEqualTo("fromId", request.getFromId())
                    .whereEqualTo("toId", myId)
                    .get()
                    .addOnSuccessListener(q -> {
                        if (!q.isEmpty()) {
                            q.getDocuments().get(0).getReference()
                                    .update("status", "accepted");
                        }
                    });

            Toast.makeText(v.getContext(), "Đã kết bạn", Toast.LENGTH_SHORT).show();
        });

        holder.btnReject.setOnClickListener(v -> {
            db.collection("friend_requests")
                    .whereEqualTo("fromId", request.getFromId())
                    .whereEqualTo("toId", myId)
                    .get()
                    .addOnSuccessListener(q -> {
                        if (!q.isEmpty()) {
                            q.getDocuments().get(0).getReference()
                                    .update("status", "rejected");
                        }
                    });

            Toast.makeText(v.getContext(), "Đã từ chối", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView btnAccept, btnReject;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
