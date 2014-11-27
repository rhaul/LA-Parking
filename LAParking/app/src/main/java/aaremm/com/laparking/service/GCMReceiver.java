package aaremm.com.laparking.service;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

/**
 * Created by rahul on 11-10-2014.
 */
public class GCMReceiver extends GCMBroadcastReceiver {

    @Override
    public String getGCMIntentServiceClassName(Context context){
        return "aaremm.com.laparking.service.GCMService";
    }
}