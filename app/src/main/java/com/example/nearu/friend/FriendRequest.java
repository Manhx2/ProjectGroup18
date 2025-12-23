package com.example.nearu.friend;

public class FriendRequest {
    private String fromId;
    private String fromName; // Tên người gửi
    private String toId;
    private String toName;   // Tên người nhận
    private String status;

    public FriendRequest() { }

    public FriendRequest(String fromId, String fromName, String toId, String toName, String status) {
        this.fromId = fromId;
        this.fromName = fromName;
        this.toId = toId;
        this.toName = toName;
        this.status = status;
    }

    // Getters and Setters
    public String getFromId() { return fromId; }
    public String getFromName() { return fromName; }
    public String getToId() { return toId; }
    public String getToName() { return toName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}