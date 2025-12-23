package com.example.nearu.friend;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nearu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<Friend> friendList;

    public FriendAdapter(List<Friend> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend f = friendList.get(position);
        holder.tvName.setText(f.getName());
        holder.tvStatus.setText(f.isOnline() ? "Trực tuyến" : "Ngoại tuyến");

        // Xử lý xóa bạn khi nhấn giữ
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa bạn bè")
                    .setMessage("Bạn có chắc chắn muốn xóa " + f.getName() + " khỏi danh sách bạn bè?")
                    .setPositiveButton("Xóa", (dialog, which) -> deleteFriend(f, position, v))
                    .setNegativeButton("Hủy", null)
                    .show();
            return true;
        });
    }

    private void deleteFriend(Friend friend, int position, View v) {
        String myId = FirebaseAuth.getInstance().getUid();
        if (myId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(myId)
                .update("friends", FieldValue.arrayRemove(friend.getUid()));

        db.collection("users").document(friend.getUid())
                .update("friends", FieldValue.arrayRemove(myId))
                .addOnSuccessListener(aVoid -> {
                    friendList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(v.getContext(), "Đã xóa bạn bè", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}