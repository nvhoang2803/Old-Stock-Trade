package com.example.oldstocktrade.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

import java.util.HashMap;

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
    private Uri imageUri = null;
    private User user;
    private CheckBox cbChangePw;
    LinearLayout llConfirm,llCurrentPw,llNewPw;
    EditText txtCurrentPw,txtNewPw,txtConfirm;
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
        llConfirm = findViewById(R.id.llConfirm);
        llCurrentPw = findViewById(R.id.llCurrentPw);
        llNewPw = findViewById(R.id.llNewPw);
        cbChangePw = findViewById(R.id.cbChangePw);
        cbChangePw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    llCurrentPw.setVisibility(View.VISIBLE);
                    llNewPw.setVisibility(View.VISIBLE);
                    llConfirm.setVisibility(View.VISIBLE);
                    txtCurrentPw = findViewById(R.id.txtCurrentPw);
                    txtNewPw  = findViewById(R.id.txtNewPw);
                    txtConfirm = findViewById(R.id.txtConfirm);
                }
                else{
                    llCurrentPw.setVisibility(View.INVISIBLE);
                    llNewPw.setVisibility(View.INVISIBLE);
                    llConfirm.setVisibility(View.INVISIBLE);
                    txtCurrentPw = null;
                    txtNewPw  = null;
                    txtConfirm = null;

                }
            }
        });
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user.getImageURL().equals("default"))
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                else{
                    //Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(user.getImageURL()).getContent());
                    //imageView.setImageBitmap(bitmap);
                    Glide.with(getApplicationContext())
                            .load(user.getImageURL())
                            .apply(RequestOptions.circleCropTransform())
                            .into(profile_image)
                    ;
                }


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
        });

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
                Log.d("Image", "onActivityResult: "+imageUri);
                //Bitmap bitmap = (Bitmap) data.getExtras().get("dat");
                //profile_image.setImageURI(imageUri);
                Glide.with(ProfileActivity.this)
                        .load(imageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profile_image)
                ;

            }
            else{
                Toast.makeText(this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }

        }
    }

    private void uploadImage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();
        String phone = txt_phone.getText().toString();
        if(!changePassword(cbChangePw.isChecked())){
            pd.dismiss();
            return;
        }
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
                            Log.d("update user", imageUrl);
                            if (!(user.getImageURL().equals("default"))) {
                                if(user.getImageURL().contains("firebase")){
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
                            pd.dismiss();
                        }
                    });


                }
                else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("username",username.getText().toString());
                    hashMap.put("phone",txt_phone.getText().toString());
                    hashMap.put("address",txt_address.getText().toString());
                    //hashMap.put("imageURL",imageUrl);

                    ref.updateChildren(hashMap);

                    pd.dismiss();
                    startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                    finish();
                }

            }
            else {
                pd.dismiss();
                Toast.makeText(ProfileActivity.this, "Your phone number is invalid.",Toast.LENGTH_SHORT).show();
            }

        }

//        else{
//            Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
//        }

    }
    private boolean changePassword(boolean isChecked){
        if(!isChecked)
            return true;
        String current = txtCurrentPw.getText().toString();
        String newPw = txtNewPw.getText().toString();
        String confirm = txtConfirm.getText().toString();
        if(TextUtils.isEmpty(current)||TextUtils.isEmpty(newPw)||TextUtils.isEmpty(confirm)){
            Toast.makeText(ProfileActivity.this, "Please fill in all Password' fields",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!newPw.equals(confirm)){
            Toast.makeText(ProfileActivity.this, "New Password and Confirm Password are not the same",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(newPw.length()<6){
            Toast.makeText(ProfileActivity.this, "New Password is equal or greater than 6 characters ",Toast.LENGTH_SHORT).show();
            return false;
        }
        else{

            AuthCredential credential = EmailAuthProvider.getCredential(fuser.getEmail(), current);
            fuser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                fuser.updatePassword(newPw)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("Change Password", "User password updated.");
                                                    Toast.makeText(ProfileActivity.this, "Change Password successfully!",Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(ProfileActivity.this, "Current Password is not right!",Toast.LENGTH_SHORT).show();
                                                    Log.d("Change Password", "Error User password updated.");

                                                }
                                            }
                                        });
                            } else {

                                Log.d("Change pw", "Error auth failed");
                                Toast.makeText(ProfileActivity.this, "Current Password is not right!",Toast.LENGTH_SHORT).show();


                            }
                        }
                    });

            return true;

        }


    }
    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }
}