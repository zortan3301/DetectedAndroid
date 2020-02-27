package com.devian.detected.view.extra.admin;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.devian.detected.R;
import com.devian.detected.model.domain.tasks.Tag;
import com.github.paolorotolo.appintro.ISlidePolicy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionMap extends Fragment implements ISlidePolicy, OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener {
    
    private static final String TAG = "OptionMap";
    
    private GoogleMap mMap;
    public Tag tag = new Tag();
    private Marker marker;
    
    @BindView(R.id.admin_mapView)
    MapView mapView;
    @BindView(R.id.admin_tvLatitude)
    TextView tvLatitude;
    @BindView(R.id.admin_tvLongitude)
    TextView tvLongitude;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_option_map, container, false);
        ButterKnife.bind(this, v);
        
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        
        this.tag.setLatitude(0);
        this.tag.setLongitude(0);
        
        return v;
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mMap = googleMap;
        setMapStyle();
        setMapView();
    }
    
    private void setMapView() {
        Log.d(TAG, "setMapView: ");
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMinZoomPreference(10.0f);
        LatLngBounds MOSCOW_BOUNDS = new LatLngBounds(
                new LatLng(55.575, 37.351),
                new LatLng(55.912, 37.838)
        );
        mMap.setLatLngBoundsForCameraTarget(MOSCOW_BOUNDS);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MOSCOW_BOUNDS.getCenter(), 0));
    
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        
        LatLng center = mMap.getCameraPosition().target;
        marker = mMap.addMarker(new MarkerOptions().position(center).draggable(false));
    }
    
    private void setMapStyle() {
        Log.d(TAG, "setMapStyle: ");
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivityNonNull(), R.raw.map_style_default));
            if (!success) {
                Log.e(TAG, "setMapStyle: Style parsing failed");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }
    
    @Override
    public void onCameraMove() {
        LatLng center = mMap.getCameraPosition().target;
        marker.setPosition(center);
    }
    
    @Override
    public void onCameraIdle() {
        LatLng center = mMap.getCameraPosition().target;
        tag.setLatitude((float) center.latitude);
        tag.setLongitude((float) center.longitude);
        tvLatitude.setText(String.valueOf((float) center.latitude));
        tvLongitude.setText(String.valueOf((float) center.longitude));
        
    }
    
    @Override
    public boolean isPolicyRespected() {
        Log.d(TAG, "isPolicyRespected: ");
        return tag.getLatitude() != 0;
    }
    
    @Override
    public void onUserIllegallyRequestedNextPage() {
        Log.d(TAG, "onUserIllegallyRequestedNextPage: ");
    }
    
    private FragmentActivity getActivityNonNull() {
        Log.d(TAG, "getActivityNonNull: ");
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("null returned from getActivity()");
        }
    }
    
    public void setTag(Tag tag) {
        this.tag.setTagId(tag.getTagId());
        this.tag.setType(tag.getType());
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        mapView.onResume();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
