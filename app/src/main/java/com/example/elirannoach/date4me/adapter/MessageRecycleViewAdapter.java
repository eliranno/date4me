package com.example.elirannoach.date4me.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elirannoach.date4me.R;
import com.example.elirannoach.date4me.data.Member;
import com.example.elirannoach.date4me.data.Message;
import com.example.elirannoach.date4me.utils.FireBaseUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageRecycleViewAdapter extends RecyclerView.Adapter<MessageRecycleViewAdapter.MessageViewHolder> {
    private List<Message> mMessageList;
    private Context mContext;
    private Member mMyProfile;
    private Member mMember;
    private String mConversationID;

    public MessageRecycleViewAdapter(Context context, List<Message> messsageList,Member myProfile , Member member,String conversationID){
        mContext = context;
        mMessageList = messsageList;
        mMyProfile = myProfile;
        mMember = member;
        mConversationID =conversationID;
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View memberCardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_layout, parent, false);
        MessageViewHolder vh = new MessageViewHolder(memberCardView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = mMessageList.get(position);
        if (msg.getmFrom().equals(mMyProfile.mUid))
            holder.mName.setText(mMyProfile.mName);
        else
            holder.mName.setText(mMember.mName);
        holder.mMessage.setText(msg.getmValue());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView mName;
        TextView mMessage;
        CircleImageView mProfileImage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.text_message_name);
            mMessage = itemView.findViewById(R.id.text_message_body);
            mProfileImage = itemView.findViewById(R.id.chat_member_image);
        }
    }

}
