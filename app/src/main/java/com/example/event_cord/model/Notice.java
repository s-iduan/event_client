package com.example.event_cord.model;

import com.google.gson.annotations.SerializedName;

public class Notice {
    @SerializedName("id")
    private int mId;

    @SerializedName("user_id")
    private int mUserId;

    @SerializedName("event_id")
    private int mEventId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("time_stamp")
    private long mTimestamp;

    @SerializedName("user_name")
    private String mUserName;

    @SerializedName("done")
    private int mIsDone;

    public Notice (int id, int userId, int eventId, String title, String description) {
        this(id, userId, eventId, title, description, 0, "", 0);
    }

    public Notice(
            int id,
            int userId,
            int eventId,
            String title,
            String description,
            long timestamp,
            String userName,
            int done
    ) {
        mId = id;
        mUserId = userId;
        mEventId = eventId;
        mTitle = title;
        mDescription = description;
        mTimestamp = timestamp;
        mUserName = userName;
        mIsDone = done;
    }

    public int getId() {
        return mId;
    }

    public int getUserId() {
        return mUserId;
    }

    public int getEventId() {
        return mEventId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public String getUserName() {
        return mUserName;
    }

    public int isDone() {
        return mIsDone;
    }
}
