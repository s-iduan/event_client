package com.example.event_cord.model;

import com.google.gson.annotations.SerializedName;

public class UserEventPair {
    @SerializedName("user_id")
    private int mUserId;
    @SerializedName("event_id")
    private int mEventId;
    @SerializedName("time_stamp")
    private long mTimestamp;

    public UserEventPair(int userId, int eventId, long timestamp) {
        mUserId = userId;
        mEventId = eventId;
        mTimestamp = timestamp;
    }

    public int getUserId() {
        return mUserId;
    }

    public int getEventId() {
        return mEventId;
    }

    public long getTimestamp() {
        return mTimestamp;
    }
}
