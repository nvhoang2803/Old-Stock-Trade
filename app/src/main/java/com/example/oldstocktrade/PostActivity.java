package com.example.oldstocktrade;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oldstocktrade.Model.Product;
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
    private static final int REQUEST_CODE_LOCATION= 1, PICK_IMAGE=2;
    private ImageView close;
    private TextView post;
    private EditText description, address, price, phone;
    private Button btnLocation;
    private EditText name;
    private Button chooseImage;
    private TextView alert;
    private ArrayList<Uri> a= new ArrayList<Uri>();
    private Uri uriImage;
    private ProgressDialog progressDialog;
    private static int count=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close = findViewById(R.id.btnClose);
        btnLocation = findViewById(R.id.btnLocation);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        address = findViewById(R.id.addressPost);
        price= findViewById(R.id.price);
        phone= findViewById(R.id.phone);
        name= findViewById(R.id.name);
        chooseImage= findViewById(R.id.chooseImage);
        alert= findViewById(R.id.alert);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }
        });
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this,MapsActivity.class);
                startActivityForResult(intent,REQUEST_CODE_LOCATION);
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMultipleImage();
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
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
                String location = data.getStringExtra("location");
                String straddress = data.getStringExtra("address");
                // Set text view with string
                Log.d("Location", "onActivityResult: "+location);;//split location bang - se ra longitude va latitude roi luu vao db
                address.setText(straddress);
            }
        }
        if(requestCode == PICK_IMAGE){
            if(resultCode==RESULT_OK){
                if(data.getClipData()!=null){
                    int countImage= data.getClipData().getItemCount();
                    for(int i=0;i<countImage;i++){
                        uriImage= data.getClipData().getItemAt(i).getUri();
                        a.add(uriImage);
                    }
                    alert.setVisibility(View.VISIBLE);
                    alert.setText(a.size()+" hình");
                }
                else{
                    Toast.makeText(this, "Please select multiple image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void uploadMultipleImage(){
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Image Uploading please wait............");
        progressDialog.show();
        if(a.size()>0)
        {
            StorageReference imageFolder= FirebaseStorage.getInstance().getReference("Post").child("Image Folder");
            ArrayList<String> aImage= new ArrayList<String>();
            DatabaseReference df= FirebaseDatabase.getInstance().getReference("Products");
            String id= df.push().getKey();
            for(int i=0;i<a.size();i++){
                Uri image= a.get(i);
                StorageReference individualImage= imageFolder.child("Image"+ image.getLastPathSegment());
                individualImage.putFile(image)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                individualImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = String.valueOf(uri);
                                        df.child(id).child("Image").child(String.valueOf(count)).setValue(url);
                                        count++;
                                    }
                                });
                            }
                        });
//                        .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
            }

            String des, addr, seller, pri, ph, na;
            des= description.getText().toString();
            addr= address.getText().toString();
            seller= FirebaseAuth.getInstance().getCurrentUser().getUid();
            pri= price.getText().toString();
            ph= phone.getText().toString();
            na= name.getText().toString();
            Product product= new Product(addr, "None", des, aImage, 0, 0, na, Double.parseDouble(pri), id, 0, seller, 0, 0, 0, ph);
            HashMap<String, Object> map= new HashMap<>();
            map.put("Product id", product.getProID());
            map.put("Address", product.getAddress());
            map.put("Buyer", product.getBuyer());
            map.put("Seller", product.getSeller());
            map.put("Description", product.getDescription());
            map.put("Latitude", product.getLatitude());
            map.put("Longtitude", product.getLongtitude());
            map.put("Name product", product.getName());
            map.put("Price", product.getPrice());
            map.put("Report", product.getReport());
            map.put("Status", product.getStatus());
            map.put("Timestamp", product.getTimestamp());
            map.put("Rate", product.getRate());
            map.put("Phone", product.getPhone());
            df.child(id).setValue(map);
            progressDialog.dismiss();
            alert.setText("Upload image successfully");
            startActivity(new Intent(PostActivity.this,MainActivity.class));
            finish();
        }
        else{
            Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
        }
    }

}