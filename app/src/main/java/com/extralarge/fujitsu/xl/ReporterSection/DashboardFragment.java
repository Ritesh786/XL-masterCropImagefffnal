package com.extralarge.fujitsu.xl.ReporterSection;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.extralarge.fujitsu.xl.FCM.tokensave;
import com.extralarge.fujitsu.xl.MainActivity;
import com.extralarge.fujitsu.xl.R;
import com.extralarge.fujitsu.xl.Spinner.MySpinnerAdapter;
import com.extralarge.fujitsu.xl.Spinner.MySpinnerLight;
import com.extralarge.fujitsu.xl.UserSessionManager;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.bitmap;
import static android.app.Activity.RESULT_OK;
import static com.extralarge.fujitsu.xl.R.id.imageView;
import static com.extralarge.fujitsu.xl.R.id.verifyotp_btn;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

     EditText  mnewsheadline,mnewscontent,mnewsimagecaption;
    MaterialBetterSpinner mnewstype;
     Button mchooseimagebtn,muploadnewsbtn;
    ImageView mnewsimage;
    int id;
    int strtext;
    private int PICK_IMAGE_REQUEST = 1;
    private int CAMERA_REQUEST = 2;
    private static final int PICK_CROPIMAGE = 4;

    private Bitmap bitmap;
    Uri selectedImage;
    String str = "chala";
    Bundle bundle;
    String strtim;
    AlertDialog dialog;
    UserSessionManager session;

    public static final String KEY_ID= "user_id";
    public static final String KEY_HHEADLINE = "headline";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_TYPE = "category";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_CAPTION = "caption";
  //  public static final String UPLOAD_URL = "http://jigsawme.esy.es/picUpload/upload.php";


    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);


        mnewsheadline = (EditText) view.findViewById(R.id.news_heasdline);
        mnewscontent = (EditText) view.findViewById(R.id.news_content);
        mnewsimagecaption = (EditText) view.findViewById(R.id.news_caption);
        mnewstype = (MaterialBetterSpinner) view.findViewById(R.id.news_type);
        mnewsimage = (ImageView) view.findViewById(R.id.news_Image);

        mchooseimagebtn = (Button) view.findViewById(R.id.chooseimage_btn);
        muploadnewsbtn = (Button) view.findViewById(R.id.uploadnews_btn);

        mchooseimagebtn.setOnClickListener(this);
        muploadnewsbtn.setOnClickListener(this);

        session = new UserSessionManager(getContext());

    //    String newstypr[] = {"","National","International","State","Business","Bollywood","Entertainment"};

        List<String> list = new ArrayList();
        list.add("National");
        list.add("International");
        list.add("State");
        list.add("Business");
        list.add("Bollywood");
        list.add("Entertainment");

        mnewstype.setOnItemSelectedListener(this);
        MySpinnerAdapter adapter = new MySpinnerAdapter(getContext(),list);
          mnewstype.setAdapter(adapter);
        mnewstype.setTextColor(getResources().getColor(R.color.black));
        mnewstype.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
