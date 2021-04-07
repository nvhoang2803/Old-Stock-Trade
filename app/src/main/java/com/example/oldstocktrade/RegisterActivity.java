package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username, email, password;
    Button btn_register;
    ProgressDialog pd;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pd = new ProgressDialog(this);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(RegisterActivity.this, "All details must be filled.",Toast.LENGTH_SHORT).show();
                }else if (txt_password.length()<6){
                        Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                    }
                    else
                        register(txt_username,txt_email,txt_password);
            }
        });
        TextView txt_signin = findViewById(R.id.txt_signin);
        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    private void register(String txt_username, String email, String password){
        pd.setMessage("Please Wait!");
        pd.show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("SignUpEmail", "Email sent.");
                                                Toast.makeText(RegisterActivity.this, "You registered successfully. Please check your email for verification", Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(RegisterActivity.this, "Something was wrong", Toast.LENGTH_SHORT).show();
                                                Log.d("Error verification", "onComplete: "+task.getException().getMessage());

                                            }
                                        }
                                    });
                            String userid = user.getUid();

                            reference = FirebaseDatabase.getInstance("https://old-stock-trade-default-rtdb.firebaseio.com/").getReference("Users").child(userid);

                            HashMap<String,Object> hashMap =  new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", txt_username);
                            hashMap.put("imageURL","default");
                            hashMap.put("status", "offline");
                            hashMap.put("phone", "");
                            hashMap.put("address", "");
                            hashMap.put("type", 0);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        if(user.isEmailVerified()){
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(RegisterActivity.this, "Please check your email to verification", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Cannot register with this email !",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
