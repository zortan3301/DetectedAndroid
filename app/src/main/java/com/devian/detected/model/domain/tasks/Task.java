package com.devian.detected.model.domain.tasks;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@SuppressWarnings("unused")
public class Task implements Parcelable, Comparable<Task> {
    
    private String tagId;
    private int reward;
    private boolean completed;
    private String executor;
    private Date completedTime;
    
    public Task(String tagId, String executor) {
        this.tagId = tagId;
        this.executor = executor;
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.tagId);
        parcel.writeInt(this.reward);
        parcel.writeInt(booleanToInt(this.completed));
    }
    
    protected Task(Parcel in) {
        tagId = in.readString();
        reward = in.readInt();
        completed = intToBoolean(in.readInt());
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
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public int compareTo(Task task) {
        return tagId.compareTo(task.tagId);
    }
    
    private int booleanToInt(boolean b) {
        return b?1:0;
    }
    
    private boolean intToBoolean(int i) {
        return i != 0;
    }
    
    public static final int GEO_TAG = 1;
    public static final int GEO_TEXT_TAG = 2;
}
