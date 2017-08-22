package com.extralarge.fujitsu.xl.FCM;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Fujitsu on 14/06/2017.
 */

public class MyFirebaseInstanceIDService  extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public static final String TOKEN_BROADCAST = "myfcmtokenbroadcast";
    String refreshedToken;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
       refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        storeToken(refreshedToken);

    }

    private void storeToken(String token) {
        //saving the token on shared preferences
        TokenSave.getInstance(getApplicationContext()).saveDeviceToken(token);
    }




}
