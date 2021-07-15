package com.example.event_cord.model;

public class Message {
    private int mId;
    private int mSenderId;
    private int mEventId;
    private String mContent;
    private long mTimestamp;

    public Message(
            int id,
            int senderId,
            int eventId,
            String content,
            long timestamp
    ) {
        mId = id;
        mSenderId = senderId;
        mEventId = eventId;
        mContent = content;
        mTimestamp = timestamp;
    }

    public int getId() {
        return mId;
    }

    public int getSenderId() {
        return mSenderId;
    }

    public int getEventId() {
        return mEventId;
    }

    public String getContent() {
        return mContent;
    }

    public long getTimestamp() {
        return mTimestamp;
    }
}
