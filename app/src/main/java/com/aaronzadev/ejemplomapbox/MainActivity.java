package com.aaronzadev.ejemplomapbox;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionsListener {

    private MapView mMapview;
    private PermissionsManager mPManager;
    private MapboxMap mMap;
    private Style currStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_api_key));
        setContentView(R.layout.activity_main);

        mMapview = findViewById(R.id.mapView);
        mMapview.onCreate(savedInstanceState);

        Button btnStart = findViewById(R.id.btnStart);

        mMapview.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mMap = mapboxMap;

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        currStyle = style;
                        enableLocationComponent();

                    }
                });

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enableLocationComponent();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapview.onStart();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapview.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapview.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapview.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapview.onDestroy();
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent() {

        // Verificar si el permiso esta habilitado
        if (PermissionsManager.areLocationPermissionsGranted(this)) {


            LocationComponent locationComponent = mMap.getLocationComponent();

            //Pasar como parametro un objeto de la clase LocationEngine
            //el cual permite determinar el mejor proveedor de ubicacion entre
            //Fusedlocation API y el propio gps
            //locationComponent.setLocationEngine();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.
                            builder(this, currStyle).build());

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.NONE);

            locationComponent.setRenderMode(RenderMode.COMPASS);

            LatLng currLoc = new LatLng();
            currLoc.setLatitude(locationComponent.getLastKnownLocation().getLatitude());
            currLoc.setLongitude(locationComponent.getLastKnownLocation().getLongitude());

            //Crear un objeto de CameraPosition que se colocara en la ubiacion actual
            CameraPosition position = new CameraPosition.Builder()
                    .target(currLoc)
                    .zoom(16)
                    .tilt(20)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 3000);


        } else {

            mPManager = new PermissionsManager(this);
            mPManager.requestLocationPermissions(this);

        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

        Toast.makeText(this, "Para el correcto funcionamiento de la aplicacion" +
                "es necesario conceder el acceso a la ubicacion", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) {

            enableLocationComponent();

        } else {

            if (mPManager != null) {

                mPManager.requestLocationPermissions(this);

            }

        }

    }
}
