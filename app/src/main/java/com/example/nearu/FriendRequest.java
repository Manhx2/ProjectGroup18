package com.example.nearu;

public class FriendRequest {
    private String fromId;
    private String toId;
    private String status;

    public FriendRequest() {}

    public FriendRequest(String fromId, String toId, String status) {
        this.fromId = fromId;
        this.toId = toId;
        this.status = status;
    }

    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    public String getStatus() {
        return status;
    }
}
