package com.extralarge.fujitsu.xl.FCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.extralarge.fujitsu.xl.MainActivity;
import com.extralarge.fujitsu.xl.R;
import com.extralarge.fujitsu.xl.ReporterSection.DisapprovedNews;
import com.extralarge.fujitsu.xl.ReporterSection.NewsDetailShow;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * Created by Fujitsu on 15/06/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    NotificationManager notificationManager;
    Intent intent;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> params = remoteMessage.getData();
        Log.d(TAG, "Notification Message Body: " + params);
        JSONObject jsonObject=new JSONObject(params);

        try {
            String keyword = jsonObject.getString("keyword");
            if(keyword.equals("nstatus3")){

                String imageurl = jsonObject.getString("image");
                String NewImgUrl = "https://s3.ap-south-1.amazonaws.com/excel-storage/images/posts/"+imageurl;
                intent = new Intent(this, NewsDetailShow.class);
                intent.putExtra("headline", jsonObject.getString("headline"));
                intent.putExtra("content", jsonObject.getString("content"));
                intent.putExtra("id", jsonObject.getString("id"));
                intent.putExtra("image", NewImgUrl);
                intent.putExtra("type", jsonObject.getString("category"));
                //  intent.putExtra("post","Disapproved Request");


                Log.d("fvsfvsvvssf852","0000000852"+jsonObject.getString("id") + NewImgUrl);
                if(imageurl.equals("null")){

                    soundNotification(jsonObject.getString("headline"));

                }else {
                    soundNotificationImage(jsonObject.getString("headline"), NewImgUrl);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String keyword = jsonObject.getString("keyword");
            if(keyword.equals("nstatus2")){

                String imageurl = jsonObject.getString("image");
                String NewImgUrl = "https://s3.ap-south-1.amazonaws.com/excel-storage/images/posts/"+imageurl;
                intent = new Intent(this, DisapprovedNews.class);
                intent.putExtra("headline", jsonObject.getString("headline"));
                intent.putExtra("content", jsonObject.getString("content"));
                intent.putExtra("id", jsonObject.getString("id"));
                intent.putExtra("image", NewImgUrl);
                intent.putExtra("type", jsonObject.getString("category"));
                //  intent.putExtra("post","Disapproved Request");


                Log.d("fvsfvsvvssf852","0000000852"+jsonObject.getString("id") + NewImgUrl);
                if(imageurl.equals("null")){

                    soundNotification("Your News Post is Disapproved");

                }else {
                    soundNotificationImage("Your News Post is Disapproved", NewImgUrl);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void soundNotification(String msg) {

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icntras)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            notificationBuilder.setSmallIcon(R.drawable.rrrrrrrrr);
//        } else {
//            notificationBuilder.setSmallIcon(R.mipmap.ic_dainik);
//        }

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= 21) notificationBuilder.setVibrate(new long[0]);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void soundNotificationImage(String msg,String url) {

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(getString(R.string.app_name));
        bigPictureStyle.setSummaryText(Html.fromHtml(msg).toString());
        bigPictureStyle.bigPicture(getBitmapFromURL(url));

        NotificationCompat.Builder notificationBuilder = null;
        try {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.icntras)
                    .setLargeIcon(Picasso.with(getApplicationContext()).load(url).get())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(msg)
                    .setStyle(bigPictureStyle)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= 21) notificationBuilder.setVibrate(new long[0]);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
