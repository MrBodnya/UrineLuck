package com.urineluck.urineluck;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MActivity extends AppCompatActivity {

    protected static final int SUB_ACTIVITY_REQUEST_CODE = 100;

    public static List<LatLng> ourLocs = new ArrayList<LatLng>();

    LatLng getCurrentLocation() {

        Context context = getApplicationContext();
        LocationManager locator = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location here = getLastKnownLocation(locator);
        LatLng me = new LatLng(here.getLatitude(), here.getLongitude());
        return me;
    }

    private Location getLastKnownLocation(LocationManager locator) {
        List<String> providers = locator.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }
            Location l = locator.getLastKnownLocation(provider);
            //ALog.d("last known location, provider: %s, location: %s", provider,
            //      l);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                //ALog.d("found best last known location: %s", l);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    ////////////////////////////////////////////////////////////////////
    // Getting the JSON API:
    //

    //TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//            System.out.println("BEFORE REQUEST");
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
//                    123);
//            System.out.println("REQUESTING");
//            //return;
//        }

        // API call:
        //HttpClient client = new DefaultHttpClient();


//        view = findViewById(android.R.id.content);

//        Snackbar.make(view, result, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
//        // our "database":


        Button nmButton = (Button) findViewById(R.id.nearMeButton);

        nmButton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
//                Snackbar.make(view, result, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent("com.google.android.gms.maps.SupportMapFragment");
                startActivity(intent);
            }

        });

        /*
        Button bnmButton = (Button) findViewById(R.id.bNearMeButton);

        bnmButton.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent("");
                startActivity(intent);
            }

        });
*/
        Button aNButton = (Button) findViewById(R.id.addNewButton);

        aNButton.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v){
                LatLng loc = getCurrentLocation();
                ourLocs.add(loc);
//                Intent intent = new Intent("");
//                startActivity(intent);
            }

        });


        Button gButton = (Button) findViewById(R.id.gameButton);

        gButton.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent("com.urineluck.urineluck.GameActivity");
                startActivity(intent);
            }

        });



    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SUB_ACTIVITY_REQUEST_CODE) {
//            Bundle b = data.getExtras();
//            TextView result = (TextView) findViewById(R.id.result);
//            result.setText(b.getString("TEXT"));
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
