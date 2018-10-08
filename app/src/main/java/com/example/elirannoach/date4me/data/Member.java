package com.example.elirannoach.date4me.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

public class Member implements Parcelable {
    // bad idea to make these fields public, but butterkife makes life easier so why not...
    @PropertyName("uid")
    public String mUid;
    @PropertyName("name")
    public String mName;
    @PropertyName("gender")
    public String mGender;
    @PropertyName("dob")
    public String mDob;
    @PropertyName("city")
    public String mCity;
    @PropertyName("state")
    public String mState;
    @PropertyName("religion")
    public String mReligion;
    @PropertyName("occupation")
    public String mOccupation;
    @PropertyName("description")
    public String mDescription;
    @PropertyName("profile_image_url")
    public String mProfileImageUrl;

    private String mConversationID;
    private boolean isFavorite;
    private byte[] mProfilePhoto;

    private Member(String mUid, String mName, String nGender, String mDob, String mCity, String mState, String mReligion, String mOccupation, String mDescription,String profileImageUrl) {
        this.mUid = mUid;
        this.mName = mName;
        this.mGender = nGender;
        this.mDob = mDob;
        this.mCity = mCity;
        this.mState = mState;
        this.mReligion = mReligion;
        this.mOccupation = mOccupation;
        this.mDescription = mDescription;
        this.mProfileImageUrl = profileImageUrl;
        isFavorite = false;
        this.mConversationID = null;
        mProfilePhoto = null;
    }

    public void setFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
    }

    public boolean isFavorite(){
        return this.isFavorite;
    }

    public String getConversationID(){
        return this.mConversationID;
    }

    public void setmConversationID(String conversationID){
        this.mConversationID = conversationID;
    }

    public void setProfilePhoto(byte[] bitmap){
        this.mProfilePhoto = bitmap;
    }

    public byte[] getProfilePhotoBitmap(){
        return this.mProfilePhoto;
    }

    public Member(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUid);
        dest.writeString(this.mName);
        dest.writeString(this.mGender);
        dest.writeString(this.mDob);
        dest.writeString(this.mCity);
        dest.writeString(this.mState);
        dest.writeString(this.mReligion);
        dest.writeString(this.mOccupation);
        dest.writeString(this.mDescription);
        dest.writeString(this.mProfileImageUrl);
        dest.writeInt(this.isFavorite ? 1 : 0);
        dest.writeString(this.mConversationID);
        if (this.mProfilePhoto != null) {
            dest.writeInt(this.mProfilePhoto.length);
            dest.writeByteArray(this.mProfilePhoto);
        }
        else
            dest.writeInt(0);
    }

    public Member (Parcel in){
        this.mUid = in.readString();
        this.mName = in.readString();
        this.mGender = in.readString();
        this.mDob = in.readString();
        this.mCity = in.readString();
        this.mState = in.readString();
        this.mReligion = in.readString();
        this.mOccupation = in.readString();
        this.mDescription = in.readString();
        this.mProfileImageUrl = in.readString();
        this.isFavorite = in.readInt() == 1;
        this.mConversationID = in.readString();
        int size = in.readInt();
        if (size > 0) {
            this.mProfilePhoto = new byte[size];
            in.readByteArray(this.mProfilePhoto);
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public static class MemberBuilder{
        private String mUid;
        private String mName;
        private String mGender;
        private String mDob;
        private String mCity;
        private String mState;
        private String mReligion;
        private String mOccupation;
        private String mDescription;
        private String mProfileImageUrl;


        public MemberBuilder (String uid,String name,String gender,String dob){
            this.mUid = uid;
            this.mName = name;
            this.mGender = gender;
            this.mDob = dob;
        }

        public MemberBuilder city(String city){
            this.mCity = city;
            return this;
        }

        public MemberBuilder state(String state){
            this.mState = state;
            return this;
        }

        public MemberBuilder religion(String religion){
            this.mReligion = religion;
            return this;
        }

        public MemberBuilder occupation(String occupation){
            this.mOccupation = occupation;
            return this;
        }

        public MemberBuilder description(String description){
            this.mDescription = description;
            return this;
        }

        public MemberBuilder profileImageUrl(String url){
            this.mProfileImageUrl = url;
            return this;
        }

        public Member build(){
            return new Member(this.mUid,this.mName,this.mGender,this.mDob,this.mCity,this.mState,this.mReligion,this.mOccupation,this.mDescription,this.mProfileImageUrl);
        }


    }
}
