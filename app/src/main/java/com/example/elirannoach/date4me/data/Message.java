package com.example.elirannoach.date4me.data;

public class Message {
    private String mFrom;
    private String mTo;
    private String mMessageID;
    private String mValue;

    public Message(String mFrom, String mTo, String mMessageID, String mValue) {
        this.mFrom = mFrom;
        this.mTo = mTo;
        this.mMessageID = mMessageID;
        this.mValue = mValue;
    }

    public Message(){}

    public String getmFrom() {
        return mFrom;
    }

    public String getmTo() {
        return mTo;
    }

    public String getmMessageID() {
        return mMessageID;
    }

    public String getmValue() {
        return mValue;
    }
}
