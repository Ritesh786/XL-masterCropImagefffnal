package com.extralarge.fujitsu.xl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.extralarge.fujitsu.xl.ReporterSection.BecomeReporter;
import com.extralarge.fujitsu.xl.ReporterSection.ReporterDashboard;
import com.extralarge.fujitsu.xl.ReporterSection.ReporterLogin;

public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    android.support.v7.widget.Toolbar toolbar;
    private BroadcastReceiver broadcastReceiver;
    String token;
    AlertDialog alertDialog;


    String name;

    UserSessionManager session;
    int i = 0;
    static boolean f = true;
  //  SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        session = new UserSessionManager(getApplicationContext());
        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationview);


        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */

        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));

        ImageView msearch = (ImageView) toolbar.findViewById(R.id.searchimage);
        msearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent searchint = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(searchint);
            }
        });


        Intent intent = getIntent();
         name = intent.getStringExtra("session");

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();


                if (menuItem.getItemId() == R.id.nav_item_home) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                    toolbar.setTitle("EXCEL");
                    toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
                }
//
//                if (menuItem.getItemId() == R.id.nav_item_national) {
//                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
//                    xfragmentTransaction.replace(R.id.containerView, new MainNews()).commit();
//                    toolbar.setTitle("राष्ट्रीय");
//                    toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
//
//                }
//
//                if (menuItem.getItemId() == R.id.nav_item_International) {
//                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
//                    xfragmentTransaction.replace(R.id.containerView, new State()).commit();
//                    toolbar.setTitle("International");
//                    toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
//
//                }
//
//                if (menuItem.getItemId() == R.id.nav_item_states) {
//                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
//                    xfragmentTransaction.replace(R.id.containerView, new State()).commit();
//                    toolbar.setTitle("States");
//                    toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
//
//                }
//                if (menuItem.getItemId() == R.id.nav_item_business) {
//                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
//                    xfragmentTransaction.replace(R.id.containerView, new State()).commit();
//                    toolbar.setTitle("Business");
//                    toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
//
//                }
//
//                if (menuItem.getItemId() == R.id.nav_item_city) {
//                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
//                    xfragmentTransaction.replace(R.id.containerView, new State()).commit();
//                    toolbar.setTitle("Cities");
//                    toolbar.setTitleTextColor(ContextCompat.getColor(MainActivity.this, R.color.black));
//
//                }


                if (menuItem.getItemId() == R.id.nav_item_becomereporter) {

                    Intent becomereporterint = new Intent(MainActivity.this, BecomeReporter.class);
                    startActivity(becomereporterint);
                }

                if (menuItem.getItemId() == R.id.nav_item_reporterlogin) {

                   // session.createUserLoginSession(name);

                    Intent reporterloginint = new Intent(MainActivity.this, ReporterLogin.class);
                    startActivity(reporterloginint);


                }

                if (menuItem.getItemId() == R.id.nav_item_uploadnews) {

                    Intent dashboardintent = new Intent(MainActivity.this, ReporterDashboard.class);
                    startActivity(dashboardintent);
                    finish();
                }

                if (menuItem.getItemId() == R.id.nav_item_setting) {

                    Intent dashboardintent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(dashboardintent);
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */


        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        if (!isNetworkAvailable(MainActivity.this)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setMessage("No Internet Connection ! Enable your Connection First !!! ");

            alertDialogBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });
            alertDialog = alertDialogBuilder.create();
            alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                               @Override
                                               public void onShow(DialogInterface arg0) {
                                                   alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor( getResources().getColor( R.color.colorPrimary ));
                                                   alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor( getResources().getColor( R.color.colorPrimary ));
                                               }
                                           }
            );
            alertDialog.show();


        }
}

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ( ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }else{
            return false;
        }
    }




    }
