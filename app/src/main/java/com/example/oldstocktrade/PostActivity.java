package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 2,REQUEST_CODE_LOCATION= 3, PICK_IMAGE=4;
    private ImageView close;
    private ImageView imageAdded;
    private TextView post;
    EditText description, address, price, phone;
    Uri imageUri;
    private String imageUrl;
    Button btnLocation;
    private EditText name;
    private Button chooseImage, uploadImage;
    private TextView alert;
    private ArrayList<Uri> a= new ArrayList<Uri>();
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        close = findViewById(R.id.btnClose);
        btnLocation = findViewById(R.id.btnLocation);
        imageAdded = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        address = findViewById(R.id.addressPost);
        price= findViewById(R.id.price);
        phone= findViewById(R.id.phone);
        name= findViewById(R.id.name);
        chooseImage= findViewById(R.id.chooseImage);
        uploadImage= findViewById(R.id.uploadImage);
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
        imageAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }

    private void openImage() {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        if(requestCode == IMAGE_REQUEST){
            if(resultCode==RESULT_OK){
                imageUri = data.getData();
                imageAdded.setImageURI(imageUri);

            }
            else{
                Toast.makeText(this,"Try again!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }

        }
        if(requestCode == PICK_IMAGE){
            if(requestCode==RESULT_OK){
                if(data.getClipData()!=null){
                    int countImage= data.getClipData().getItemCount();
                    for(int i=0;i<countImage;i++){
                        uriImage= data.getClipData().getItemAt(i).getUri();
                        a.add(uriImage);
                    }
                    alert.setVisibility(View.VISIBLE);
                    alert.setText(a.size()+" hình");
                    chooseImage.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(this, "Please select multiple image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();
        if(imageUri!= null){
            StorageReference filePath= FirebaseStorage.getInstance().getReference("Post").child(System.currentTimeMillis()+"-"+getFileExtension(imageUri));
            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Uri downloadUri = (Uri) task.getResult();
                    imageUrl = downloadUri.toString();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");

                    String ProID = databaseReference.push().getKey();
                    HashMap<String,Object> map= new HashMap<>();
                    map.put("ProID",ProID);
                    map.put("ImageURL",imageUrl);
                    map.put("Description",description.getText().toString());
                    map.put("Address", address.getText().toString());
                    map.put("Seller", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("Price", price.getText().toString());
                    map.put("Phone", phone.getText().toString());
                    map.put("Name", name.getText().toString());
                    map.put("Status", 0);
                    map.put("Report", 0);
                    map.put("VisibleToBuyer", true);
                    map.put("VisibleToSeller", true);
                    map.put("Buyer", "None");
                    databaseReference.child(ProID).setValue(map);
                    pd.dismiss();
                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }else{
            Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }
}