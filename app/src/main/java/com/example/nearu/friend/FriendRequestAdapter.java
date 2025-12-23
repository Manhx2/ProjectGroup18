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

    public FriendRequestAdapter(List<FriendRequest> requestList) { this.requestList = requestList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequest req = requestList.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        holder.tvName.setText(req.getFromName()); // Dùng Name trực tiếp

        holder.btnAccept.setOnClickListener(v -> {
            String pId = req.getFromId();
            db.collection("users").document(myId).update("friends", FieldValue.arrayUnion(pId));
            db.collection("users").document(pId).update("friends", FieldValue.arrayUnion(myId));
            updateRequestStatus(pId, myId, "accepted");
            Toast.makeText(v.getContext(), "Đã kết bạn với " + req.getFromName(), Toast.LENGTH_SHORT).show();
            removeItem(position);
        });

        holder.btnReject.setOnClickListener(v -> {
            updateRequestStatus(req.getFromId(), myId, "rejected");
            removeItem(position);
        });
    }

    private void updateRequestStatus(String fromId, String toId, String status) {
        FirebaseFirestore.getInstance().collection("friend_requests")
                .whereEqualTo("fromId", fromId).whereEqualTo("toId", toId).whereEqualTo("status", "pending")
                .get().addOnSuccessListener(q -> {
                    if (!q.isEmpty()) q.getDocuments().get(0).getReference().update("status", status);
                });
    }

    private void removeItem(int pos) {
        if (pos < requestList.size()) { requestList.remove(pos); notifyItemRemoved(pos); notifyItemRangeChanged(pos, requestList.size()); }
    }

    @Override
    public int getItemCount() { return requestList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName; ImageView btnAccept, btnReject;
        ViewHolder(View v) { super(v); tvName = v.findViewById(R.id.tvName); btnAccept = v.findViewById(R.id.btnAccept); btnReject = v.findViewById(R.id.btnReject); }
    }
}