//      ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.autocomplete,newstypr);
   //     mnewstype.setAdapter(adapter);

        CheckLogout();

        return view;
    }

    private void showFileChooser() {
try {
    if (android.os.Build.VERSION.SDK_INT >= 23) {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);


    } else {

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, ""), PICK_IMAGE_REQUEST);

    }
}catch (Exception e){

    Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG).show();
}

    }



    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void uploadImage() {



        try {
           bundle = this.getArguments();
            strtext = bundle.getInt("message", 0);
            Log.d("idv012", String.valueOf(strtext));

            final String userid = String.valueOf(strtext);
            final String headline = mnewsheadline.getText().toString().trim();
            final String content = mnewscontent.getText().toString().trim();
            final String type = mnewstype.getText().toString().trim();
            final String caption = mnewsimagecaption.getText().toString().trim();
            final String image = getStringImage(bitmap);


            String url = null;
            String REGISTER_URL = "http://excel.ap-south-1.elasticbeanstalk.com/news/uploadNews.php";

            REGISTER_URL = REGISTER_URL.replaceAll(" ", "%20");
            try {
                URL sourceUrl = new URL(REGISTER_URL);
                url = sourceUrl.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            final ProgressDialog loading = ProgressDialog.show(getContext(), "Uploading...", "Please wait...", false, false);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("jaba", userid);
                        try {
                            JSONObject jsonresponse = new JSONObject(response);
                            boolean success = jsonresponse.getBoolean("success");

                            if (success) {
                                                      mnewsimagecaption.setText("");
                                                      mnewsheadline.setText("");
                                                      mnewscontent.setText("");
                                                      mnewstype.setText("");
                                                      mnewsimage.setImageResource(0);
//                                PendingNews fragment = new PendingNews();
//                                FragmentManager manager = getFragmentManager();
//                                fragment.setArguments(bundle);
//                                manager.beginTransaction().replace(R.id.frame_trans, fragment).addToBackStack("Pending News").commit();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Upoading Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                            Log.d("jabadi", headline);
                            loading.dismiss();
                            Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("bada123", userid);

                            loading.dismiss();
                            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                            Log.d("error1234", error.toString());

                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put(KEY_ID, userid);
                    params.put(KEY_HHEADLINE, headline);
                    params.put(KEY_CONTENT, content);
                    params.put(KEY_TYPE, type);
                    params.put(KEY_IMAGE, image);
                    params.put(KEY_CAPTION, caption);
                    return params;

                }

            };
           // stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            stringRequest.setRetryPolicy(
                    new DefaultRetryPolicy(
                            500000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );


            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);



        }catch (Exception e){

            Log.d("msg1234",e.toString());
        }
    }



    @Override
    public void onClick(View v) {

        if(v == mchooseimagebtn){

            AlertDialog.Builder mbuilder = new AlertDialog.Builder(getContext());
            View mview =getActivity().getLayoutInflater().inflate(R.layout.chooseimage, null);
            Button mtakephoto = (Button) mview.findViewById(R.id.imagebycamera);
            mtakephoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent cameraIntent = new Intent(getContext(),CameraActivity.class);
                    cameraIntent.putExtra(GlobalVariables.FILENAME,GlobalVariables.profilepic_name);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);


                }
            });

            Button mtakegallery = (Button) mview.findViewById(R.id.imagebygallery);
            mtakegallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showFileChooser();

                }
            });
            mbuilder.setView(mview);
             dialog = mbuilder.create();
            dialog.show();
        }

        if(v == muploadnewsbtn){
            if (isvalidinput()) {
                uploadImage();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            Log.d("try4",str);
            super.onActivityResult(requestCode, resultCode, data);}catch (Exception e) {
            Log.d("try8", e.toString());
            Toast.makeText(getContext(), "On super " + e.toString(), Toast.LENGTH_LONG).show();

        }


//
//        if (requestCode >= PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//
//
//            Uri filePath = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
//                mnewsimage.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//        }

        if(requestCode==PICK_IMAGE_REQUEST) {

            if(data==null){

                Toast.makeText(getContext()," Please Select Image For Uploading.... ",Toast.LENGTH_LONG).show();

            }else {
                Uri filePath = data.getData();
                Intent intentcrop = new Intent(getContext(), CropImage.class);
                intentcrop.putExtra("ramji", filePath.toString());
                startActivityForResult(intentcrop, 6);
            }
        }


//        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            bitmap = (Bitmap) data.getExtras().get("data");
//            mnewsimage.setImageBitmap(bitmap);
//        }

        if(requestCode ==CAMERA_REQUEST ){
//            if(data.getExtras()==null){
//
//                Toast.makeText(getContext()," Please Take Image For Uploading.... ",Toast.LENGTH_LONG).show();
//
//            }else {
//                Bitmap bitmapcamear = (Bitmap) data.getExtras().get("data");
//                String bitstring = getStringImage(bitmapcamear);
//                boolean checktr = true;
//                Intent intentcrop = new Intent(getContext(), CropImage.class);
//                intentcrop.putExtra("cameraji", bitstring);
//                intentcrop.putExtra("camerajiboolean", checktr);
//                startActivityForResult(intentcrop, PICK_CROPIMAGE);
//            }
            bitmap =  UtilityClass.getImage(GlobalVariables.profilepic_name);
            mnewsimage.setImageBitmap(bitmap);
            dialog.dismiss();


        }

        if(requestCode==6)
        {
            strtim = data.getStringExtra("cropimage");
            Log.d("imageindash","imageindd "+strtim);
            bitmap = StringToBitMap(strtim);
            Log.d("imageinbitmap","imageinbit "+bitmap);
            mnewsimage.setImageBitmap(bitmap);
            dialog.dismiss();

        }

    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position < 1){
//            TextView errorText = (TextView)mnewstype.getSelectedView();
//            errorText.setError("");
//            errorText.setTextColor(Color.RED);//just to highlight that this is an error
//            errorText.setText("Choose A News Type");//changes the selected item text to this

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean isvalidinput(){

        boolean isValid = true;
        String headline = mnewsheadline.getText().toString().trim();
        String content = mnewscontent.getText().toString().trim();
        String type = mnewstype.getText().toString().trim();
        String caption = mnewsimagecaption.getText().toString().trim();


        if(type.isEmpty()){
            mnewstype.setError("This Field Is Mandatory");
            isValid = false;
        }

        else  if(headline.length() <=0) {
            mnewsheadline.requestFocus();
            mnewsheadline.setError("This Field Is Mandatory");
            isValid = false;
        }

     else if(content.length() <=0){
            mnewscontent.requestFocus();
            mnewscontent.setError("This Field Is Mandatory");
            isValid = false;
        }
        else if(caption.length() <=0){

            mnewsimagecaption.requestFocus();
            mnewsimagecaption.setError("This Field Is Mandatory");
            isValid = false;
        }
        return isValid;

    }

    public void CheckLogout(){

        final String macid = tokensave.getInstance(getContext()).getDeviceToken();
        Log.d("mc0120","macid11"+macid);
        final String KEY_mac = "token";


            String url = null;
            String REGISTER_URL = "http://excel.ap-south-1.elasticbeanstalk.com/checkMeOut.php";

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
                            Log.d("dashb00", macid);
                            try {

                                JSONObject jsonresponse = new JSONObject(response);
                                boolean success = jsonresponse.getBoolean("success");

                                if (success) {

                                    Toast.makeText(getContext(), "Same User", Toast.LENGTH_LONG).show();

                                } else {

                                    session.logoutUser();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                             Log.d("jabadimc", macid);
                            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request

                    params.put(KEY_mac, macid);
                    return params;

                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }



//
//    public  String getMacId()
//    {
//        TelephonyManager telephonyManager;
//
//        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.
//                TELEPHONY_SERVICE);
//
//        String deviceId = telephonyManager.getDeviceId();
//
//        return deviceId;
//    }


}
