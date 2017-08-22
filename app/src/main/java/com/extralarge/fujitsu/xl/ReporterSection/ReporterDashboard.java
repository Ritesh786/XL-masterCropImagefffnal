package com.extralarge.fujitsu.xl.ReporterSection;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
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
import com.extralarge.fujitsu.xl.MainActivity;
import com.extralarge.fujitsu.xl.R;
import com.extralarge.fujitsu.xl.Url;
import com.extralarge.fujitsu.xl.UserSessionManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ReporterDashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    TextView mnametext;

    UserSessionManager session;

   // String name;
    DashboardFragment fragment1;
    String id;
    Bundle bundle;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    String name,image1,nreimg;

    CircularImageView userImageVIew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporter_dashboard);

        session = new UserSessionManager(getApplicationContext());

        if (session.checkLogin())
            finish();

        HashMap<String, String> user = session.getname();
        name = user.get(UserSessionManager.KEY_name);

        HashMap<String, String> image = session.getImagedetail();
        image1 = image.get(UserSessionManager.KEY_IMAGE);

        nreimg = "https://s3.ap-south-1.amazonaws.com/excel-storage/images/profiles/"+image1;
        Log.d("jnjnjnjn","00000"+image1+nreimg);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        userImageVIew=(CircularImageView)toolbar.findViewById(R.id.userImageVIew);
        Picasso.with(getApplicationContext()).load(String.valueOf(nreimg)).into(userImageVIew);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        mnametext = (TextView) hView.findViewById(R.id.nametext);
        navigationView.setNavigationItemSelectedListener(this);


         tabdesign tabfrag = new tabdesign();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.frame_trans, tabfrag).addToBackStack("Reporter Dashboard").commit();
        mFragmentManager.addOnBackStackChangedListener(this);


        Toast.makeText(getApplicationContext(),
                "User Login Status: " + session.isUserLoggedIn(),
                Toast.LENGTH_LONG).show();


        mnametext.setText("Welcome Reporter");



    }

//    public int getMyData() {
//        return id;
//    }


    @Override
    public void onBackPressed() {
        Fragment fragment = new Fragment();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (fragment.equals(fragment1)){

            super.onBackPressed();
        }

        else {


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    ReporterDashboard.this);

            // set title
            alertDialogBuilder.setTitle("Exit");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Press Yes For Exit Or Press Samachar For News")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            Intent intent = new Intent(Intent.ACTION_MAIN);
//                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
//                            startActivity(intent);
                            finish();

                            //   System.exit(0);
                        }
                    })
                    .setNegativeButton("Samachar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent newsint = new Intent(ReporterDashboard.this,MainActivity.class);
                            newsint.putExtra("sessionname",name);
                            startActivity(newsint);
                            ReporterDashboard.this.finish();
                            dialog.cancel();
                        }
                    });


            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


        }}

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.reporter_dashboard, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            if (id == R.id.homeexcel) {

                Intent homeintent = new Intent(ReporterDashboard.this,MainActivity.class);
                startActivity(homeintent);

                return true;
            }
//            if (id == R.id.logout) {
//
//                session.logoutUser();
//                ReporterDashboard.this.finish();
//                sendmacid();
//
//                return true;
//            }


            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected (MenuItem item){
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_home) {

                tabdesign tabfrag = new tabdesign();
                mFragmentManager = getSupportFragmentManager();
                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.frame_trans, tabfrag).addToBackStack("Reporter Dashboard").commit();
                mFragmentManager.addOnBackStackChangedListener(this);


            }

               else if (id == R.id.nav_upload) {

                DashboardFragment fragment = new DashboardFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.frame_trans, fragment).addToBackStack("Upload News").commit();
                manager.addOnBackStackChangedListener(this);

            } else if (id == R.id.nav_newsStatus) {

                PendingNews fragment = new PendingNews();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.frame_trans, fragment).addToBackStack("Pending News").commit();
                manager.addOnBackStackChangedListener(this);

            } else if (id == R.id.nav_verifiednews) {

                VerifiedNews fragment = new VerifiedNews();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.frame_trans, fragment).addToBackStack("Verified News").commit();
                manager.addOnBackStackChangedListener(this);

            } else if (id == R.id.nav_notver) {

                RejectedNews fragment = new RejectedNews();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.frame_trans, fragment).addToBackStack("Rejected News").commit();
                manager.addOnBackStackChangedListener(this);

            }
            else if (id == R.id.nav_editprofile) {

                startActivity(new Intent(ReporterDashboard.this,EditProfile.class));
                finish();

            }

            else if (id == R.id.nav_logout) {

                session.logoutUser();
               ReporterDashboard.this.finish();
               sendmacid();

            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }


    @Override
    public void onBackStackChanged() {

        try {

            int lastBackStackEntryCount = getSupportFragmentManager().getBackStackEntryCount() - 1;

            FragmentManager.BackStackEntry lastBackStackEntry =
                    getSupportFragmentManager().getBackStackEntryAt(lastBackStackEntryCount);

            setTitle(lastBackStackEntry.getName());

        } catch (Exception e) {
           e.printStackTrace();
        }

    }

    public void sendmacid(){

        final String macid = SaveUserId.getInstance(ReporterDashboard.this).getUserId();
        Log.d("mc00","macid11"+macid);
        final String KEY_mac = "token";

            String url = null;
            String REGISTER_URL = Url.logout+macid;

            REGISTER_URL = REGISTER_URL.replaceAll(" ", "%20");
            try {
                URL sourceUrl = new URL(REGISTER_URL);
                url = sourceUrl.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("jabaver", macid);
//                            try {
//                                JSONObject jsonresponse = new JSONObject(response);
//                                boolean success = jsonresponse.getBoolean("success");

//                                if (success) {
//
//                                    String name = jsonresponse.getString("name");
//                                    int id = jsonresponse.getInt("id");
//
//                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Verifyotp.this);
//                                    SharedPreferences.Editor editor = prefs.edit();
//                                    editor.putInt("NameOfShared", id);
//                                    editor.commit();
//
//                                    session.createUserLoginSession(name);
//
//                                    Intent registerintent = new Intent(ReporterDashboard.this, ReporterDashboard.class);
////
//                                    registerintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                                    startActivity(registerintent);
//                                    finish();
//
//
//
//                                } else {
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(Verifyotp.this);
//                                    builder.setMessage("Registration Failed")
//                                            .setNegativeButton("Retry", null)
//                                            .create()
//                                            .show();
//
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }

                            Toast.makeText(ReporterDashboard.this, response.toString(), Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Log.d("jabadi", usernsme);
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(ReporterDashboard.this,"You Have Some Connectivity Issue..", Toast.LENGTH_LONG).show();
                            }

                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(ReporterDashboard.this);
            requestQueue.add(stringRequest);
        }


//    public static String getMacId(Context context)
//    {
//        String macId=null;
//
//        macId=getMacAddress(context);
//
//        return macId;
//    }
//
//    private static String getMacAddress(Context context)
//    {
//        WifiManager manager;
//        String macId=null;
//        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        if(manager!=null)
//            macId= manager.getConnectionInfo().getMacAddress();
//        return macId;
//    }

}



