package com.example.nearu;

import android.view.*;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter
        extends RecyclerView.Adapter<NotificationAdapter.VH> {

    List<NotificationItem> list;

    public NotificationAdapter(List<NotificationItem> list) {
        this.list = list;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_notification, p, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH h, int i) {
        NotificationItem n = list.get(i);
        h.title.setText(n.title);
        h.content.setText(n.content);
        h.itemView.setAlpha(n.read ? 0.6f : 1f);
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, content;
        VH(View v) {
            super(v);
            title = v.findViewById(R.id.txtTitle);
            content = v.findViewById(R.id.txtContent);
        }
    }
}
