package com.devian.detected.view.map_tab;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.devian.detected.R;
import com.devian.detected.model.domain.tasks.GeoTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapFragment extends Fragment
        implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener {
    
    private static final String TAG = "MapFragment";
    
    private GoogleMap mMap;
    
    private MapViewModel viewModel;
    
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.fab_map_refresh)
    FloatingActionButton fab_refresh;
    private Animation fab_rotate;
    
    private ArrayList<GeoTask> geoTasks;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, v);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        
        setupView();
        bindView();
        return v;
    }
    
    private void bindView() {
        Log.d(TAG, "bindView: ");
        viewModel = ViewModelProviders.of(getActivityNonNull()).get(MapViewModel.class);
        viewModel.bindGeoTasks().observe(this, markersDataWrapper -> {
            hideProgress();
            if (markersDataWrapper.getObject() != null)
                geoTasks = new ArrayList<>(markersDataWrapper.getObject());
            displayGeoTasks(geoTasks);
        });
    }
    
    private void setupView() {
        fab_refresh.setOnClickListener(this);
        fab_rotate = AnimationUtils.loadAnimation(getContext(), R.anim.fab_full_rotate);
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mMap = googleMap;
        setMapStyle();
        setMapView();
        updateGeoTasks();
    }
    
    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");
        if (view.getId() == R.id.fab_map_refresh) {
            if (isRefreshAvailable()) {
                showProgress();
                updateGeoTasks();
            } else
                hideProgress();
        }
    }
    
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    
    private void updateGeoTasks() {
        Log.d(TAG, "updateGeoTasks: ");
        showProgress();
        viewModel.updateGeoTasks();
    }
    
    private void displayGeoTasks(ArrayList<GeoTask> geoTasks) {
        Log.d(TAG, "displayMarkers: ");
        if (geoTasks == null || geoTasks.isEmpty()) {
            return;
        }
        mMap.clear();
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        for (GeoTask geoTask : geoTasks) {
            mMap.addMarker(new MarkerOptions()
                    .icon(markerIcon)
                    .position(new LatLng(geoTask.getLatitude(), geoTask.getLongitude()))
            );
        }
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
    }
    
    private void setMapStyle() {
        Log.d(TAG, "setMapStyle: ");
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivityNonNull(), R.raw.map_style));
            if (!success) {
                Log.e(TAG, "setMapStyle: Style parsing failed");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }
    
    private Date lastRefresh = new Date();

    private boolean isRefreshAvailable() {
        Date currTime = new Date();
        if (currTime.getTime() - lastRefresh.getTime() >= getResources().getInteger(R.integer.refresh_delay)) {
            lastRefresh = currTime;
            return true;
        } else {
            return false;
        }
    }
    
    private void showProgress() {
        Log.d(TAG, "showProgress: ");
        fab_refresh.startAnimation(fab_rotate);
    }

    private void hideProgress() {
        Log.d(TAG, "hideProgress: ");
        fab_refresh.clearAnimation();
    }
    
    private FragmentActivity getActivityNonNull() {
        Log.d(TAG, "getActivityNonNull: ");
        if (super.getActivity() != null) {
            return super.getActivity();
        } else {
            throw new RuntimeException("null returned from getActivity()");
        }
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
