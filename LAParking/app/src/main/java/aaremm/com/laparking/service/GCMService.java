package aaremm.com.laparking.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.push.KinveyGCMService;

import org.json.JSONException;
import org.json.JSONObject;

import aaremm.com.laparking.R;
import aaremm.com.laparking.activity.ParkingActivity;
import aaremm.com.laparking.config.BootstrapApplication;

/**
 * Created by rahul on 11-10-2014.
 */
public class GCMService extends KinveyGCMService {

    NotificationManager nf = (NotificationManager) BootstrapApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);;
    NotificationCompat.Builder mBuilder;
    public static final int notifyID = 2500;
    PendingIntent pi_parkingLotLoc;
    String plid = "",nameAddress = "",community ="";
    // kinvey
    Client mKinveyClient;
    @Override
    public void onMessage(final String message) {
        Log.i(Client.TAG, "GCM - onMessage: " + message);
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                int scene = 0;
                try {
                    jsonObject.get(message);
                    plid = jsonObject.getString("plid");
                    nameAddress = jsonObject.getString("nameAddress");
                    community = jsonObject.getString("community");
                    scene = jsonObject.getInt("scene");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                displayNotification(scene);
                // Toast.makeText(BootstrapApplication.getInstance(), "Message: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }/*

    private void getParkingLotDetails(String plid) {
        mKinveyClient = BootstrapApplication.getInstance().getKinveyClient();
        Query myQuery = mKinveyClient.query();
        myQuery.equals("plid", plid);
        AsyncAppData<ParkingLot> myEvents = mKinveyClient.appData("LAPL", ParkingLot.class);
        myEvents.get(myQuery, new KinveyListCallback<ParkingLot>() {
            @Override
            public void onSuccess(ParkingLot[] results) {
                Log.v(TAG, "received " + results.length + " events");
                    if (results.length == 1) {
                        parkingLot = results[0];
                        displayNotification();
                    }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "failed to fetch parking lot", error);
            }
        });
    }*/

    @Override
    public void onError(String error) {
        Log.i(Client.TAG, "GCM - onError: " + error);
    }

    @Override
    public void onDelete(int deleteCount) {
        Log.i(Client.TAG, "GCM - onDelete, message deleted count: " + deleteCount);
    }

    @Override
    public void onRegistered(String gcmID) {
        Log.i(Client.TAG, "GCM - onRegister, new gcmID is: " + gcmID);
        Toast.makeText(this,"Registered:",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUnregistered(String oldID) {
        Log.i(Client.TAG, "GCM - onUnregister");
    }


    private void displayNotification(int scene) {
        Intent i_parkingLotLoc = new Intent(this, ParkingActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i_parkingLotLoc.putExtra("freeSpaceNotify",true);
        i_parkingLotLoc.putExtra("plid",plid);
        i_parkingLotLoc.putExtra("nameAddress",nameAddress);
        i_parkingLotLoc.putExtra("community",community);
        pi_parkingLotLoc = PendingIntent.getBroadcast(this, 0, i_parkingLotLoc, 0);
        if(scene == 0) {
            mBuilder = new NotificationCompat.Builder(BootstrapApplication.getInstance())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker("Parking Space Available")
                    .setContentTitle("Parking Space about to be available.")
                    .setContentText("At " + nameAddress)
                    .setContentIntent(pi_parkingLotLoc)
                    .setVibrate(new long[]{0, 1000, 1000, 1000, 1000});
        }else{
            mBuilder = new NotificationCompat.Builder(BootstrapApplication.getInstance())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker("Parking Space Available")
                    .setContentTitle("Parking Space now available.")
                    .setContentText("At " + nameAddress)
                    .setContentIntent(pi_parkingLotLoc)
                    .setVibrate(new long[]{0, 1000, 1000, 1000, 1000});
        }
        nf.notify(notifyID, mBuilder.build());
    }
}