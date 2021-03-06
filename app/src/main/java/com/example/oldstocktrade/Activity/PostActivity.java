package com.example.oldstocktrade.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oldstocktrade.Adapter.AnnouncementPostAdapter;
import com.example.oldstocktrade.Adapter.ImageAdapter;
import com.example.oldstocktrade.Model.Product;
import com.example.oldstocktrade.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOCATION = 3, PICK_IMAGE = 4;
    private ImageView close;
    private TextView post;
    private EditText description, address, price;
    private ImageView btnLocation;
    private EditText name;
    private ImageView chooseImage;
    private GridView chosenImages;
    private ArrayList<Uri> a = new ArrayList<Uri>();
    private Uri uriImage;
    private ProgressDialog progressDialog;
    private Double lon = null, lat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close = findViewById(R.id.btnClose);
        btnLocation = findViewById(R.id.btnLocation);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        address = findViewById(R.id.addressPost);
        price = findViewById(R.id.price);
        name = findViewById(R.id.name);
        chooseImage = findViewById(R.id.chooseImage);
        chosenImages = findViewById(R.id.chosenImages);
        Intent recieve = getIntent();
        if (recieve != null) {
            Bundle myBundle = recieve.getExtras();
            if (myBundle != null) {
                lat = myBundle.getDouble("lat");
                lon = myBundle.getDouble("long");
            }


        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, MapsActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putDouble("lat", lat);
                myBundle.putDouble("long", lon);
                intent.putExtras(myBundle);


                startActivityForResult(intent, REQUEST_CODE_LOCATION);
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] count={0,1,2};
                post.setEnabled(false);
                uploadMultipleImage(count);
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.clear();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                if (data.getStringExtra("location").length() != 0) {
                    String location = data.getStringExtra("location");
                    String straddress = data.getStringExtra("address");
                    // Set text view with string
                    Log.d("Location", "onActivityResult: " + location);
                    ;//split location bang - se ra longitude va latitude roi luu vao db
                    address.setText(straddress);
                    String a[] = location.split("#");
                    lat = Double.parseDouble(a[0]);
                    lon = Double.parseDouble(a[1]);

                }
            }
        }
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    uriImage = data.getData();
                    a.add(uriImage);
                    chosenImages.setAdapter(new ImageAdapter(this, a));
                }
                if (data.getClipData() != null) {
                    int countImage = data.getClipData().getItemCount();
                    for (int i = 0; i < countImage; i++) {
                        uriImage = data.getClipData().getItemAt(i).getUri();
                        a.add(uriImage);
                    }
                    chosenImages.setAdapter(new ImageAdapter(this, a));
                }
            }
        }
    }

    private void uploadMultipleImage(int count[]) {
        String des, addr, seller, pri, na;
        des = description.getText().toString();
        addr = address.getText().toString();
        seller = FirebaseAuth.getInstance().getCurrentUser().getUid();
        pri = price.getText().toString();
        na = name.getText().toString();
        long ts = System.currentTimeMillis();
        StorageReference imageFolder = FirebaseStorage.getInstance().getReference("Post").child("Image Folder");
        ArrayList<String> aImage = new ArrayList<String>();
        DatabaseReference df = FirebaseDatabase.getInstance().getReference("Products");
        String id = df.push().getKey();

        if (addr.matches("") || des.matches("") || pri.matches("") || na.matches("") || a.size() == 0) {
            AnnouncementPostAdapter postAdapter = new AnnouncementPostAdapter();
            postAdapter.show(getSupportFragmentManager(), "dialog");
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading please wait............");
            progressDialog.show();

            int limitImage= a.size();
            if(a.size()>3){
                limitImage=3;
            }
            for (int i = 0; i < limitImage; i++) {
                Uri image = a.get(i);
                StorageReference individualImage = imageFolder.child("Image" + image.getLastPathSegment());
                int finalI = i;
                individualImage.putFile(image)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                individualImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = String.valueOf(uri);
                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                df.child(id).child("ImageURL").child(String.valueOf(count[finalI])).setValue(url);
                                            }
                                        }, 1500);
                                    }
                                });
                            }
                        });
            }



        Product product = new Product(addr, "None", des, aImage, lat, lon, na, Double.parseDouble(pri), id, 0, seller, 1, ts, 0);
        HashMap<String, Object> map = new HashMap<>();
        map.put("ProID", product.getProID());
        map.put("Address", product.getAddress());
        map.put("Buyer", product.getBuyer());
        map.put("Seller", product.getSeller());
        map.put("Description", product.getDescription());
        map.put("Latitude", product.getLatitude());
        map.put("Longitude", product.getLongitude());
        map.put("Name", product.getName());
        map.put("Price", product.getPrice());
        map.put("Report", product.getReport());
        map.put("Status", product.getStatus());
        map.put("Timestamp", product.getTimestamp());
        map.put("VisibleToBuyer", product.isVisibleToBuyer());
        map.put("VisibleToSeller", product.isVisibleToSeller());
        map.put("Enable", true);
//        int time= 1500*a.size();
//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
                df.child(id).setValue(map);
                progressDialog.dismiss();
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
//            }
//        }, time);

    }
}
}