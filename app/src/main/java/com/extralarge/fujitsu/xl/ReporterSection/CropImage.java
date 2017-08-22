package com.extralarge.fujitsu.xl.ReporterSection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.extralarge.fujitsu.xl.R;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CropImage extends AppCompatActivity {

    CropImageView cropImageView;
    LinearLayout cropBtn;
    boolean cameraSource;
    String filename="";
    String photoUri;
    Bitmap bitmap;
    Bitmap cropped;
    public static final String PHOTO_URI="photouri";
    private static final int PICK_CROPIMAGE = 4;
    boolean checkcam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        cropImageView=(CropImageView)findViewById(R.id.cropImageView);
        cropBtn=(LinearLayout)findViewById(R.id.cropBtn);

       Intent data = getIntent();
        String camearaimage = data.getStringExtra("cameraji");
        checkcam = data.getBooleanExtra("camerajiboolean",false);
        Log.d("camimg00","camimg "+camearaimage);
        if(checkcam){

         //   bitmap = StringToBitMap(camearaimage);

            try {
              bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), Uri.parse(camearaimage));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("bitmaocam00","bitmapvam "+bitmap);
        }

//        cameraSource=getIntent().getBooleanExtra(GlobalVariables.CAMERA_SOURCE,false);
//       checkcam = getIntent().getBooleanExtra("camerajiboolean",false);
//        if(cameraSource) {
//            filename =GlobalVariables.profilepic_name;
//            bitmap=UtilityClass.getImage(filename);
//        }

else {
            photoUri = getIntent().getStringExtra("ramji");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(photoUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        cropImageView.setImageBitmap(bitmap);


        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("8888888","00000");
                cropImage(bitmap);
            }
        });
    }

    public void cropImage(Bitmap bitmap)

    {
         cropped = cropImageView.getCroppedImage();

        if(checkcam) {
            try {
//                UtilityClass.saveImage(GlobalVariables.profilepic_name, cropped);
//                Intent returnIntent = new Intent();
//                setResult(RESULT_OK, returnIntent);
            String bitimage = getStringImage(cropped);
                Log.d("010101",""+cropped.toString());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("cropimageone",bitimage);
            setResult(PICK_CROPIMAGE, returnIntent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("010111101",""+e.toString());
            }
        }else {
            String bitimage = getStringImage(cropped);
          Intent returnIntent = new Intent();
         returnIntent.putExtra("cropimage",bitimage);
          setResult(6, returnIntent);
            finish();

        }
        }

    public String getStringImage(Bitmap bmp){
        long lengthbmp = BitmapCompat.getAllocationByteCount(bmp);
        byte[] imageBytes = null;
        if(lengthbmp > 16000000){

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos);
            imageBytes = baos.toByteArray();
            Log.d("202kghg",""+lengthbmp);
        }
        else{

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            imageBytes = baos.toByteArray();
            Log.d("202kghg",""+lengthbmp);
        }


        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
    public void onBackPressed() {

    }
}
