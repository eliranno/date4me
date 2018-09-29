package com.example.elirannoach.date4me.data;

public class Message {
    private Member mFrom;
    private Member mTo;
    private String mMessageID;
    private String Value;

    public Message(Member mFrom, Member mTo, String mMessageID, String value) {
        this.mFrom = mFrom;
        this.mTo = mTo;
        this.mMessageID = mMessageID;
        Value = value;
    }

    public Member getmFrom() {
        return mFrom;
    }

    public Member getmTo() {
        return mTo;
    }

    public String getmMessageID() {
        return mMessageID;
    }

    public String getValue() {
        return Value;
    }
}
