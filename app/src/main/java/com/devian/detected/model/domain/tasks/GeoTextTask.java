package com.devian.detected.model.domain.tasks;

import android.os.Parcel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@SuppressWarnings("unused")
public class GeoTextTask extends Task {

    private String title;
    private String description;
    private String imgUrl;
    
    public GeoTextTask(String tagId, String executor) {
        super(tagId, executor);
    }
    
    private GeoTextTask(Parcel in) {
        super(in);
        this.title = in.readString();
        this.description = in.readString();
        this.imgUrl = in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(imgUrl);
    }
    
    public static final Creator<GeoTextTask> CREATOR = new Creator<GeoTextTask>() {
        @Override
        public GeoTextTask createFromParcel(Parcel in) {
            return new GeoTextTask(in);
        }
        
        @Override
        public GeoTextTask[] newArray(int size) {
            return new GeoTextTask[size];
        }
    };
}
