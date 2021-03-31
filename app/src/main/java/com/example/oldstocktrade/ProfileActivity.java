package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.oldstocktrade.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 2;
    private ImageView profile_image;
    private TextView username;
    private EditText txt_phone;
    private EditText txt_address;
    private FirebaseUser fuser;
    private DatabaseReference ref;
    private ImageView btnClose;
    private TextView btnSave,change_photo;
    private String imageUrl;
    private Uri imageUri;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        txt_phone  = findViewById(R.id.txt_phone);
        txt_address  = findViewById(R.id.txt_address);
        btnClose = findViewById(R.id.btnClose);
        btnSave = findViewById(R.id.btnSave);
        change_photo = findViewById(R.id.change_photo);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user.getImageURL().equals("default"));
//                    profile_image.setImageResource(R.mipmap.ic_launcher);
                else
                    //Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()).into(imageView);
                    Glide.with(ProfileActivity.this).load(user.getImageURL()).apply(RequestOptions.circleCropTransform()).into(profile_image);
                username.setText(user.getUsername());
                txt_phone.setText(user.getPhone());
                txt_address.setText(user.getAddress());
                txt_phone.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        btnSave.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        btnSave.setVisibility(View.VISIBLE);
                    }
                });
                txt_address.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        btnSave.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        btnSave.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

    }
    ValueEventListener valueEventListener = null;

    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ref.addValueEventListener(valueEventListener);

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

        if(requestCode == IMAGE_REQUEST){
            if(resultCode==RESULT_OK){
                imageUri = data.getData();
                Glide.with(ProfileActivity.this).load(imageUri).apply(RequestOptions.circleCropTransform()).into(profile_image);
            }
            else{
                Toast.makeText(this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                finish();
            }

        }
    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();
        String phone = txt_phone.getText().toString();
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(ProfileActivity.this, "Your phone number cannot be empty!",Toast.LENGTH_SHORT).show();
        }
        else {
            if (phone.length() == 10 && TextUtils.isDigitsOnly(phone)){

                if(imageUri!= null){
                    StorageReference filePath= FirebaseStorage.getInstance().getReference("User").child(System.currentTimeMillis()+"-"+getFileExtension(imageUri));
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
//
                            if (!user.getImageURL().equals("default")) {
                                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getImageURL());
                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                        Log.d("Delete success", "onSuccess: deleted file image");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                        Log.d("Delete fail", "onFailure: did not delete file");
                                    }
                                });
                            }
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("username",username.getText().toString());
                            hashMap.put("phone",txt_phone.getText().toString());
                            hashMap.put("address",txt_address.getText().toString());
                            hashMap.put("imageURL",imageUrl);

                            ref.updateChildren(hashMap);

                            pd.dismiss();
                            startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }
            else {
                Toast.makeText(ProfileActivity.this, "Your phone number is invalid.",Toast.LENGTH_SHORT).show();
            }

        }

//        else{
//            Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
//        }

    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }
}