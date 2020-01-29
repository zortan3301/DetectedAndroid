package com.devian.detected.model.domain.tasks;

import android.os.Parcel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@SuppressWarnings("unused")
public class GeoTask extends Task {

    private float Latitude;
    private float Longitude;
    
    public GeoTask(String tagId, String executor) {
        super(tagId, executor);
    }
    
    private GeoTask(Parcel in) {
        super(in);
        this.Latitude = in.readFloat();
        this.Longitude = in.readFloat();
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeFloat(this.Latitude);
        parcel.writeFloat(this.Longitude);
    }
    
    public static final Creator<GeoTask> CREATOR = new Creator<GeoTask>() {
        @Override
        public GeoTask createFromParcel(Parcel in) {
            return new GeoTask(in);
        }
        
        @Override
        public GeoTask[] newArray(int size) {
            return new GeoTask[size];
        }
    };
    
}
