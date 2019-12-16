package com.devian.detected.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devian.detected.R;
import com.devian.detected.utils.Network.NetworkService;
import com.devian.detected.utils.domain.ServerResponse;
import com.devian.detected.utils.domain.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    
    private static final String TAG = "MapFragment";
    
    private static final String MAP_STYLE = "mapbox://styles/aminovmaksim/ck4397jle16vo1cpalemp5ddc";
    
    private Gson gson = new Gson();
    
    private MapView mapView;
    private SymbolManager symbolManager = null;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        Mapbox.getInstance(getContext(), "pk.eyJ1IjoiYW1pbm92bWFrc2ltIiwiYSI6ImNrNDM2OXJqZzA0N3Izbm9icWc2ZGxhMGYifQ.mZzd2YEu-PpsODWkQMSB2g");
    
        View v = inflater.inflate(R.layout.fragment_map2, container, false);
        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        
        getMarkers();
        
        return v;
    }
    
    private final static String[] markers = {"marker_1", "marker_2", "marker_3"};
    
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
    
        final Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.marker_1);
    
        mapboxMap.setStyle(new Style.Builder().fromUri(MAP_STYLE), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                style.addImage("my-marker", bm);
                for (int i = 0; i < markers.length; i++) {
                    String mDrawableName = markers[i];
                    int resId = getResources().getIdentifier(mDrawableName , "drawable", getContext().getPackageName());
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
                    style.addImage(markers[i], bm);
                }
                symbolManager = new SymbolManager(mapView, mapboxMap, style);
                // set non-data-driven properties, such as:
                symbolManager.setIconAllowOverlap(true);
                symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);
                getMarkers();
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
    
    public void getMarkers() {
        NetworkService.getInstance().getJSONApi().getMapTasks().enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.body() == null)
                    return;
                if (response.body().getType() == ServerResponse.TYPE_TASK_SUCCESS) {
                    String json = response.body().getData();
                    Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
                    List<Task> listTask = gson.fromJson(response.body().getData(), listType);
                    updateMarkers(listTask);
                }
            }
    
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    
    public void updateMarkers(List<Task> listTask) {
        
        if (symbolManager == null)
            return;
    
        for (Task t : listTask) {
            symbolManager.create(new SymbolOptions()
                    .withLatLng(new LatLng(t.getLatitude(), t.getLongitude()))
                    .withIconImage(markers[(new Random()).nextInt(3)])
                    .withIconSize(0.3f));
        }
        
        
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
        getMarkers();
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
