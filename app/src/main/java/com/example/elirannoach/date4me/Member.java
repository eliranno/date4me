package com.example.elirannoach.date4me;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.SerializedName;

public class Member {
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

    private Member(String mUid, String mName, String nGender, String mDob, String mCity, String mState, String mReligion, String mOccupation, String mDescription) {
        this.mUid = mUid;
        this.mName = mName;
        this.mGender = nGender;
        this.mDob = mDob;
        this.mCity = mCity;
        this.mState = mState;
        this.mReligion = mReligion;
        this.mOccupation = mOccupation;
        this.mDescription = mDescription;
    }

    public Member(){}

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

        public Member build(){
            return new Member(this.mUid,this.mName,this.mGender,this.mDob,this.mCity,this.mState,this.mReligion,this.mOccupation,this.mDescription);
        }


    }
}
