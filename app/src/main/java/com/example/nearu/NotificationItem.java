package com.example.nearu;

public class NotificationItem {
    public String id;
    public String title;
    public String content;
    public String type;
    public String fromUid;
    public boolean read;
    public long time;

    public NotificationItem() {}

    public NotificationItem(String id, String title, String content,
                            String type, String fromUid,
                            boolean read, long time) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.fromUid = fromUid;
        this.read = read;
        this.time = time;
    }
}
