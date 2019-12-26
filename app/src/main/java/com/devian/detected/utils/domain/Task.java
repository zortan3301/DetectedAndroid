package com.devian.detected.utils.domain;


import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    private long id;
    private String tagId;
    private int reward;
    private int type;
    private float Latitude;
    private float Longitude;
    private int completed;
    private String executor;
    
    private String title;
    private String description;
    private String imgUrl;
    
    public Task(String tagId, String executor) {
        this.tagId = tagId;
        this.executor = executor;
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.tagId);
        parcel.writeInt(this.reward);
        parcel.writeInt(this.type);
        parcel.writeFloat(this.Latitude);
        parcel.writeFloat(this.Longitude);
        parcel.writeInt(this.completed);
        parcel.writeString(this.executor);
    }
    
    private Task(Parcel in) {
        id = in.readLong();
        tagId = in.readString();
        reward = in.readInt();
        type = in.readInt();
        Latitude = in.readFloat();
        Longitude = in.readFloat();
        completed = in.readInt();
        executor = in.readString();
        title = in.readString();
        description = in.readString();
        imgUrl = in.readString();
    }
    
    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }
        
        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
    
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
    
    public void setExecutor(String executor) {
        this.executor = executor;
    }
    
    public long getId() {
        return id;
    }
    
    public String getTagId() {
        return tagId;
    }
    
    public int getReward() {
        return reward;
    }
    
    public int getType() {
        return type;
    }
    
    public float getLatitude() {
        return Latitude;
    }
    
    public float getLongitude() {
        return Longitude;
    }
    
    public int getCompleted() {
        return completed;
    }
    
    public String getExecutor() {
        return executor;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getImgUrl() {
        return imgUrl;
    }
    
    public static final int TYPE_MAP = 1;
    public static final int TYPE_TEXT = 2;
    
    @Override
    public int describeContents() {
        return 0;
    }
}
