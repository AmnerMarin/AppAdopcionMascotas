package sistemas.unc.edu.appadopcionmascotas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ActividadMapas extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker marcadorSeleccionado;

    private double latSeleccionada;
    private double lngSeleccionada;
    private String direccionSeleccionada = "";

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final int LOCATION_SETTINGS_REQUEST = 200;

    private ActivityResultLauncher<Intent> autocompleteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_mapas);

        inicializarPlaces();
        inicializarMapa();
        inicializarBuscador();

        findViewById(R.id.btnBuscar).setOnClickListener(v -> abrirBuscador());
        findViewById(R.id.btnGuardarUbicacion).setOnClickListener(v -> guardarUbicacion());
    }

    // 🔹 Inicializar Places API
    private void inicializarPlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBKQ_NetM9ThDvtI0NCDznAo115LZVR774");
        }
    }

    // 🔹 Inicializar mapa
    private void inicializarMapa() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // 🔹 Inicializar buscador de lugares
    private void inicializarBuscador() {
        autocompleteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Place place = Autocomplete.getPlaceFromIntent(result.getData());
                        if (place.getLatLng() != null) {
                            actualizarMarcador(place.getLatLng(), null); // Usamos marcador único
                        }
                    }
                });
    }

    // 🔹 Configurar mapa cuando esté listo
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        configurarMapa();
        verificarUbicacionActivada();
    }

    // 🔹 Configurar interacciones del mapa
    private void configurarMapa() {
        // Click en cualquier lugar del mapa
        mMap.setOnMapClickListener(latLng -> actualizarMarcador(latLng, null));

        // Click en el botón azul de ubicación
        mMap.setOnMyLocationButtonClickListener(() -> {
            verificarUbicacionActivada();
            return false;
        });

        // Click en POI (lugares de interés)
        mMap.setOnPoiClickListener(poi -> actualizarMarcador(poi.latLng, poi.name));
    }

    // 🔹 Método único para colocar marcador y mover cámara
    private void actualizarMarcador(LatLng latLng, String titulo) {
        String direccion = obtenerDireccion(latLng);

        if (marcadorSeleccionado != null) marcadorSeleccionado.remove();

        latSeleccionada = latLng.latitude;
        lngSeleccionada = latLng.longitude;
        direccionSeleccionada = direccion;

        marcadorSeleccionado = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(titulo != null ? titulo : "Ubicación seleccionada")
                .snippet(direccion));

        marcadorSeleccionado.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }

    // 🔹 Obtener dirección desde coordenadas
    private String obtenerDireccion(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return "Dirección no encontrada";
    }

    // 🔹 Abrir buscador de lugares
    private void abrirBuscador() {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
        );
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        autocompleteLauncher.launch(intent);
    }

    // 🔹 Guardar ubicación seleccionada
    private void guardarUbicacion() {
        Intent intent = new Intent();
        intent.putExtra("lat", latSeleccionada);
        intent.putExtra("lng", lngSeleccionada);
        intent.putExtra("direccion", direccionSeleccionada);
        setResult(RESULT_OK, intent);
        finish();
    }

    // 🔹 Verificar permisos y activación de ubicación
    private void verificarUbicacionActivada() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(res -> obtenerUbicacionActual())
                .addOnFailureListener(e -> {
                    if (e instanceof com.google.android.gms.common.api.ResolvableApiException) {
                        try {
                            ((com.google.android.gms.common.api.ResolvableApiException) e)
                                    .startResolutionForResult(this, LOCATION_SETTINGS_REQUEST);
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                });
    }

    // 🔹 Obtener ubicación actual
    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                        actualizarMarcador(miUbicacion, "Ubicación Actual");
                    }
                });
    }

    // 🔹 Manejar permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            verificarUbicacionActivada();
        }
    }

    // 🔹 Resultado de activación de ubicación
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS_REQUEST && resultCode == RESULT_OK) {
            obtenerUbicacionActual();
        }
    }
}