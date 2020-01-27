package com.devian.detected.main.map;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devian.detected.R;
import com.devian.detected.utils.domain.Task;
import com.devian.detected.utils.network.GsonSerializer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

public class MapFragment extends Fragment implements
        OnMapReadyCallback, View.OnClickListener, MapContract.View {

    private static final String TAG = "MapFragment";

    private Context mContext;
    private View mView;
    private MapPresenter mapPresenter;

    private static String MAP_STYLE;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbolManager = null;
    private Bundle savedBundle;

    @BindView(R.id.fab_map_refresh)
    FloatingActionButton fab_refresh;
    private Animation fab_rotate;

    private ArrayList<Task> markers;

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

        setupMVP();

        return mView;
    }

    private void setupMVP() {
        mapPresenter = new MapPresenter(this);
    }

    private void getMarkers() {
        showProgress();
        mapPresenter.getMarkers();
    }

    @Override
    public void displayMarkers(ArrayList<Task> markers) {
        hideProgress();
        if (symbolManager == null)
            return;
        symbolManager.deleteAll();
        for (Task t : markers) {
            symbolManager.create(new SymbolOptions()
                    .withLatLng(new LatLng(t.getLatitude(), t.getLongitude()))
                    .withIconImage("marker")
                    .withIconSize(0.6f));
        }
        this.markers = markers;
    }

    private void checkSavedBundle(Bundle inState) {
        Log.d(TAG, "checkSavedBundle");
        if (inState != null) {
            markers = inState.getParcelableArrayList("markers");
            displayMarkers(markers);
        } else {
            getMarkers();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        outState.putParcelableArrayList("markers", markers);
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

        this.mapboxMap = mapboxMap;
    }

    @Override
    public void displayError(Throwable t) {
        hideProgress();
        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgress() {
        fab_refresh.startAnimation(fab_rotate);
    }

    @Override
    public void hideProgress() {
        fab_refresh.clearAnimation();
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
}
