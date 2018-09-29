package com.example.elirannoach.date4me.data;

import java.util.List;

public class Conversation {
    private String mConversationID;
    private List<Message> mMessageList;
    private List<Member> mConversationMemberList;


    public Conversation(String mConversationID, List<Message> mMessageList, List<Member> mConversationMemberList) {

        this.mConversationID = mConversationID;
        this.mMessageList = mMessageList;
        this.mConversationMemberList = mConversationMemberList;
    }

    public String getmConversationID() {
        return mConversationID;
    }

    public List<Message> getmMessageList() {
        return mMessageList;
    }

    public List<Member> getmConversationMemberList() {
        return mConversationMemberList;
    }
}
