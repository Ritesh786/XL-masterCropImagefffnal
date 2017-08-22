package com.extralarge.fujitsu.xl;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.extralarge.fujitsu.xl.FCM.TokenSave;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    SwitchCompat mswitchCompat,mswtchdnd;
    private ProgressDialog pDialog;
    ImageView mbackimg;
    SharedPreferences preferences;
    boolean checkone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mswitchCompat = (SwitchCompat) findViewById(R.id.switchButton);
        mswtchdnd = (SwitchCompat) findViewById(R.id.switchdndbtn);


//        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//        checkone = preferences.getBoolean("tgpref", true);  //default is true
//
//        if (checkone = true) //if (tgpref) may be enough, not sure
//        {
//            mswtchdnd.setChecked(true);
//        }
//        else
//        {
//            mswtchdnd.setChecked(false);
//        }

        mbackimg = (ImageView) findViewById(R.id.back_image);
        mbackimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });


        mswtchdnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((mswtchdnd.isChecked()))
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("tgpref", true); // value to store
                    editor.commit();
                    Checkondnd();
                }
                else
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("tgpref", false); // value to store
                    editor.commit();
                    Checkofdnd();
                }
            }

        });

//        mswtchdnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    if(isChecked){
//                        SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
//                        editor.putBoolean("NameOfThingToSave", true);
//                        editor.commit();
//                        Checkondnd();
//
//                    }else{
//
//
//                        SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
//                        editor.putBoolean("NameOfThingToSave", false);
//                        editor.commit();
//                        Checkofdnd();
//
//                    }
//
//
//
//            }
//        });

        mswitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    Notifyof();

                }else{

                    Notifyon();

                }


            }
        });

    }


    public void Checkondnd(){

        pDialog = new ProgressDialog(SettingActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Please Wait...");
        pDialog.show();

        final String macid = TokenSave.getInstance(SettingActivity.this).getDeviceToken();
        Log.d("mc0120","macid11"+macid);
        final String KEY_mac = "token";


        String url = null;
        String REGISTER_URL = "http://excelsamachar.com/lumen/public/reader/dnd/on/"+macid;

        REGISTER_URL = REGISTER_URL.replaceAll(" ", "%20");
        try {
            URL sourceUrl = new URL(REGISTER_URL);
            url = sourceUrl.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("dashb00", macid);
                     hidePDialog();
                        try {

                            JSONObject jsonresponse = new JSONObject(response);
                            boolean success = jsonresponse.getBoolean("success");

                            if (success) {

                                Toast.makeText(SettingActivity.this, "DND On", Toast.LENGTH_LONG).show();

                            } else {


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                       // Toast.makeText(SettingActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("jabadimc", macid);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(SettingActivity.this,"You Have Some Connectivity Issue..", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
        requestQueue.add(stringRequest);
    }
    public void Checkofdnd(){

        pDialog = new ProgressDialog(SettingActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Please Wait...");
        pDialog.show();

        final String macid = TokenSave.getInstance(SettingActivity.this).getDeviceToken();
        Log.d("mc0120","macid11"+macid);
        final String KEY_mac = "token";


        String url = null;
        String REGISTER_URL = "http://excelsamachar.com/lumen/public/reader/dnd/off/"+macid;

        REGISTER_URL = REGISTER_URL.replaceAll(" ", "%20");
        try {
            URL sourceUrl = new URL(REGISTER_URL);
            url = sourceUrl.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("dashb00", macid);
                     hidePDialog();
                        try {

                            JSONObject jsonresponse = new JSONObject(response);
                            boolean success = jsonresponse.getBoolean("success");

                            if (success) {

                                Toast.makeText(SettingActivity.this, "DND Off", Toast.LENGTH_LONG).show();

                            } else {


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                       // Toast.makeText(SettingActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("jabadimc", macid);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(SettingActivity.this,"You Have Some Connectivity Issue..", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
        requestQueue.add(stringRequest);
    }

    public void Notifyon(){

        pDialog = new ProgressDialog(SettingActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Please Wait...");
        pDialog.show();

        final String macid = TokenSave.getInstance(SettingActivity.this).getDeviceToken();
        Log.d("mc0120","macid11"+macid);
        final String KEY_mac = "token";


        String url = null;
        String REGISTER_URL = "http://excelsamachar.com/lumen/public/reader/noti/on/"+macid;

        REGISTER_URL = REGISTER_URL.replaceAll(" ", "%20");
        try {
            URL sourceUrl = new URL(REGISTER_URL);
            url = sourceUrl.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("dashb00", macid);
                       hidePDialog();
                        try {

                            JSONObject jsonresponse = new JSONObject(response);
                            boolean success = jsonresponse.getBoolean("success");

                            if (success) {

                                Toast.makeText(SettingActivity.this, "Notification Off", Toast.LENGTH_LONG).show();

                            } else {


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                      //  Toast.makeText(SettingActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("jabadimc", macid);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(SettingActivity.this,"You Have Some Connectivity Issue..", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
        requestQueue.add(stringRequest);
    }

    public void Notifyof(){

        pDialog = new ProgressDialog(SettingActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Please Wait...");
        pDialog.show();

        final String macid = TokenSave.getInstance(SettingActivity.this).getDeviceToken();
        Log.d("mc0120","macid11"+macid);
        final String KEY_mac = "token";


        String url = null;
        String REGISTER_URL = "http://excelsamachar.com/lumen/public/reader/noti/off/"+macid;

        REGISTER_URL = REGISTER_URL.replaceAll(" ", "%20");
        try {
            URL sourceUrl = new URL(REGISTER_URL);
            url = sourceUrl.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("dashb00", macid);
                      hidePDialog();
                        try {

                            JSONObject jsonresponse = new JSONObject(response);
                            boolean success = jsonresponse.getBoolean("success");

                            if (success) {

                                Toast.makeText(SettingActivity.this, "Notification On", Toast.LENGTH_LONG).show();

                            } else {


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                      //  Toast.makeText(SettingActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("jabadimc", macid);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(SettingActivity.this,"You Have Some Connectivity Issue..", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(SettingActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}
