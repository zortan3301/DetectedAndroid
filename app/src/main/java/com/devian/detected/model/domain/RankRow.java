package com.devian.detected.model.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class RankRow implements Parcelable {
    private String uid;
    private long rank;
    private String nickname;
    private long points;
    
    public RankRow() {
    }
    
    private RankRow(Parcel in) {
        uid = in.readString();
        rank = in.readLong();
        nickname = in.readString();
        points = in.readLong();
    }
    
    public static final Creator<RankRow> CREATOR = new Creator<RankRow>() {
        @Override
        public RankRow createFromParcel(Parcel in) {
            return new RankRow(in);
        }
        
        @Override
        public RankRow[] newArray(int size) {
            return new RankRow[size];
        }
    };
    
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.uid);
        parcel.writeLong(this.rank);
        parcel.writeString(this.nickname);
        parcel.writeLong(this.points);
    }
    
    public long getRank() {
        return rank;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public long getPoints() {
        return points;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
}
