package com.devian.detected.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.devian.detected.R;
import com.devian.detected.utils.Network.NetworkService;
import com.devian.detected.utils.Network.ServerResponse;
import com.devian.detected.utils.domain.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    
    private static final String TAG = "MapFragment";
    
    private Context mContext;
    private View mView;
    
    private static String MAP_STYLE;
    private Gson gson = new Gson();
    private MapView mapView;
    private SymbolManager symbolManager = null;
    private Bundle savedBundle;
    
    @BindView(R.id.fab_map_refresh)
    FloatingActionButton fab_refresh;
    private Animation fab_rotate;
    
    private ArrayList<Task> tasks;
    
    private Call<ServerResponse> callGetTasks;
    
    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        mContext = context;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        Mapbox.getInstance(mContext, getResources().getString(R.string.mapbox_access_token));
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, mView);
        
        MAP_STYLE = getResources().getString(R.string.map_style);
        mapView = mView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    
        savedBundle = savedInstanceState;
        fab_refresh.setOnClickListener(this);
        fab_rotate = AnimationUtils.loadAnimation(getContext(), R.anim.fab_full_rotate);
    
        return mView;
    }
    
    private void checkSavedBundle(Bundle inState) {
        Log.d(TAG, "checkSavedBundle");
        if (inState != null) {
            tasks = inState.getParcelableArrayList("tasks");
            updateMarkers();
        } else {
            getMarkers();
        }
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        outState.putParcelableArrayList("tasks", tasks);
    }
    
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_map_refresh) {
            fab_refresh.startAnimation(fab_rotate);
            getMarkers();
        }
    }
    
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        Log.d(TAG, "onMapReady");
        mapboxMap.setStyle(new Style.Builder().fromUri(MAP_STYLE), style -> {
            Bitmap bitmapMarker = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
            style.addImage("marker", bitmapMarker);
        
            symbolManager = new SymbolManager(mapView, mapboxMap, style);
            symbolManager.setIconAllowOverlap(true);
            symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);
            symbolManager.addClickListener(symbol -> {
                DecimalFormat df = new DecimalFormat("#.######");
                DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
                formatSymbols.setDecimalSeparator('.');
                df.setDecimalFormatSymbols(formatSymbols);
                df.setRoundingMode(RoundingMode.CEILING);
                String lat = df.format(symbol.getLatLng().getLatitude());
                String lng = df.format(symbol.getLatLng().getLongitude());
                final String snack_text = lat + ", " + lng;
            
                Snackbar.make(mView, snack_text, Snackbar.LENGTH_LONG)
                        .setAction("Копировать", view -> {
                            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("LatLng", snack_text);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            }
                        }).show();
            });
            checkSavedBundle(savedBundle);
        });
        
        mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(55.7, 37.6))
                .zoom(10)
                .bearing(360)
                .tilt(45)
                .build();
        
        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 5000);
    }
    
    private void getMarkers() {
        Log.d(TAG, "getMarkers");
        callGetTasks = NetworkService.getInstance().getApi().getMapTasks();
        callGetTasks.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call,
                                   @NonNull Response<ServerResponse> response) {
                if (response.body() == null) {
                    Log.e(TAG, "updateTasks onResponse: response body is null");
                    return;
                }
                if (response.body().getType() == ServerResponse.TYPE_TASK_SUCCESS) {
                    Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
                    tasks = gson.fromJson(response.body().getData(), listType);
                    updateMarkers();
                }
            }
    
            @Override
            public void onFailure(@NonNull Call<ServerResponse> call,
                                  @NonNull Throwable t) {
                fab_refresh.clearAnimation();
                if (!call.isCanceled()) {
                    Log.d(TAG, "callGetTasks onFailure: call is cancelled");
                } else {
                    t.printStackTrace();
                }
            }
        });
    }
    
    private void updateMarkers() {
        Log.d(TAG, "updateMarkers");
        if (symbolManager == null)
            return;
        if (tasks == null)
            return;
        fab_refresh.clearAnimation();
        for (Task t : tasks) {
            symbolManager.create(new SymbolOptions()
                    .withLatLng(new LatLng(t.getLatitude(), t.getLongitude()))
                    .withIconImage("marker")
                    .withIconSize(0.6f));
        }
    }
    
    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        mapView.onStart();
    }
    
    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mapView.onResume();
    }
    
    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        mapView.onPause();
    }
    
    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        mapView.onStop();
    }
    
    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
        mapView.onLowMemory();
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mapView.onDestroy();
        if (callGetTasks != null)
            callGetTasks.cancel();
    }
}
