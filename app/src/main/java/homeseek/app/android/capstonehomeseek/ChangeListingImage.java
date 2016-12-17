package homeseek.app.android.capstonehomeseek;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import homeseek.app.android.capstonehomeseek.HttpRequests.ChangeListingImageRequest;
import homeseek.app.android.capstonehomeseek.HttpRequests.ListingImageRequest;

public class ChangeListingImage extends AppCompatActivity {
    ImageView iv_property_img;
    BaseUrl baseUrl;
    com.android.volley.toolbox.ImageLoader displayImageLoader;
    String selectedPhoto;
    String imagePath, imageName;
    String final_encoded_string, final_image_name;
    GalleryPhoto galleryPhoto;
    protected static final int GALLERY_REQUEST = 1;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_listing_image);
        getSupportActionBar().setTitle("Change Image");
        iv_property_img = (ImageView) findViewById(R.id.iv_property_image);
        chooseFromGallery(iv_property_img);

        //display recent property image
        progressDialog = ProgressDialog.show(this, "Please wait ..", "Taking image ..", true);
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    DisplayImage();
                    progressDialog.dismiss();
                }catch (Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Something went wrong (progress dialog)", Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

    private void ChangeListingImage(String encoded_string, String image_name){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        iv_property_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        Toast.makeText(getApplicationContext(), "Successfully saved.", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        DisplayImage();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "Failed to change listing image.", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        DisplayImage();
                    }
                }catch (JSONException e) {
                    Log.e("Response", response);
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to connect to server.", Toast.LENGTH_LONG).show();
                }finally {
                    progressDialog.dismiss();
                }

            }
        };

        ChangeListingImageRequest changeListingImageRequest = new ChangeListingImageRequest
                (encoded_string, image_name, Integer.parseInt(getPropertyID()), responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(changeListingImageRequest);
    }

    private void chooseFromGallery(ImageView img_view){
        img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call it to open the gallery
                galleryPhoto = new GalleryPhoto(getApplicationContext());

                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GALLERY_REQUEST:
                if(resultCode == Activity.RESULT_OK){
                    galleryPhoto.setPhotoUri(data.getData());

                    Bitmap imgBitmap;
                    String photopath = galleryPhoto.getPath();
                    Uri imageUri = Uri.fromFile(new File(photopath));


                    selectedPhoto = photopath.toString();
                    try{
                        imgBitmap = ImageLoader.init().from(photopath).requestSize(256,256).getBitmap();
                        iv_property_img.setImageBitmap(imgBitmap);
                        imagePath = photopath.toString();

                        //get the image name
                        imageName = imageUri.getLastPathSegment();
                        String checkImageName = imageName.replaceAll(" ","_").toLowerCase();
                        uploadPhoto(checkImageName);
                    }catch (FileNotFoundException e) {
                        Log.d("Error: ", e.toString());
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.d("error gallery", e.toString());
                        e.printStackTrace();
                    }


                }
        }
    }

    private void uploadPhoto(final String image_name){
        //root view
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        final String[] items = new String[] {"Yes", "No"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Use this image?");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //yes
                    try {
                        //2 important inputs bitmap (encoded to base 64) and image name
                        Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(256,256).getBitmap();
                        final String encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 80);
                        Log.d("Encoded image: ", encodedImage);

                        Bitmap decodedBm = decodeBase64(encodedImage);
                        iv_property_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        iv_property_img.setImageBitmap(decodedBm);

//                        progress dialog will appear
                        progressDialog = ProgressDialog.show(viewGroup.getContext(), "Please wait ..", "Taking image ..", true);
                        progressDialog.setCancelable(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    ChangeListingImage(encodedImage,image_name);
                                    progressDialog.dismiss();
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Something went wrong (progress dialog)", Toast.LENGTH_LONG).show();
                                }

                            }
                        }).start();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    //no
                    iv_property_img.setScaleType(ImageView.ScaleType.CENTER);
                    iv_property_img.setImageResource(R.drawable.addproperty);

                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void DisplayImage(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success){
                        String img_path = jsonResponse.getString("image_path");
                        baseUrl = new BaseUrl();
                        String url = baseUrl.getBaseUrl() + img_path;

                        //displays image from a url
                        Log.e("Response", url);
                        displayImageLoader = MySingleton.getInstance(getApplicationContext()).getImageLoader();
                        iv_property_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        displayImageLoader.get(url, displayImageLoader.getImageListener(iv_property_img,R.drawable.default_photo,R.drawable.default_photo));

                    }else{
                        Log.e("Response", response);
                    }

                }catch (JSONException e){
                    Log.d("Error", response);
                    e.printStackTrace();
                }
//                catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }

            }
        };

        ListingImageRequest listingImageRequest = new ListingImageRequest(getPropertyID(),responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(listingImageRequest);
    }

    private String getPropertyID(){
        Intent intent = getIntent();
        String property_id = intent.getStringExtra("property_id");

        return property_id;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void getImageDetails(String encoded_string, String image_name){
        final_encoded_string = encoded_string;
        final_image_name = image_name;
    }



}
