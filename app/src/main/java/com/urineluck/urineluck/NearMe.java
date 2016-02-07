package com.urineluck.urineluck;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;

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
import java.util.HashMap;
import java.util.List;


public class NearMe extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    boolean LIVE = true; // Use LIVE API calls?

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            System.out.println("BEFORE REQUEST");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
            System.out.println("REQUESTING");
            //return;
        }

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//        } else {
//            // Show rationale and request permission.
//        }

        Context context = getApplicationContext();
        LocationManager locator = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //Criteria criteria = new Criteria();
        //criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        //String connection = locator.getBestProvider(criteria, true);
        //System.out.println("Connection String: "+ connection);
        Location here = getLastKnownLocation(locator);


        //Location here = locator.getLastKnownLocation(connection);

        // Add a marker in Sydney and move the camera
        LatLng me = new LatLng(here.getLatitude(), here.getLongitude());
        mMap.addMarker(new MarkerOptions().position(me).title("Here I Am"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
        //mMap.moveCamera(CameraUpdateFactory.zoomTo());


        HashMap<String, String[]> bathrooms = readJSON(here.getLatitude(), here.getLongitude());
        String[] lats = bathrooms.get("lat");
        String[] lons = bathrooms.get("lon");

        double dist = 0.05; //0.5 = 30 m, 1 = 60 mi
        LatLngBounds bounds = new LatLngBounds(new LatLng(here.getLatitude()-dist,here.getLongitude()-dist),
                                               new LatLng(here.getLatitude()+dist,here.getLongitude()+dist));


        for(int i=0; i<lats.length; i++) {

            //bounds.extend(new LatLng(Double.parseDouble(lats[i]), Double.parseDouble(lons[i])));
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lats[i]), Double.parseDouble(lons[i]))).title("Urinate Here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        }

        List<LatLng> ourLocs = MActivity.ourLocs;

        for(int i=0; i<ourLocs.size(); i++) {
            LatLng ll = ourLocs.get(i);
            //bounds.extend(new LatLng(Double.parseDouble(lats[i]), Double.parseDouble(lons[i])));
            mMap.addMarker(new MarkerOptions().position(ll).title("Our location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,0));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (permissions.length > 0) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    System.out.println("Permission: SUCCESS");
                } else {
                    // Show rationale and request permission.
                    System.out.println("Permission :WUMBO");
                }
            } else {
                // Permission was denied. Display an error message.
                System.out.println("Permission: FAIL");
            }
        }

    }


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        System.out.println("URL: " + sb.toString());
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Each call to this function is one API call.
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    String getURL(String lat, String lon) {
        // Gets the URL for a given latitude and longetude (in string format).
        String amenity = "toilet";
        String user = "hop-hacks16";
        String key = "7990725ad0ad340d04a71fab68f09e67";

        String out = "http://amenimaps.com/amenimapi.php?amenity=" + amenity + "&mylat=" + lat + "&mylon=" + lon + "&mode=json&name=";
        out = out + user + "&key=" + key;
        return out;
    }
    ////////////////////////////////////////////////////////////////////


    String result = "<nil>"; // JSON string goes here.
    String testJSONoffline = "{\"markers\":[\n" +
            "{ \"latitude\":51.508205, \"longitude\":-0.1285538, \"fee\":\"\", \"wheelchair\":\"yes\" },\n" +
            "{ \"latitude\":51.5089214, \"longitude\":-0.1259481, \"fee\":\"\", \"wheelchair\":\"\" },\n" +
            "{ \"latitude\":51.5082165, \"longitude\":-0.1247138, \"fee\":\"\", \"wheelchair\":\"\" },\n" +
            "{ \"latitude\":51.5081029, \"longitude\":-0.1245797, \"fee\":\"\", \"wheelchair\":\"\" },\n" +
            "{ \"latitude\":51.5098044, \"longitude\":-0.1311357, \"fee\":\"\", \"wheelchair\":\"\" },\n" +
            "{ \"latitude\":51.5107724, \"longitude\":-0.1297637, \"fee\":\"\", \"wheelchair\":\"\" },\n" +
            "{ \"latitude\":51.5075104, \"longitude\":-0.1218098, \"fee\":\"\", \"wheelchair\":\"\" },\n" +
            "{ \"latitude\":51.5046837, \"longitude\":-0.1300195, \"fee\":\"\", \"wheelchair\":\"\" },\n" +
            "{ \"latitude\":51.5114911, \"longitude\":-0.1233139, \"fee\":\"\", \"wheelchair\":\"\" },\n" +
            "{ \"latitude\":51.511713, \"longitude\":-0.1217072, \"fee\":\"\", \"wheelchair\":\"\" }\n" +
            "]}";

    public HashMap<String,String[]> readJSON(double latIn, double lonIn){

        JSONObject fetch = null;
        if (LIVE) {
            String url = getURL(""+latIn,""+lonIn);
            try {
                fetch = readJsonFromUrl(url);
            } catch (IOException e) {
                result = "IO: "+e.getMessage();
            } catch (JSONException e) {
                result = "JSON: "+e.getMessage();
            }
        }
        else {
            try {
                fetch = new JSONObject(testJSONoffline);
            } catch (JSONException e) {
                result = "Offline failure in JSON object.";
            }
        }


        String[] latitudes = {""};
        String[] longitudes = {""};
        String[] fees = {""};
        String [] wheelchairs = {""};

        try {
            System.out.println("GETTING JSON ARRAY");
            JSONArray markers = fetch.getJSONArray("markers");

            //markers.length() and markers.size() DO NOT WORK!!!!!!!!!!!!!
            int n = 0;
            for (int i=0; i<10000; i++) {
                try {
                    JSONObject toilet = (JSONObject) markers.get(i);
                    String tmp = toilet.getString("latitude");
                    n = i+1;
                }
                catch (Exception e) {
                    break;
                }
            }
            latitudes = new String[n];
            longitudes = new String[n];
            fees = new String[n];
            wheelchairs = new String[n];

            System.out.println("THE MARKER LENGTH OF JASON OBJ IS: "+n);
            for (int i=0; i <n; i++) {
                JSONObject toilet = (JSONObject) markers.get(i);
                latitudes[i] = toilet.getString("latitude");
                System.out.println("LAT: "+latitudes[i]);
                longitudes[i] = toilet.getString("longitude");
                System.out.println("LON: "+longitudes[i]);
                fees[i] = toilet.getString("fee");
                System.out.println("FEE: "+fees[i]);
                wheelchairs[i] = toilet.getString("wheelchair");
                System.out.println("WCH: "+wheelchairs[i]);
            }
            int ix = 0;
            result = latitudes[ix]+" N "+longitudes[ix]+" E "+" fee: "+fees[ix]+" handicap: "+wheelchairs[ix];
        } catch (JSONException e) {
            result = "BR: "+e.getMessage();
        }

        //System.out.println("result: "+result);
        HashMap<String, String[]> out = new HashMap<String, String[]>();
        out.put("lat",latitudes);
        out.put("lon",longitudes);
        out.put("fee",fees);
        out.put("wch",wheelchairs);
        return out;
    }

}

