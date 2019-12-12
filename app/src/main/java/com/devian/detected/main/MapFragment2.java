package com.devian.detected.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devian.detected.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

public class MapFragment2 extends Fragment implements OnMapReadyCallback {
    
    private static final String TAG = "MapFragment2";
    
    private static final String MAP_STYLE = "mapbox://styles/aminovmaksim/ck4397jle16vo1cpalemp5ddc";
    
    private MapView mapView;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        Mapbox.getInstance(getContext(), "pk.eyJ1IjoiYW1pbm92bWFrc2ltIiwiYSI6ImNrNDM2OXJqZzA0N3Izbm9icWc2ZGxhMGYifQ.mZzd2YEu-PpsODWkQMSB2g");
    
        View v = inflater.inflate(R.layout.fragment_map2, container, false);
        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        
        return v;
    }
    
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(new Style.Builder().fromUri(MAP_STYLE), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
            
            }
        });
        mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(55.7, 37.6)) // Sets the new camera position
                .zoom(10) // Sets the zoom
                .bearing(360) // Rotate the camera
                .tilt(45) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder
    
        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 7000);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
