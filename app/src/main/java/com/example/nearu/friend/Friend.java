package com.example.nearu.friend;

public class Friend {
    private String uid;
    private String name;
    private String avatarUrl;
    private int avatarRes;
    private boolean isOnline;
    public Friend() {}

    public Friend(String uid, String name, int avatarRes, boolean isOnline) {
        this.uid = uid;
        this.name = name;
        this.avatarRes = avatarRes;
        this.isOnline = isOnline;
    }

    // Getter và Setter
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvatarRes() {
        return avatarRes;
    }

    public void setAvatarRes(int avatarRes) {
        this.avatarRes = avatarRes;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}