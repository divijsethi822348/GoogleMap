package com.example.googlemap;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText editText;
    private GoogleMap map;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        editText=findViewById(R.id.edit);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
        mapFragment.getMapAsync((OnMapReadyCallback) this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //autocompleteFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                editText.setText(place.getName());
                try {
                    Search();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map=googleMap;
    }
    private void goToLocationZoom(double lst,double lng,float zoom)
    {
        LatLng latLng=new LatLng(lst,lng);
        CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(latLng,zoom);
        map.animateCamera(cameraUpdate);

    }
    public void Search() throws IOException {
        map.clear();
        String location=editText.getText().toString();
        Geocoder geocoder=new Geocoder((this));
        List<Address> list=geocoder.getFromLocationName(location,1);
        Address address=list.get(0);
        String locality=address.getLocality();
        Toast.makeText(this,locality,Toast.LENGTH_LONG).show();
        double lqt=address.getLatitude();
        double lwg=address.getLongitude();
        goToLocationZoom(lqt,lwg,10);
        LatLng latLng=new LatLng(lqt,lwg);
        map.addMarker(new MarkerOptions().position(latLng).title(location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
    }
}
