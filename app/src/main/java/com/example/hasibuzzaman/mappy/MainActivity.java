package com.example.hasibuzzaman.mappy;


//https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=13:21:DB:DC:4B:2D:35:D0:D1:8E:E3:CA:7A:D4:2F:05:39:97:B4:E4%3Bcom.example.hasibuzzaman.mappy

import android.support.design.widget.FloatingActionButton;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MainActivity extends AppCompatActivity implements PlaceSelectionListener {

    private PlacePicker.IntentBuilder builder;
    private static final int BOUNDS_RADIUS = 2000;
    LatLngBounds latLngBounds;

    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private static final int PLACE_PICKER_FLAG = 1;
    private static final int REQUEST_SELECT_PLACE = 1000;
    private TextView locationTextView;
    private TextView attributionsTextView;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-34.041058, 150.890200));
    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        locationTextView = (TextView) findViewById(R.id.txt_location);
        attributionsTextView = (TextView) findViewById(R.id.txt_attributions);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);



        // Method #1
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("Search a Location");
        autocompleteFragment.setBoundsBias(BOUNDS_MOUNTAIN_VIEW);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Method #2
                try {
                    /*Intent intent = new PlaceAutocomplete.IntentBuilder
                            (PlaceAutocomplete.MODE_OVERLAY)
                            .setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                            .build(MainActivity.this);*/


                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                            .build();

                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setFilter(typeFilter)
                                    .build(MainActivity.this);

                    startActivityForResult(intent, REQUEST_SELECT_PLACE);
                } catch (GooglePlayServicesRepairableException |
                        GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            // Method #3
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder
                        (PlaceAutocomplete.MODE_OVERLAY)
                        .setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                        .build(MainActivity.this);
                startActivityForResult(intent, REQUEST_SELECT_PLACE);
            } catch (GooglePlayServicesRepairableException |
                    GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlaceSelected(Place place) {
        final LatLng latLngy = place.getLatLng();
        //final LatLngBounds viewport = place.getViewport();
         latLngBounds = DataUtils.latitudeLongitudeToBounds(latLngy, BOUNDS_RADIUS);
      //  Log.e("Select korse ", viewport.toString());

        builder = new PlacePicker.IntentBuilder();


        Intent intent = null;
        try {
            intent = builder.build(MainActivity.this);
            builder.setLatLngBounds(latLngBounds);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        // Start the Intent by requesting a result, identified by a request code.
        startActivityForResult(intent, PLACE_PICKER_FLAG);

        /*Log.i(LOG_TAG, "Place Selected: " + place.getName());
        locationTextView.setText(getString(R.string.formatted_place_data, place
                .getName(), place.getAddress(), place.getPhoneNumber(), place
                .getWebsiteUri(), place.getRating(), place.getId()));
        if (!TextUtils.isEmpty(place.getAttributions())){
            attributionsTextView.setText(Html.fromHtml(place.getAttributions().toString()));
        }*/
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                 place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
                Log.e("Toi call hoisost kere", place.getViewport().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
        }

        else if(requestCode == PLACE_PICKER_FLAG)
        {
            place = PlaceAutocomplete.getPlace(this, data);
            this.onPlaceSelected(place);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}