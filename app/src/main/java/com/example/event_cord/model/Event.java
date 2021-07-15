package com.example.event_cord.model;

import com.google.gson.annotations.SerializedName;

public class Event {
    @SerializedName("id")
    private int mId;
    @SerializedName("user_id")
    private int mUserId;
    @SerializedName("name")
    private String mName;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("create_time")
    private long mCreatetime;
    @SerializedName("last_modify_time")
    private long mLastModifytime;

    public Event(
        int id,
        int userId,
        String name,
        String description
    ) {
        this(id, userId, name, description, 0,0);
    }

    public Event(
            int id,
            int userId,
            String name,
            String description,
            long createTime,
            long LastModifyTime
    ) {
        mId = id;
        mUserId = userId;
        mName = name;
        mDescription = description;
        mCreatetime = createTime;
        mLastModifytime = LastModifyTime;
    }

    public int getId() {
        return mId;
    }

    public int getUserId() {
        return mUserId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public long getCreateTime() {
        return mCreatetime;
    }

    public long getLastModifytime() {
        return mLastModifytime;
    }
}
