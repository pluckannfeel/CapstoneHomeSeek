package homeseek.app.android.capstonehomeseek.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import homeseek.app.android.capstonehomeseek.BaseUrl;
import homeseek.app.android.capstonehomeseek.EditUserInfo;
import homeseek.app.android.capstonehomeseek.HttpRequests.AccountImageRequest;
import homeseek.app.android.capstonehomeseek.HttpRequests.SendUserImageRequest;
import homeseek.app.android.capstonehomeseek.HttpRequests.UserInfoRequest;
import homeseek.app.android.capstonehomeseek.MySingleton;
import homeseek.app.android.capstonehomeseek.R;
import homeseek.app.android.capstonehomeseek.UserChangePassword;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    TextView tv_cno, tv_email, tv_uname, tv_address, fullname, tv_image_name;
    String userID, firstname, lastname, username, cno, email, address, image_path;
    ImageButton editProfile;
    ImageView accountPhoto;
    View view;
    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    String selectedPhoto;
    String imagePath, image_name;
    ProgressDialog progressDialog;
    BaseUrl baseUrl;
    com.android.volley.toolbox.ImageLoader displayImageLoader;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Uri imageUri;
    protected static final int CAMERA_REQUEST = 1;
    protected static final int GALLERY_REQUEST = 2;

    public MyAccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_account, container, false);
        try{
            EditDetails(editProfile);
//        firstname = getArguments().getString("firstname");
//        lastname = getArguments().getString("lastname");
//        username = getArguments().getString("username");
//        cno = getArguments().getString("cno");
//        email = getArguments().getString("email");
//        address = getArguments().getString("address");
        userID = getArguments().getString("userID");

        fullname = (TextView) view.findViewById(R.id.name);
        tv_uname = (TextView) view.findViewById(R.id.user_name);
        tv_cno = (TextView) view.findViewById(R.id.user_cno);
        tv_email = (TextView) view.findViewById(R.id.user_email);
        tv_address = (TextView) view.findViewById(R.id.user_address);

        //upload photo
        accountPhoto = (ImageView) view.findViewById(R.id.display_image);
        startDialog();
        galleryPhoto = new GalleryPhoto(getActivity().getApplicationContext());

        //initialize swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLists);
        swipeRefreshLayout.setOnRefreshListener(this);

        //displays photo
        DisplayImage();
        GetUserInformation();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }

    private void GetUserInformation(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    firstname = jsonResponse.getString("firstname");
                    lastname = jsonResponse.getString("lastname");
                    username = jsonResponse.getString("username");
                    cno = jsonResponse.getString("cno");
                    email = jsonResponse.getString("email");
                    address = jsonResponse.getString("address");

                    if(success){
                        fullname.setText(firstname + " " + lastname);
                        tv_uname.setText(username);
                        tv_cno.setText(cno);
                        tv_email.setText(email);
                        tv_address.setText(address);
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "failed to load info", Toast.LENGTH_SHORT).show();
                        Log.d("firstname", firstname);
                        Log.d("lastname", lastname);
                        Log.d("username", username);
                        Log.d("cno", cno);
                        Log.d("email", email);
                        Log.d("address", address);
                    }

                }catch (JSONException e) {
                    Log.e("Response", response);
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to connect to server.", Toast.LENGTH_LONG).show();
                }finally {
                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

            }
        };

        UserInfoRequest userInfoRequest = new UserInfoRequest(Integer.parseInt(userID), responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(userInfoRequest);
    }

    @Override
    public void onRefresh() {
        //gets data from home fragment
        DisplayImage();
        GetUserInformation();
    }

    private void EditDetails(ImageButton btnEdit){
        btnEdit = (ImageButton) view.findViewById(R.id.editDetails);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[] {"Edit Information", "Change Password"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Choose Action: ");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //edit info
                            Intent intent = new Intent(getActivity().getApplicationContext(), EditUserInfo.class);
                            intent.putExtra("user_id", userID);
                            intent.putExtra("firstname", firstname);
                            intent.putExtra("lastname", lastname);
                            intent.putExtra("cno", cno);
                            intent.putExtra("email", email);
                            intent.putExtra("address", address);
                            startActivity(intent);
                        } else {
                            //change password
                            Intent intent = new Intent(getActivity().getApplicationContext(), UserChangePassword.class);
                            intent.putExtra("user_id", userID);
                            startActivity(intent);
                        }
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void startDialog(){
        final String[] items = new String[] {"Take a picture", "Choose from storage"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose Action: ");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //camera
                    takePhoto();
                } else {
                    //gallery
                    chooseFromGallery();
                }
            }
        });
        final AlertDialog dialog = builder.create();

        accountPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
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



    private void savePhoto(final String image_name){
        final String[] items = new String[] {"Yes", "No"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Save Image?");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //yes
                    try {
                        //2 important inputs bitmap (encoded to base 64) and image name
                        Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(256,256).getBitmap();
                        final String encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 80);
                        String final_image_name = image_name;
                        Log.d(getTag(), encodedImage);

                        Bitmap decodedBm = decodeBase64(encodedImage);
                        accountPhoto.setImageBitmap(decodedBm);

//                        progress dialog will appear
                        progressDialog = ProgressDialog.show(view.getContext(),
                                "Please wait ..", "Uploading Image ..", true);
                        progressDialog.setCancelable(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    SendUserImage(encodedImage, image_name);
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                    Toast.makeText(getActivity().getApplicationContext(), "Something went wrong (progress dialog)", Toast.LENGTH_LONG).show();
                                }

                            }
                        }).start();


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    //no
                    DisplayImage();
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void chooseFromGallery(){
        //call it to open the gallery
        startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
    }


    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFromCam = userID + timeStamp +".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(),imageFromCam);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private static final int RESULT = 1;
    Bitmap imgBitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    selectedPhoto = getRealPathFromURI(imageUri);
                    getActivity().getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getActivity().getContentResolver();
                    try {
                        //permission issues
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT);
                            }
                        else
                            {
                                imgBitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                                imagePath = selectedImage.toString();

                                //gets the image name
                                image_name = selectedImage.getLastPathSegment();
                                accountPhoto.setImageBitmap(imgBitmap);
                                savePhoto(image_name);
                            }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), "Something went wrong while taking a photo", Toast.LENGTH_LONG).show();
                        Log.e("Camera", e.toString());
                    }

                }
            case GALLERY_REQUEST:
                if(resultCode == Activity.RESULT_OK){
                    try {
                        galleryPhoto.setPhotoUri(data.getData());
                        String photoPath = galleryPhoto.getPath();
                        Uri imageUri = Uri.fromFile(new File(photoPath));
                        selectedPhoto = photoPath;
                        imgBitmap = ImageLoader.init().from(photoPath).requestSize(128, 128).getBitmap();
                        accountPhoto.setImageBitmap(imgBitmap);
                        imagePath = photoPath.toString();

                        //gets the image name
                        image_name = imageUri.getLastPathSegment();
                        String checkImageName = image_name.replaceAll(" ", "_").toLowerCase();
                        savePhoto(checkImageName);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        Log.d("error gallery", e.toString());
                        e.printStackTrace();
                    }
                }
        }
    }

    private void SendUserImage(String encoded_string, String image_name){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
//                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonResponse = new JSONObject(response);
//                    boolean duplicate = jsonResponse.getBoolean("duplicate");
                    boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            Toast.makeText(view.getContext(), "Successfully saved.", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            DisplayImage();
                        }


                }catch (JSONException e) {
                    Log.e("Response", response);
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(view.getContext(), "Failed to connect to server.", Toast.LENGTH_LONG).show();
                }finally {
                    progressDialog.dismiss();
                }

            }
        };

        SendUserImageRequest sendUserImageRequest = new SendUserImageRequest(encoded_string, image_name, Integer.parseInt(userID), responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(sendUserImageRequest);
    }

    private void DisplayImage(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success){
                        String response_image_name = jsonResponse.getString("image_name");
                        baseUrl = new BaseUrl();
                        String url = baseUrl.getBaseUrl() + "user_images/" + response_image_name;

                        //displays image from a url
                        Log.e("Response", url);
                        displayImageLoader = MySingleton.getInstance(getActivity().getApplicationContext()).getImageLoader();
                        displayImageLoader.get(url, displayImageLoader.getImageListener(accountPhoto,R.drawable.default_photo,R.drawable.default_photo));

                    }else{
                        Log.e("Response", response);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        };

        AccountImageRequest accountImageRequest = new AccountImageRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(accountImageRequest);
    }
}
