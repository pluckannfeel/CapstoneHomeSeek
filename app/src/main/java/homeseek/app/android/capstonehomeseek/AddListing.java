package homeseek.app.android.capstonehomeseek;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import homeseek.app.android.capstonehomeseek.HttpRequests.AddListingRequest;

public class AddListing extends AppCompatActivity {
    Spinner s_type, s_term, s_city, s_bedrooms, s_bathrooms, s_status;
    EditText et_pname, et_address, et_larea, et_farea, et_price, et_hostname, et_cno, et_hdetails;
    ImageView iv_property_img;
    String[] cities, terms, types, bedrooms, bathrooms, status;
    int user_id;
    Button btn_publish;

    String selectedPhoto;
    String imagePath, imageName;
    String final_encoded_string, final_image_name;
    GalleryPhoto galleryPhoto;
    protected static final int GALLERY_REQUEST = 1;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);
        getSupportActionBar().setTitle("Add Listing");

        Intent intent = getIntent();
        user_id = Integer.parseInt(intent.getStringExtra("user_id"));
        //initialization of inputs
        et_pname = (EditText) findViewById(R.id.et_pname);
        s_type = (Spinner) findViewById(R.id.s_ptype);
        s_term = (Spinner) findViewById(R.id.s_term);
        s_city = (Spinner) findViewById(R.id.s_city);
        et_address = (EditText) findViewById(R.id.et_address);
        et_larea = (EditText) findViewById(R.id.et_larea);
        et_farea = (EditText) findViewById(R.id.et_farea);
        et_price = (EditText) findViewById(R.id.et_price);
        s_bedrooms = (Spinner) findViewById(R.id.s_bedrooms);
        s_bathrooms = (Spinner) findViewById(R.id.s_bathrooms);
        et_hostname = (EditText) findViewById(R.id.et_hostname);
        et_cno = (EditText) findViewById(R.id.et_cno);
        et_hdetails = (EditText) findViewById(R.id.et_hdetails);
        s_status = (Spinner) findViewById(R.id.s_status);
        iv_property_img = (ImageView) findViewById(R.id.iv_property_image);
        btn_publish = (Button) findViewById(R.id.AddProperty);
        AddProperty(btn_publish);

        //array datas on res/strings.xml
        cities = getResources().getStringArray(R.array.cities);
        terms = getResources().getStringArray(R.array.property_term);
        types = getResources().getStringArray(R.array.property_type);
        bedrooms = getResources().getStringArray(R.array.bedrooms);
        bathrooms = getResources().getStringArray(R.array.bathrooms);
        status = getResources().getStringArray(R.array.status);

        //spinner cities
        final ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_city.setAdapter(cityAdapter);
        s_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_city.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner terms
        final ArrayAdapter<String> termAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, terms);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_term.setAdapter(termAdapter);
        s_term.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_term.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner types
        final ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_type.setAdapter(typeAdapter);
        s_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_type.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner bedrooms
        final ArrayAdapter<String> bedAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, bedrooms);
        bedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_bedrooms.setAdapter(bedAdapter);
        s_bedrooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_bedrooms.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner bathrooms
        final ArrayAdapter<String> bathAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, bathrooms);
        bathAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_bathrooms.setAdapter(bathAdapter);
        s_bathrooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_bathrooms.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner status
        final ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, status);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_status.setAdapter(statusAdapter);
        s_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                s_status.setPrompt(itemSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        //gallery intent
        chooseFromGallery(iv_property_img);

    }

    private void AddProperty(Button publish){
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

                progressDialog = ProgressDialog.show(viewGroup.getContext(), "Please wait ..", "Publishing..", true);
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            finalizeData();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Something went wrong (progress dialog)", Toast.LENGTH_LONG).show();
                        } finally {
                            progressDialog.dismiss();
                        }

                    }
                }).start();
            }
        });
    }

    private void finalizeData(){
        try {
        if(checkInputs()){
            final String p_name = et_pname.getText().toString();
            final String p_type = s_type.getSelectedItem().toString();
            final String p_term = s_term.getSelectedItem().toString();
            final String p_city = s_city.getSelectedItem().toString();
            final String p_address = et_address.getText().toString();
            final String p_larea = et_larea.getText().toString();
            final String p_farea = et_farea.getText().toString();
            final int p_price = Integer.parseInt(et_price.getText().toString());
            final int p_bedrooms = Integer.parseInt(s_bedrooms.getSelectedItem().toString());
            final int p_bathrooms = Integer.parseInt(s_bathrooms.getSelectedItem().toString());
            final String p_hostname = et_hostname.getText().toString();
            final String p_cno = et_cno.getText().toString();
            final String p_hdetails = et_hdetails.getText().toString();
            final String p_status = s_status.getSelectedItem().toString();

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean duplicate = jsonResponse.getBoolean("hasData");
                        boolean success = jsonResponse.getBoolean("success");

                        if(duplicate){
                            Toast.makeText(getApplicationContext(), "Error: Duplicate data", Toast.LENGTH_LONG).show();
                        }else{
                            if (success) {
                                Toast.makeText(getApplicationContext(), "Successfully published.", Toast.LENGTH_LONG).show();
                                finish();
                            }else{
                                Log.d("p_name", p_name);
                                Log.d("p_type", p_type);
                                Log.d("p_term", p_term);
                                Log.d("p_city", p_city);
                                Log.d("p_address", p_address);
                                Log.d("p_larea", p_larea);
                                Log.d("p_farea", p_farea);
                                Log.d("p_price", String.valueOf(p_price));
                                Log.d("p_bed", String.valueOf(p_bedrooms));
                                Log.d("p_bath", String.valueOf(p_bathrooms));
                                Log.d("p_hostname", p_hostname);
                                Log.d("p_cno", p_cno);
                                Log.d("p_hdetails", p_hdetails);
                                Log.d("p_status", p_status);
                                Log.d("encoded_string", final_encoded_string);
                                Log.d("image_name", final_image_name);
                                Toast.makeText(getApplicationContext(), "Failed to publish listing.", Toast.LENGTH_LONG).show();
                            }
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

            AddListingRequest addListingRequest = new AddListingRequest(p_name, p_type, p_term, p_city, p_address, p_larea,p_farea,
                    p_price, p_bedrooms, p_bathrooms,
                    p_hostname, p_cno, p_hdetails, p_status, final_encoded_string, imageName,
                    user_id, responseListener);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(addListingRequest);
        }else{
            Toast.makeText(getApplicationContext(), "Insufficient information provided", Toast.LENGTH_SHORT).show();
        }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }catch (Exception e){
            Log.d("Error ", e.toString());
        }
    }

    private boolean checkInputs(){
        if(et_pname.getText().toString().trim().equals("") || s_type.getSelectedItem().toString().trim().equals("") ||
                s_term.getSelectedItem().toString().trim().equals("") || s_city.getSelectedItem().toString().trim().equals("") ||
                et_address.getText().toString().trim().equals("") ||  et_price.getText().toString().trim().equals("") ||
                s_bedrooms.getSelectedItem().toString().trim().equals("") || s_bathrooms.getSelectedItem().toString().trim().equals("") ||
                et_hostname.getText().toString().trim().equals("") || et_cno.getText().toString().trim().equals("") ||
                et_hdetails.getText().toString().trim().equals("") || s_status.getSelectedItem().toString().trim().equals("") ||
                final_image_name.equals("") || final_encoded_string.equals(""))
            return false;
        else
            return true;
    }

    private void getImageDetails(String encoded_string, String image_name){
        final_encoded_string = encoded_string;
        final_image_name = image_name;
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
                                    getImageDetails(encodedImage, image_name);
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
}
