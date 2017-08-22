package com.extralarge.fujitsu.xl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Fujitsu on 10/08/2017.
 */

public class Url {

    public static String serverAddresscore = "http://excelsamachar.com/";
    public static String reporterregister = serverAddresscore + "lumen/public/user/create";
    public static String reporterlogin = serverAddresscore + "lumen/public/user/login";
    public static String verifyotp = serverAddresscore + "lumen/public/user/verify";
    public static String newsupload = serverAddresscore + "lumen/public/post/create";
    public static String sendingtoken = serverAddresscore + "lumen/public/reader/create/";
    public static String logout = serverAddresscore + "lumen/public/user/logout/";
    public static String news = serverAddresscore + "lumen/public/posts/completed/";
    public static String newmain = serverAddresscore + "lumen/public/posts/completed";
    public static String checkmeout = serverAddresscore + "lumen/public/user/checktoken";
    public static String reporternews = serverAddresscore + "lumen/public/post/user/";
    public static String searchnews = serverAddresscore + "lumen/public/post/search/";
    public static String imageurl = "https://s3.ap-south-1.amazonaws.com/excel-storage/images/posts/";


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }

    }

}
