package aaremm.com.laparking.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.api.client.json.GenericJson;
import com.google.maps.android.ui.IconGenerator;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.AsyncCustomEndpoints;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyDeleteResponse;
import com.openxc.VehicleManager;
import com.openxc.measurements.IgnitionStatus;
import com.openxc.measurements.Latitude;
import com.openxc.measurements.Longitude;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.VehicleDoorStatus;
import com.openxc.remote.VehicleServiceException;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import aaremm.com.laparking.R;
import aaremm.com.laparking.adapter.PlaceAutoCompleteAdapter;
import aaremm.com.laparking.config.BootstrapApplication;
import aaremm.com.laparking.object.BoundaryLoc;
import aaremm.com.laparking.object.JSONParser;
import aaremm.com.laparking.object.Parked;
import aaremm.com.laparking.object.ParkingLot;
import aaremm.com.laparking.object.Subscribe;
import aaremm.com.laparking.views.TouchHighlightIB;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ParkingActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener,
        AdapterView.OnItemClickListener {
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private boolean isExpandedImageVisible = false;
    Rect startBounds = new Rect();
    float startScaleFinal;

    private static final String TAG = "parkingActivity";
    HashMap<Integer, Integer> usedSpaces = new HashMap<Integer, Integer>();

    public enum LAST_ACTION {
        DOOR, IGNITION
    }

    private LAST_ACTION mLast_action;
    private IgnitionStatus.IgnitionPosition currentIP;
    private VehicleManager mVehicleManager;

    // Views
    @InjectView(R.id.ib_parking_streetView)
    ImageButton b_streetView;
    @InjectView(R.id.b_parking_minmax)
    Button b_minmax;
    @InjectView(R.id.ll_parking_lotDetail)
    LinearLayout ll_lotDetail;
    @InjectView(R.id.ll_parking_lotSubDetail)
    LinearLayout ll_lotSubDetail;
    @InjectView(R.id.b_parking_notify)
    Button b_notify;
    @InjectView(R.id.pb_parking_loadingPL)
    ProgressBar loading;
    @InjectView(R.id.b_parking_directions)
    Button b_directions;
    @InjectView(R.id.iv_parking_gpsLoc)
    ImageView b_gpsLoc;/*
    @InjectView(R.id.autoTV_parking_search)
    AutoCompleteTextView actv_search;*/
    @InjectView(R.id.iv_parking_image)
    TouchHighlightIB iv_image;
    @InjectView(R.id.expanded_image)
    ImageView expandedImageView;

    // Views parking lot details
    @InjectView(R.id.tv_parking_FT)
    TextView tv_freeTotal;
    @InjectView(R.id.tv_parking_timings)
    TextView tv_timings;
    @InjectView(R.id.tv_parking_type)
    TextView tv_type;
    @InjectView(R.id.tv_parking_name)
    TextView tv_name;
    @InjectView(R.id.tv_parking_address)
    TextView tv_address;
    @InjectView(R.id.tv_parking_hr)
    TextView tv_hr;
    @InjectView(R.id.tv_parking_conv)
    TextView tv_conv;
    @InjectView(R.id.tv_parking_features)
    TextView tv_features;/*
    @InjectView(R.id.tv_parking_disabled)
    TextView tv_disabled;*/
    @InjectView(R.id.tv_parking_dmr)
    TextView tv_dmr;
    @InjectView(R.id.tv_parking_mr)
    TextView tv_mr;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    // Location Code

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private LocationClient mLocationClient;
    private LocationManager manager;
    private AlertDialog dialog;
    private Marker now;
    private String currentComm = "";
    private int currentSelectedMarker = -1;


    JSONParser jParser = new JSONParser();
    List<ParkingLot> parkingLots = new ArrayList<ParkingLot>();
    HashMap<String, Integer> parkingLotId = new HashMap<String, Integer>();
    private Geocoder geocoder;
    AutoCompleteTextView actv_search;
    private boolean isLookingAround = false;
    private double lookAroundLat;
    private double lookAroundLng;

    // car is running
    private double carLat;
    private double carLng;

    // FLAGS
    //private boolean isPLDetailsShown = false;
    private boolean isBackPressedTwice = false;
    // kinvey

    private Client mKinveyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);
        ButterKnife.inject(this);
        initializeKinveyUser();
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Geocoder.isPresent()) {
            geocoder = new Geocoder(this, Locale.getDefault());
        }
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            mLocationClient = new LocationClient(this, this, this);

            if (!mLocationClient.isConnected() || !mLocationClient.isConnecting()) {
                mLocationClient.connect();

                // Create the LocationRequest object
                mLocationRequest = LocationRequest.create();
                // Use high accuracy
                mLocationRequest.setPriority(
                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                // Set the update interval to 60 seconds
                mLocationRequest.setInterval(UPDATE_INTERVAL);
                // Set the fastest update interval to 1 second
                mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            }
        } else {
            Log.e("LAParking", "unable to connect to google play services.");
        }
        setUpMapIfNeeded();
        b_minmax.setOnClickListener(this);
        b_gpsLoc.setOnClickListener(this);
        b_directions.setOnClickListener(this);
        iv_image.setOnClickListener(this);
        b_streetView.setOnClickListener(this);
        actv_search = (AutoCompleteTextView) findViewById(R.id.autoTV_parking_search);
        actv_search.setAdapter(new PlaceAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line));
        actv_search.setOnItemClickListener(this);
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        actv_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    lookAround(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String addr = (String) parent.getItemAtPosition(position);
        // Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        lookAround(addr);
    }

    private void lookAround(String addr) {
        isLookingAround = true;
        actv_search.clearFocus();
        actv_search.dismissDropDown();
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(actv_search.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(addr, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size()>0) {
            loading.setVisibility(View.VISIBLE);
            String comm = addresses.get(0).getSubLocality();
            if(comm == null){
                String addrArray[] = addr.split(",");
                comm = addrArray[0];
            }
            lookAroundLat = addresses.get(0).getLatitude();
            lookAroundLng = addresses.get(0).getLongitude();
            setUpMyMarker();
            Query myQuery = mKinveyClient.query();
            myQuery.equals("community", comm);
            AsyncAppData<ParkingLot> myEvents = mKinveyClient.appData("LAPL", ParkingLot.class);
            myEvents.get(myQuery, new KinveyListCallback<ParkingLot>() {
                @Override
                public void onSuccess(ParkingLot[] results) {
                    Log.v(TAG, "received " + results.length + " events");
                    if (results.length > 0) {
                        parkingLots = Arrays.asList(results);
                        getUsedSpaces();
                    } else {
                        Toast.makeText(ParkingActivity.this, "Parking Lots are not available in this area!", Toast.LENGTH_LONG).show();
                        loading.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Throwable error) {
                    Log.e(TAG, "failed to fetchByFilterCriteria", error);
                    loading.setVisibility(View.GONE);
                }
            });
        }
    }

    private void initializeKinveyUser() {
        mKinveyClient = BootstrapApplication.getInstance().getKinveyClient();
        if (!mKinveyClient.user().isUserLoggedIn()) {
            mKinveyClient.user().login(new KinveyUserCallback() {
                @Override
                public void onFailure(Throwable error) {
                    Log.e(TAG, "Login Failure", error);
                }

                @Override
                public void onSuccess(User result) {
                    Log.i(TAG, "Logged in a new implicit user with id: " + result.getId());
                    registerPush();
                }
            });
        } else {
            registerPush();
        }
    }

    public void registerPush() {
        if (!mKinveyClient.push().isPushEnabled()) {
            mKinveyClient.push().initialize(getApplication());
            //Not going to hook up intents for this sample, so just wait for five seconds before redrawing
            new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long miliseconds) {
                }

                @Override
                public void onFinish() {
                    //after 5 seconds update the status
                    if (!mKinveyClient.push().isPushEnabled()) {
                     //   Toast.makeText(ParkingActivity.this, "Push Reg. Error.", Toast.LENGTH_LONG).show();
                    } else {
                      //  Toast.makeText(ParkingActivity.this, "Push Reg. Successful", Toast.LENGTH_LONG).show();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrentLocation == null) {
            mCurrentLocation = location;
        }
        long timeElapsed = System.currentTimeMillis() - mCurrentLocation.getTime();
        float distance = mCurrentLocation.distanceTo(location);
        if (!isLookingAround) {
            if (ignitionStatus == null) {
                if (distance < 10 && timeElapsed <= 180 * 1000 && parkingLots.size() > 0) {
                    return;
                }
            } else {
                if (ignitionStatus.getValue().enumValue() == IgnitionStatus.IgnitionPosition.RUN) {
                    return;
                }
            }
        } else {
            return;
        }
        mCurrentLocation = location;
        if (mMap != null && mCurrentLocation != null) {
            setUpMyMarker();
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                String comm = addresses.get(0).getSubLocality();
                if (!currentComm.equalsIgnoreCase(comm) && !comm.isEmpty()) {
                    loading.setVisibility(View.VISIBLE);
                    currentComm = comm;
                    actv_search.setText(currentComm);
                    Toast.makeText(this, "community :" + comm, Toast.LENGTH_LONG).show();
                    /*plc = new GetParkingLotsCords(this, currentComm);
                    plc.execute();*/
                    Query myQuery = mKinveyClient.query();
                    myQuery.equals("community", currentComm);
                    AsyncAppData<ParkingLot> myEvents = mKinveyClient.appData("LAPL", ParkingLot.class);
                    myEvents.get(myQuery, new KinveyListCallback<ParkingLot>() {
                        @Override
                        public void onSuccess(ParkingLot[] results) {
                            Log.v(TAG, "received " + results.length + " events");
                            if (!isLookingAround) {
                                if (results.length > 0) {
                                    parkingLots = Arrays.asList(results);
                                    getUsedSpaces();
                                } else {
                                    Toast.makeText(ParkingActivity.this, "Parking Lots are not available in this area!", Toast.LENGTH_LONG).show();
                                    loading.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Log.e(TAG, "failed to fetchByFilterCriteria", error);
                            loading.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }

    }

    private void getUsedSpaces() {
        Query myQuery = mKinveyClient.query();
        myQuery.equals("community", currentComm);
        AsyncAppData<Parked> myEvents = mKinveyClient.appData("parked", Parked.class);
        myEvents.get(myQuery, new KinveyListCallback<Parked>() {
            @Override
            public void onSuccess(Parked[] results) {
                if (results.length > 0) {
                    //Log.i("TAG", "got: " + rresults[0].get("plid") + " " + res.results[0].get("_result"));
                    assignFreeSpaces(results);
                }
                showParkingLots();
                setUpMyMarker();
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable error) {
                loading.setVisibility(View.GONE);
            }
        });
    }

    private void assignFreeSpaces(Parked[] results) {
        usedSpaces.clear();
        for (int i = 0; i < results.length; i++) {
            if (usedSpaces.containsKey(Integer.valueOf(results[i].getPlid()))) {
                usedSpaces.put(Integer.valueOf(results[i].getPlid()), usedSpaces.get(Integer.valueOf(results[i].getPlid())) + 1);
            } else {
                usedSpaces.put(Integer.valueOf(results[i].getPlid()), 1);
            }
        }
        for (int j = 0; j < parkingLots.size(); j++) {
            if (usedSpaces.containsKey(Integer.valueOf(parkingLots.get(j).getPlid()))) {
                int noOfUsedSpaces = usedSpaces.get(Integer.valueOf(parkingLots.get(j).getPlid()));
                int noOfTotalSpaces = Integer.valueOf(parkingLots.get(j).getSpaces().toString());
                parkingLots.get(j).setFreeSpaces(noOfTotalSpaces - noOfUsedSpaces);
                Log.d("parking lot Free" + parkingLots.get(j).getPlid(), parkingLots.get(j).getFreeSpaces() + "");
                break;
            }
        }
    }

    private void getCurrentGPSLocation() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showDialogEnableGPS();
        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.cancel();
            }
            mCurrentLocation = mLocationClient.getLastLocation();
            if (mCurrentLocation != null && (System.currentTimeMillis() - mCurrentLocation.getTime()) < 60 * 1000) {
                setUpMyMarker();
            } else {
                mCurrentLocation = null;
                mLocationClient.requestLocationUpdates(mLocationRequest, this);
                Toast.makeText(this, "Fetching your current location..", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }


    @Override
    protected void onStop() {
        // If the client is connected
        if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
            mLocationClient.removeLocationUpdates(this);
        }
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onBackPressed() {
        if (isExpandedImageVisible) {
            contractImage(startBounds, startScaleFinal);
        }else if(ll_lotSubDetail.getVisibility() == View.VISIBLE){
            ll_lotSubDetail.setVisibility(View.GONE);
        }else {
            if(isBackPressedTwice) {
                super.onBackPressed();
            }else{
                isBackPressedTwice = true;
                Toast.makeText(this,"Press again to exit.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // When the activity goes into the background or exits, we want to make
        // sure to unbind from the service to avoid leaking memory
        if (mVehicleManager != null) {
            Log.i(TAG, "Unbinding from Vehicle Manager");
            try {
                // Remember to remove your listeners, in typical Android
                // fashion.
                mVehicleManager.removeListener(IgnitionStatus.class, mIgnitionListener);
                mVehicleManager.removeListener(VehicleDoorStatus.class, mDoorStatusListener);
            } catch (VehicleServiceException e) {
                e.printStackTrace();
            }
            unbindService(mConnection);
            mVehicleManager = null;
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.setOnMarkerClickListener(this);
            // Check if we were successful in obtaining the map.
            if (mMap == null) {
                Toast.makeText(this, "Google Maps couldn't be loaded! Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpMyMarker() {
        if (now != null) {
            now.remove();
        }
        LatLng loc;
        if(mCurrentLocation != null) {
            loc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }else{
            loc = new LatLng(lookAroundLat, lookAroundLng);
        }
        if (ignitionStatus == null) {
            if (isLookingAround) {
                loc = new LatLng(lookAroundLat, lookAroundLng);
            } else {
                loc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                now = mMap.addMarker(new MarkerOptions().position(loc).title("Me"));
            }
        }else if (ignitionStatus.getValue().enumValue() == IgnitionStatus.IgnitionPosition.RUN){
            loc = new LatLng(vehicleLocLat.getValue().doubleValue(),vehicleLocLong.getValue().doubleValue());
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 16);
        mMap.animateCamera(cameraUpdate);
    }

    private void showParkingLots() {
        mMap.clear();
        parkingLotId.clear();
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_BLUE);
        // Log.d("NoOfPL:",parkingLots.size()+"");
        for (int i = 0; i < parkingLots.size(); i++) {/*
            List<Address> add = new ArrayList<Address>();
            try {
                add = geocoder.getFromLocationName(parkingLots.get(i).StreetAddress + " " + parkingLots.get(i).ZipCode, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            addIcon(iconFactory, i, parkingLots.get(i).getSpaces(), parkingLots.get(i).getName(), new LatLng(parkingLots.get(i).getLatitude(), parkingLots.get(i).getLongitude()));
            if (parkingLots.get(i).getBoundary().size() > 0) {
                addPLBoundary(i);
            }
        }
    }

    private void addPLBoundary(int i) {
        ParkingLot lot = parkingLots.get(i);
        List<BoundaryLoc> boundary = lot.getBoundary();
        mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(boundary.get(0).getLat(), boundary.get(0).getLng()), new LatLng(boundary.get(1).getLat(), boundary.get(1).getLng()), new LatLng(boundary.get(2).getLat(), boundary.get(2).getLng()), new LatLng(boundary.get(3).getLat(), boundary.get(3).getLng()))
                .strokeColor(R.color.peter)
                .fillColor(R.color.boundary));
    }

    private void addIcon(IconGenerator iconFactory, int position, String text, String name, LatLng loc) {
        MarkerOptions markerOptions = new MarkerOptions().
                title(text).
                snippet(name).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)).
                position(loc);

        Marker marker = mMap.addMarker(markerOptions);
        parkingLotId.put(marker.getId(), position);
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        mCurrentLocation = mLocationClient.getLastLocation();
        if (mCurrentLocation != null && (mCurrentLocation.getTime() - System.currentTimeMillis()) < 60 * 1000) {
            setUpMyMarker();
        } else {
            mCurrentLocation = null;
        }
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    private void showDialogEnableGPS() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        final String message = "Enable GPS to locate nearby Parking Spots.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                Intent gpsOptionsIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(gpsOptionsIntent);
                                d.dismiss();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        }
                );

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (parkingLotId.containsKey(marker.getId())) {
            currentSelectedMarker = parkingLotId.get(marker.getId());
            showParkingLotDetails(currentSelectedMarker);
            return false;
        }
        return false;
    }

    private void showParkingLotDetails(Integer index) {
        ParkingLot lot = parkingLots.get(index);
        Picasso.with(this).load("http://parkinginla.lacity.org/images/lots/" + lot.getPlid() + ".jpg").into(iv_image);
        Log.d(TAG, "http://parkinginla.lacity.org/images/lots/" + lot.getPlid() + ".jpg");
        tv_name.setText(lot.getName());
        tv_address.setText(lot.getStreetAddress() + "," + lot.getCommunity());
        if (lot.getFreeSpaces() == -1) {
            tv_freeTotal.setText(lot.getSpaces() + "/" + lot.getSpaces());
        } else {
            tv_freeTotal.setText(lot.getFreeSpaces() + "/" + lot.getSpaces());
        }
        //tv_disabled.setText("" + lot.getDisabled());
        tv_timings.setText(lot.getTimings());
        tv_type.setText(lot.getOperator());
        tv_hr.setText(lot.getHourlyRate());
        tv_dmr.setText(lot.getDailyRate());
        tv_mr.setText(lot.getMonthlyRate());
        tv_conv.setText(lot.getConvenientTo());
        tv_features.setText(lot.getFeatures());
        if ((lot.getFreeSpaces() == 0 || lot.getFreeSpaces() <Integer.valueOf(lot.getSpaces())) && lot.getFreeSpaces() != -1) {
            b_notify.setVisibility(View.VISIBLE);
        } else {
            b_notify.setVisibility(View.GONE);
        }
        if (ll_lotDetail.getVisibility() == View.GONE) {
            ll_lotDetail.setVisibility(View.VISIBLE);
        }
        ll_lotSubDetail.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_parking_minmax: {
                if (ll_lotSubDetail.getVisibility() == View.VISIBLE) {
                    ll_lotSubDetail.setVisibility(View.GONE);
                } else {
                    ll_lotSubDetail.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.ib_parking_streetView: {
                if(currentSelectedMarker != -1) {
                    Intent intent = new Intent(ParkingActivity.this, StreetViewActivity.class);
                    intent.putExtra("plid", parkingLots.get(currentSelectedMarker).getPlid());
                    intent.putExtra("name", parkingLots.get(currentSelectedMarker).getName());
                    intent.putExtra("streetName", parkingLots.get(currentSelectedMarker).getStreetAddress());
                    intent.putExtra("lat", parkingLots.get(currentSelectedMarker).getLatitude());
                    intent.putExtra("lng", parkingLots.get(currentSelectedMarker).getLongitude());
                    startActivity(intent);
                }
                break;
            }
            case R.id.iv_parking_gpsLoc: {
                isLookingAround = false;
                getCurrentGPSLocation();
                break;
            }
            case R.id.b_parking_notify: {
                subscribeForSpaceAvail(currentSelectedMarker);
                break;
            }
            case R.id.b_parking_directions: {
                showDirections();
                break;
            }
            case R.id.iv_parking_image: {
                isExpandedImageVisible = true;
                zoomImageFromThumb();
                break;
            }
        }
    }

    private void subscribeForSpaceAvail(final int SelectedMarker) {
        Subscribe event = new Subscribe();
        event.setPlid(parkingLots.get(SelectedMarker).getPlid());
        event.setUser(mKinveyClient.user());
        AsyncAppData<Subscribe> myevents = mKinveyClient.appData("subscribe", Subscribe.class);
        myevents.save(event, new KinveyClientCallback<Subscribe>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "failed to subscribe", e);
            }

            @Override
            public void onSuccess(Subscribe s) {
                Log.d(TAG, "Successfully subscribed to " + parkingLots.get(SelectedMarker).getName());
            }
        });

    }

    private void showDirections() {
        String dLat = parkingLots.get(currentSelectedMarker).getLatitude() + "";
        String dLng = parkingLots.get(currentSelectedMarker).getLongitude() + "";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?&daddr=" + dLat + "," + dLng));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                    /*
                     * Try the request again
                     */
                        break;
                }
        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(),
                        "Location Updates");
            }
        }
        return false;
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
            Toast.makeText(ParkingActivity.this, connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
        }
    }
    private IgnitionStatus ignitionStatus;
    IgnitionStatus.Listener mIgnitionListener = new IgnitionStatus.Listener() {
        @Override
        public void receive(Measurement measurement) {
            ignitionStatus = (IgnitionStatus) measurement;
            runOnUiThread(new Runnable() {
                public void run() {

                    checkVehicleIgnitionStatus(ignitionStatus.getValue().enumValue());

                }
            });

        }
    };

    private VehicleDoorStatus vehicleDoorStatus;
    VehicleDoorStatus.Listener mDoorStatusListener = new Measurement.Listener() {
        @Override
        public void receive(Measurement measurement) {
            vehicleDoorStatus = (VehicleDoorStatus) measurement;
            runOnUiThread(new Runnable() {
                public void run() {
                    if (!vehicleDoorStatus.getEvent().booleanValue()) {
                        checkVehicleDoorStatus(vehicleDoorStatus.getValue().enumValue());
                    }

                }
            });

        }
    };
    private Latitude vehicleLocLat;
    Latitude.Listener mVehicleLocLatListener = new Measurement.Listener() {
        @Override
        public void receive(Measurement measurement) {
            vehicleLocLat = (Latitude) measurement;
            runOnUiThread(new Runnable() {
                public void run() {
                    gotVehicleLat = true;
                }
            });

        }
    };

    private Longitude vehicleLocLong;
    private boolean gotVehicleLat = false;
    Longitude.Listener mVehicleLocLongListener = new Measurement.Listener() {
        @Override
        public void receive(Measurement measurement) {
            vehicleLocLong = (Longitude) measurement;
            runOnUiThread(new Runnable() {
                public void run() {
                    if(gotVehicleLat) {
                        setUpMyMarker();
                        gotVehicleLat = false;
                    }
                }
            });

        }
    };

    private void checkVehicleDoorStatus(VehicleDoorStatus.DoorId doorId) {
        mLast_action = LAST_ACTION.values()[BootstrapApplication.getInstance().getSPInteger(BootstrapApplication.getInstance().LAST_ACTION)];
        switch (doorId) {
            case DRIVER:
                switch (mLast_action) {
                    case DOOR:// GOING IN
                        String toast = "Door Open (Going in)";
                        if (BootstrapApplication.getInstance().getParkingStatus()) {
                            aboutToLeaveNotify();
                            toast+=" - About to leave!";
                        }
                        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
                        break;
                    case IGNITION:// GOING OUT
                        if (!BootstrapApplication.getInstance().getParkingStatus()) {
                            parkIfInAnyParkingLot();
                        }
                        Toast.makeText(this, "Door Open (Going out)", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
            case PASSENGER:
                break;
            case REAR_LEFT:
                break;
            case REAR_RIGHT:
                break;
            case BOOT:
                break;
        }
        mLast_action = LAST_ACTION.DOOR;
        BootstrapApplication.getInstance().setSPInteger(BootstrapApplication.getInstance().LAST_ACTION, 0);
    }

    private void parkIfInAnyParkingLot() {
        if (parkingLots.size() > 0) {
            float[] results = new float[3];
            Location.distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), parkingLots.get(0).getLatitude(), parkingLots.get(0).getLongitude(), results);
            int nearestPL = 0;
            float nearestDistance = results[0];
            for (int i = 1; i < parkingLots.size(); i++) {
                Location.distanceBetween(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), parkingLots.get(i).getLatitude(), parkingLots.get(i).getLongitude(), results);
                if (results[0] < nearestDistance) {
                    nearestPL = i;
                    nearestDistance = results[0];
                }
            }
            ParkingLot lot = parkingLots.get(nearestPL);
            int intersectCount = 0;
            for (int j = 0; j < lot.getBoundary().size() - 1; j++) {
                if (BootstrapApplication.getInstance().rayCastIntersect(mCurrentLocation, lot.getBoundary().get(j), lot.getBoundary().get(j + 1))) {
                    intersectCount++;
                }
            }

            if ((intersectCount % 2) == 1) { // odd = inside, even = outside;
                parkInThisLot(nearestPL);
            }
        }
    }

    private void parkInThisLot(final int i) {
        Parked event = new Parked();
        event.setPlid(parkingLots.get(i).getPlid());
        event.setUid(mKinveyClient.user().getId());
        event.setCommunity(currentComm);
        AsyncAppData<Parked> myevents = mKinveyClient.appData("parked", Parked.class);
        myevents.save(event, new KinveyClientCallback<Parked>() {
            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "failed to save event data", e);
            }

            @Override
            public void onSuccess(Parked r) {
                Log.d(TAG, "Successfully Parked at " + parkingLots.get(i).getName());
                Toast.makeText(ParkingActivity.this,"Successfully Parked at " + parkingLots.get(i).getName(), Toast.LENGTH_LONG).show();
                BootstrapApplication.getInstance().setParkingStatus(true);
            }
        });

    }

    private void aboutToLeaveNotify() {
        GenericJson notify = new GenericJson();
        notify.put("uid", mKinveyClient.user().getId());
        //notify.put("status", "atl");
        AsyncCustomEndpoints endpoints = mKinveyClient.customEndpoints(GenericJson.class);
        endpoints.callEndpoint("notifySpaceATBAvailable", notify, new KinveyClientCallback<GenericJson>() {
            @Override
            public void onSuccess(GenericJson result) {
                Log.d(TAG, "User about to leave");
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, error.toString());
            }
        });
    }

    private void checkVehicleIgnitionStatus(IgnitionStatus.IgnitionPosition ignitionPosition) {
        if (currentIP != ignitionPosition) {
            currentIP = ignitionPosition;
            switch (ignitionPosition) {
                case OFF:
                    parkIfInAnyParkingLot();
                    Toast.makeText(this, "Ignition Off", Toast.LENGTH_LONG).show();
                    break;
                case ACCESSORY:
                    break;
                case RUN:
                    String toast = "Ignition Running";
                    if (BootstrapApplication.getInstance().getParkingStatus()) { //Got IN THE CAR
                        leftNotify();
                        toast+=" - Just left!";
                    }
                    Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
                    break;
                case START:
                    break;
            }
            mLast_action = LAST_ACTION.IGNITION;
            BootstrapApplication.getInstance().setSPInteger(BootstrapApplication.getInstance().LAST_ACTION, 1);
        }
    }

    private void leftNotify() {
        AsyncAppData<Parked> myevents = mKinveyClient.appData("parked", Parked.class);
        Query myQuery = mKinveyClient.query();
        myQuery.equals("uid", mKinveyClient.user().getId());
        myevents.delete(myQuery, new KinveyDeleteCallback() {
            @Override
            public void onSuccess(KinveyDeleteResponse kinveyDeleteResponse) {
                //Log.d(TAG, "Moved from "+ parkingLots.get(i).getName());
                BootstrapApplication.getInstance().setParkingStatus(false);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "failed to save event data", throwable);
            }
        });
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder) service)
                    .getService();
            try {
                mVehicleManager.addListener(IgnitionStatus.class, mIgnitionListener);
                mVehicleManager.addListener(VehicleDoorStatus.class, mDoorStatusListener);
                mVehicleManager.addListener(Latitude.class, mVehicleLocLatListener);
                mVehicleManager.addListener(Longitude.class, mVehicleLocLongListener);
            } catch (VehicleServiceException e) {
                e.printStackTrace();
            } catch (UnrecognizedMeasurementTypeException e) {
                e.printStackTrace();
            }
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleManager Service  disconnected unexpectedly");
            mVehicleManager = null;
        }
    };

    private void zoomImageFromThumb() {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        expandedImageView.setImageDrawable(iv_image.getDrawable());

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        iv_image.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        iv_image.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contractImage(startBounds, startScaleFinal);
            }
        });
    }

    private void contractImage(Rect startBounds, float startScaleFinal) {
        isExpandedImageVisible = false;
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(expandedImageView, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.Y, startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.SCALE_X, startScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.SCALE_Y, startScaleFinal));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                iv_image.setAlpha(1f);
                expandedImageView.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                iv_image.setAlpha(1f);
                expandedImageView.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }
}
