package com.extralarge.fujitsu.xl.ReporterSection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.extralarge.fujitsu.xl.R;
import com.extralarge.fujitsu.xl.Url;
import com.extralarge.fujitsu.xl.UserSessionManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.iwgang.countdownview.CountdownView;

public class Verifyotp extends AppCompatActivity implements View.OnClickListener {


   static EditText mverifyotptext;
    String verifyotptxt;

    AppCompatButton mverifyotpbtn;

    BroadcastReceiver receiver = null;

    UserSessionManager session;
    CountdownView mflatclocl;
    String usernumbr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyotp);

        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(2147483647);

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arr, Intent brr) {

                processreceiver(arr,brr);

            }
        };

        registerReceiver(receiver,filter);

        session = new UserSessionManager(getApplicationContext());

        mverifyotptext = (EditText) findViewById(R.id.mobileotp);
        mflatclocl = (CountdownView) findViewById(R.id.flatClock);

        mverifyotpbtn = (AppCompatButton) findViewById(R.id.verifyotp_btn);
        mverifyotpbtn.setOnClickListener(this);

        mflatclocl.start(120000);

        mflatclocl.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {

            }
        });

        usernumbr = getIntent().getStringExtra("usernumber");
        Log.d("bbbbbbbbb","123456000"+usernumbr);

    }
    public void recivedSms(String message)
    {
        try
        {
           // String code = parseCode(message);
            mverifyotptext.setText(message.substring(47));
        }
        catch (Exception e)
        {
        }
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
            code = m.group(0);
            while (m.find()) {
        }
        return code;
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    public void processreceiver(Context context , Intent intent) {

        Bundle data = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");
        String sms = "";

        for (int i = 0; i < pdus.length; i++) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String messageBody = smsMessage.getMessageBody();

            String sender = smsMessage.getDisplayOriginatingAddress();
            String senderNum = sender ;
            String asubstring = senderNum.substring(3);
            Log.d("otp00","otp"+senderNum);
            Log.d("otp0","otp123"+asubstring);

            try
            {
                if (asubstring.equals("MIDAPP"))
                {
                    recivedSms(messageBody);
                    mverifyotpbtn.performClick();
                }
            }
            catch(Exception e){}


        }
    }


    @Override
    public void onClick(View v) {

        verifyotp();

    }

    private void verifyotp(){


        final String macid = TokenSave.getInstance(this).getDeviceToken();
        final String KEY_mobile = "otp";
        final String KEY_mac = "token";
        final String KEY_mobilenumber = "mobile";


        verifyotptxt = mverifyotptext.getText().toString().trim();

        if (TextUtils.isEmpty(verifyotptxt)) {
            mverifyotptext.requestFocus();
            mverifyotptext.setError("This Field Is Mandatory");
        }
        else{

            String url = null;
            String REGISTER_URL = Url.verifyotp;

            REGISTER_URL = REGISTER_URL.replaceAll(" ", "%20");
            try {
                URL sourceUrl = new URL(REGISTER_URL);
                url = sourceUrl.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                               Log.d("jabaver", usernumbr);
                            try {
                                JSONObject jsonresponse = new JSONObject(response);
                                boolean success = jsonresponse.getBoolean("success");

                                if (success) {

                                    String name = jsonresponse.getString("name");
                                    String id = jsonresponse.getString("id");
                                    String image = jsonresponse.getString("image");

                                    SaveUserId.getInstance(getApplicationContext()).saveuserId(id);

                                    session.createUserLoginSession(id);
                                    session.SaveImage(image);
                                    session.Savename(name);

                                    Intent registerintent = new Intent(Verifyotp.this, ReporterDashboard.class);
//
                                    registerintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                    startActivity(registerintent);
                                    finish();



                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Verifyotp.this);
                                    builder.setMessage("Registration Failed")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(Verifyotp.this, response.toString(), Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("jabavererr00", usernumbr);
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(Verifyotp.this,"You Have Some Connectivity Issue..", Toast.LENGTH_LONG).show();
                            }

                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();


                    params.put(KEY_mac, macid);
                    params.put(KEY_mobile, verifyotptxt);
                    params.put(KEY_mobilenumber, usernumbr);

                    return params;

                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(Verifyotp.this);
            requestQueue.add(stringRequest);
        }
    }


    public String getMacId()
    {
        TelephonyManager telephonyManager;

        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);

        String deviceId = telephonyManager.getDeviceId();

        return deviceId;
    }

    }



