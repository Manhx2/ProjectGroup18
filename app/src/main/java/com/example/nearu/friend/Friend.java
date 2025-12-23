package com.example.nearu.friend;

public class Friend {
    private String name;
    private int avatarRes;
    private boolean isOnline;

    public Friend(String name, int avatarRes, boolean isOnline) {
        this.name = name;
        this.avatarRes = avatarRes;
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public int getAvatarRes() {
        return avatarRes;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
