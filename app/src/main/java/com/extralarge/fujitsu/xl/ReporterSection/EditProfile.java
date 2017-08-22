package com.extralarge.fujitsu.xl.ReporterSection;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.extralarge.fujitsu.xl.MainActivity;
import com.extralarge.fujitsu.xl.R;
import com.extralarge.fujitsu.xl.UserSessionManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfile extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    EditText mpassword, mname, memail, mmobile, mpincode;
    Button mbtnregister;

    String   password="123456789", name, email, mobile, city, district, state, landmark, pincode,gender;
    String usertype= "individual";
    String  usernsme;
    ImageView mbackomage;

    AutoCompleteTextView mcity, mdistrict, mstate,mgender;

    AlertDialog.Builder builder;
    ArrayAdapter<CharSequence> adapter;
    CircularImageView userImageVIew;

    private ProgressDialog pDialog;
    private Bitmap bitmap;
    AlertDialog dialog;
    String strtim;

    private int PICK_IMAGE_REQUEST = 1;
    private int CAMERA_REQUEST = 2;
    UserSessionManager session;
   Uri imageUri;

    private static final int PICK_CROPIMAGE = 4;

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD7_gkB6R8Tn2SVAgis-rrYJnB2KZtWbbQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //  mmobile = (EditText) findViewById(R.id.reg_mobile);

        mname = (EditText) findViewById(R.id.reg_name);
        // memail = (EditText) findViewById(R.id.reg_email);

        session = new UserSessionManager(getApplicationContext());

        mcity = (AutoCompleteTextView) findViewById(R.id.reg_city);
        mdistrict = (AutoCompleteTextView) findViewById(R.id.reg_area);
        mstate = (AutoCompleteTextView) findViewById(R.id.reg_state);
        mpincode = (EditText) findViewById(R.id.reg_pincode);
        userImageVIew=(CircularImageView)findViewById(R.id.userImageVIew);
        //  mgender =(AutoCompleteTextView) findViewById(R.id.reg_gender);

        mbackomage = (ImageView) findViewById(R.id.back_image);
        mbackomage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(EditProfile.this,ReporterDashboard.class));
                finish();
            }
        });

        builder = new AlertDialog.Builder(getApplicationContext());

        String[] genders = getResources().getStringArray(R.array.gender);

        ArrayAdapter<String> gendadapter =
                new ArrayAdapter<String>(getApplicationContext(), R.layout.autocomplete, genders);

        // mgender.setAdapter(gendadapter);
        populatedata();

        mcity.setAdapter(new EditProfile.GooglePlacesAutocompleteAdapter(EditProfile.this, R.layout.autocomplete));
        mstate.setAdapter(new EditProfile.GooglePlacesAutocompleteAdapter(EditProfile.this, R.layout.autocomplete));
        mdistrict.setAdapter(new EditProfile.GooglePlacesAutocompleteAdapter(EditProfile.this, R.layout.autocomplete));
        mcity.setOnItemClickListener(this);
        mstate.setOnItemClickListener(this);
        mdistrict.setOnItemClickListener(this);

        mbtnregister = (Button) findViewById(R.id.register_btn);
        mbtnregister.setOnClickListener(this);

        userImageVIew.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(EditProfile.this,ReporterDashboard.class));
        finish();

    }

    public boolean isValidPhoneNumber(String phoneNumber) {

        String expression ="^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}$";
        CharSequence inputString = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches())
        {
            return true;
        }
        else{
            return false;
        }

    }


    public void populatedata(){

        pDialog = new ProgressDialog(EditProfile.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        String userid = SaveUserId.getInstance(EditProfile.this).getUserId();

        final String url = "http://excelsamachar.com/lumen/public/user/profile/"+userid;

        StringRequest movieReq = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("rr000", response.toString());
                        hidePDialog();

                        // Parsing json
                        try {

                            JSONObject obj = new JSONObject(response);
                            String imagrurl = null;
                            String imageurlfull;

                            Picasso.with(getApplicationContext()).load(String.valueOf("https://s3.ap-south-1.amazonaws.com/excel-storage/images/profiles/"+obj.getString("image"))).into(userImageVIew);
                            mname.setText(obj.getString("name"));
                            mstate.setText(obj.getString("state"));
                            mdistrict.setText(obj.getString("district"));
                            mcity.setText(obj.getString("city"));
                            mpincode.setText(obj.getString("pincode"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    // notifying list adapter about data changes
                    // so that it renders the list view with updated data

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(EditProfile.this,"You Have Some Connectivity Issue..", Toast.LENGTH_LONG).show();
                }
                hidePDialog();

            }
        });{

            RequestQueue requestQueue = Volley.newRequestQueue(EditProfile.this);
            requestQueue.add(movieReq);
        }
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


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.userImageVIew:

                AlertDialog.Builder mbuilder = new AlertDialog.Builder(EditProfile.this);
                View mview = getLayoutInflater().inflate(R.layout.chooseimage, null);
                Button mtakephoto = (Button) mview.findViewById(R.id.imagebycamera);
                mtakephoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        imageUri = getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, CAMERA_REQUEST);

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

                break;
            case R.id.register_btn:

                registerUser();

                break;

        }
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

            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }

    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(bmp == null){

        }else {
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        }
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }



    private void registerUser() {


        // final String KEY_mobile = "mobile";
        final String KEY_name = "name";
        final String KEY_email = "email";
        final String KEY_gender = "gender";
        final String KEY_state = "state";
        final String KEY_city = "city";
        final String KEY_district = "district";
        final String KEY_pincode = "pincode";
        final String KEY_image = "image";



        final String image = getStringImage(bitmap);
        //   mobile = mmobile.getText().toString().trim();
        name = mname.getText().toString().trim();
//        email = memail.getText().toString().trim();
        // gender = mgender.getText().toString().trim();
        state = mstate.getText().toString().trim();
        city = mcity.getText().toString().trim();
        district = mdistrict.getText().toString().trim();
        pincode = mpincode.getText().toString().trim();


        if(TextUtils.isEmpty(name)){
            mname.requestFocus();
            mname.setError("This Field Is Mandatory");
        } else if (TextUtils.isEmpty(password)) {
            mpassword.requestFocus();
            mpassword.setError("This Field Is Mandatory");
        } else if (TextUtils.isEmpty(name)) {
            mname.requestFocus();
            mname.setError("This Field Is Mandatory");
        } else if (TextUtils.isEmpty(city)) {
            mcity.requestFocus();
            mcity.setError("This Field Is Mandatory");
        }
        else if (TextUtils.isEmpty(state)) {
            mstate.requestFocus();
            mstate.setError("This Field Is Mandatory");
        }
        else if (TextUtils.isEmpty(pincode)) {
            mpincode.requestFocus();
            mpincode.setError("This Field Is Mandatory");
        }
        else if (pincode.length()<6) {
            mpincode.requestFocus();
            mpincode.setError("Please Fill Correct Pincode");
        }

        else {
            String url = null;
            String uid = SaveUserId.getInstance(EditProfile.this).getUserId();
            String REGISTER_URL ="http://excelsamachar.com/lumen/public/user/edit/"+uid;


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
                            Log.d("reg00","regis11"+name);
                            try {
                                JSONObject jsonresponse = new JSONObject(response);
                                boolean success = jsonresponse.getBoolean("success");
                                String imagnewup = jsonresponse.getString("image");
                                String upname  = jsonresponse.getString("name");

                                if (success) {
                                    session.SaveImage(imagnewup);
                                    session.Savename(upname);
                                    Intent registerintent = new Intent(EditProfile.this, ReporterDashboard.class);
                                    startActivity(registerintent);
                                    finish();

                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
                                    builder.setMessage("Updation Failed...")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(EditProfile.this, response.toString(), Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("regerr00","regiserr11"+name);
                            Toast.makeText(EditProfile.this, error.toString(), Toast.LENGTH_LONG).show();

                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();



                    params.put(KEY_name, name);
                    //   params.put(KEY_mobile, mobile);
                    //    params.put(KEY_gender,gender);
                    params.put(KEY_state, state);
                    params.put(KEY_city, city);
                    params.put(KEY_district, district);
                    params.put(KEY_pincode, pincode);
                    params.put(KEY_image, image);
                    return params;

                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(EditProfile.this);
            requestQueue.add(stringRequest);
        }
    }


    public static ArrayList autocomplete(String input) {

        Log.d("chala00","chal");
        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {

            return resultList;

        } catch (IOException e) {

            return resultList;
        } finally {
            if (conn != null) {

                conn.disconnect();
            }
        }
        try {

            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            resultList = new ArrayList(predsJsonArray.length());
            Log.d("arrjs00", String.valueOf(predsJsonArray));
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch(JSONException e){

        }
        return resultList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements android.widget.Filterable {
        private ArrayList resultList;
        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }
        @Override
        public int getCount() {
            return resultList.size();
        }
        @Override
        public String getItem(int index) {
            return resultList.get(index).toString();
        }
        @Override
        public android.widget.Filter getFilter() {
            android.widget.Filter filter = new android.widget.Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {

                        resultList = autocomplete(constraint.toString());

                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            // Log.d("try4",str);
            super.onActivityResult(requestCode, resultCode, data);}catch (Exception e) {
            Log.d("try8", e.toString());
            //   Toast.makeText(getContext(), "On super " + e.toString(), Toast.LENGTH_LONG).show();

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

                Toast.makeText(EditProfile.this," Please Select Image For Uploading.... ",Toast.LENGTH_LONG).show();

            }else {
                Uri filePath = data.getData();
                Intent intentcrop = new Intent(EditProfile.this, CropImage.class);
                intentcrop.putExtra("ramji", filePath.toString());
                startActivityForResult(intentcrop, 6);
            }
        }


//        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            bitmap = (Bitmap) data.getExtras().get("data");
//            mnewsimage.setImageBitmap(bitmap);
//        }

        if(requestCode == CAMERA_REQUEST ){
//            if(data.getExtras()==null){
//
//                Toast.makeText(getContext()," Please Take Image For Uploading.... ",Toast.LENGTH_LONG).show();
//
//            }else {
//                Bitmap bitmapcamear = (Bitmap) data.getExtras().get("data");
//                String bitstring = getStringImage(bitmapcamear);
            Bitmap thumbnail = null;
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }


            if(thumbnail == null){

                Toast.makeText(EditProfile.this," Please Select Image For Uploading.... ",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
            else {
                boolean checktr = true;
                Intent intentcrop = new Intent(EditProfile.this, CropImage.class);
                intentcrop.putExtra("cameraji", imageUri.toString());
                intentcrop.putExtra("camerajiboolean", checktr);
                startActivityForResult(intentcrop, PICK_CROPIMAGE);
            }
//            }
//            bitmap =  UtilityClass.getImage(GlobalVariables.profilepic_name);
//            mnewsimage.setImageBitmap(bitmap);
//            dialog.dismiss();


        }

        if(requestCode==PICK_CROPIMAGE)
        {
            strtim = data.getStringExtra("cropimageone");
            Log.d("imageindash","imageindd "+strtim);
            bitmap = StringToBitMap(strtim);
            Log.d("imageinbitmap","imageinbit "+bitmap);
            userImageVIew.setImageBitmap(bitmap);
            dialog.dismiss();

        }


        if(requestCode==6)
        {
            strtim = data.getStringExtra("cropimage");
            Log.d("imageindash","imageindd "+strtim);
            bitmap = StringToBitMap(strtim);
            Log.d("imageinbitmap","imageinbit "+bitmap);
            userImageVIew.setImageBitmap(bitmap);
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



}
