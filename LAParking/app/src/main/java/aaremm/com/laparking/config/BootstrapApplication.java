package aaremm.com.laparking.config;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import aaremm.com.laparking.object.BoundaryLoc;

public class BootstrapApplication extends Application{
    public static final String LAST_ACTION = "last_action";
    // app
    private static BootstrapApplication instance;
    // Debugging tag for the application
    public static final String APPTAG = "LAparking";
    private boolean parkingStatus = false;
    private Client mKinveyClient;

    /**
     * Create main application
     */
    public BootstrapApplication() {

    }

    /**
     * Create main application
     *
     * @param context
     */
    public BootstrapApplication(final Context context) {
        this();
        attachBaseContext(context);
    }


    public static void setSPString(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getInstance());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static void setSPBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getInstance());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    public static void setSPInteger(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getInstance());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static Integer getSPInteger(String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getInstance());
        return sharedPreferences.getInt(key, 0); // 0 - DOOR 1 - ENGINE
    }

    public static String getSPString(String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getInstance());
        return sharedPreferences.getString(key, "");
    }

    public static Boolean getSPBoolean(String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getInstance());
        return sharedPreferences.getBoolean(key, true);
    }


    public String getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;

                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("HashKey", something);
                return something;
            }
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mKinveyClient = new Client.Builder(this.getApplicationContext()).build();
        pingKinvey();
    }

    public Client getKinveyClient(){
        return this.mKinveyClient;
    }
    private void pingKinvey() {
        mKinveyClient.ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Log.e(APPTAG, "Server not responding", t);
                Toast.makeText(instance,"Server not responding",Toast.LENGTH_LONG).show();
            }
            public void onSuccess(Boolean b) {
                Log.d(APPTAG, "Kinvey Ping Success");
            }
        });
    }

    /**
     * Create main application
     *
     * @param instrumentation
     */
    public BootstrapApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    public static BootstrapApplication getInstance() {

        if (instance == null) {
            instance = new BootstrapApplication();
            return instance;

        } else {
            return instance;
        }
    }

    /**
     * Calculates the end-point from a given source at a given range (meters)
     * and bearing (degrees). This methods uses simple geometry equations to
     * calculate the end-point.
     *
     * @param point   Point of origin
     * @param range   Range in meters
     * @param bearing Bearing in degrees
     * @return End-point from the source given the desired range and bearing.
     */
    public static LatLng calculateDerivedPosition(Location point,
                                                  double range, double bearing) {
        double EarthRadius = 6371000; // m

        double latA = Math.toRadians(point.getLatitude());
        double lonA = Math.toRadians(point.getLongitude());
        double angularDistance = range / EarthRadius;
        double trueCourse = Math.toRadians(bearing);

        double lat = Math.asin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance)
                                * Math.cos(trueCourse)
        );

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance)
                        * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat)
        );

        double lon = ((lonA + dlon + Math.PI) % (Math.PI * 2)) - Math.PI;

        lat = Math.toDegrees(lat);
        lon = Math.toDegrees(lon);

        LatLng newLatLng = new LatLng(lat, lon);

        return newLatLng;

    }
    public List<LatLng> geofencesLL = new ArrayList<LatLng>();

    public void setGeofencesLL(List<LatLng> sortedgeo){
        if(sortedgeo == null){
            geofencesLL.clear();
        }else {/*
          //  List<LatLng> temp = sortedgeo;
            for (int i = 0; i <= sortedgeo.size() - 1; i++) {
                int k = i + 1;
                while (k < sortedgeo.size()) {
                    float results[] = new float[3];
                    Location.distanceBetween(sortedgeo.get(i).latitude, sortedgeo.get(i).longitude, sortedgeo.get(k).latitude, sortedgeo.get(k).longitude, results);
                    if (results[0] < 30) {
                        sortedgeo.add(i, new LatLng(((sortedgeo.get(i).latitude + sortedgeo.get(k).latitude) / (double)2), ((sortedgeo.get(i).longitude + sortedgeo.get(k).longitude) / (double)2)));
                        sortedgeo.remove(k);
                    }
                }
            }
*/
            geofencesLL = sortedgeo;
        }
    }
    public List<LatLng> getGeofencesLL(){
        return geofencesLL;
    }

    public LatLng currentLocation;

    public void setCurrentLocation(LatLng loc){
        currentLocation = loc;
    }
    public LatLng getCurrentLocation(){
        return currentLocation;
    }

    public boolean getParkingStatus() {
        return parkingStatus;
    }
    public void setParkingStatus(boolean status) {
        parkingStatus = status;
    }


    public boolean rayCastIntersect(Location tap, BoundaryLoc vertA, BoundaryLoc vertB) {

        double aY = vertA.getLat();
        double bY = vertB.getLat();
        double aX = vertA.getLng();
        double bX = vertB.getLng();
        double pY = tap.getLatitude();
        double pX = tap.getLongitude();

        if ( (aY>pY && bY>pY) || (aY<pY && bY<pY) || (aX<pX && bX<pX) ) {
            return false; // a and b can't both be above or below pt.y, and a or b must be east of pt.x
        }

        double m = (aY-bY) / (aX-bX);               // Rise over run
        double bee = (-aX) * m + aY;                // y = mx + b
        double x = (pY - bee) / m;                  // algebra is neat!

        return x > pX;
    }

    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyCPqPECtWcEJkpNb3TlK_T5IJcDmOZy-Io";

    public ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:us");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
}


