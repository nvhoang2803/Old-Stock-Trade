package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    FirebaseAuth auth;
    SignInButton btnGoogleLog;
    DatabaseReference reference;
    ProgressDialog pd;
    private static final int SIGN_IN=1;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pd = new ProgressDialog(this);
        Button btn_login = (Button) findViewById(R.id.btn_login2);

        EditText email = findViewById(R.id.email_login);
        EditText password = findViewById(R.id.password_login);
        auth = FirebaseAuth.getInstance();
        Log.d("current user", "onCreate: "+FirebaseAuth.getInstance().getCurrentUser());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogleLog = findViewById(R.id.btnGoogleLog);
//        btnGoogleLog.setColorScheme(SignInButton.COLOR_AUTO);
//        btnGoogleLog.setSize(SignInButton.SIZE_WIDE);
//        for (int i = 0; i < btnGoogleLog.getChildCount(); i++) {
//            View v = btnGoogleLog.getChildAt(i);
//
//            if (v instanceof TextView) {
//                TextView tv = (TextView) v;
//                tv.setText("Log in with Google");
//
//                return;
//            }
//        }
        btnGoogleLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                signIn();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(LoginActivity.this, "All details must be filled", Toast.LENGTH_SHORT).show();
                }
                else {
                    Login(txt_email,txt_password);
                }

            }
        });
        TextView txt_signup = findViewById(R.id.txt_signup);
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });


    }
    private void signIn() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("login by google", "firebaseAuthWithGoogle:" + account.getId()+account);
                firebaseAuthWithGoogle(account.getIdToken());
                pd.setMessage("Please Wait!");
                pd.show();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("login by google", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login by google", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.hasChild("id")) {
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();

                                    }else{
                                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                        if(account != null){
                                            String userid = user.getUid();

                                            //reference = FirebaseDatabase.getInstance("https://old-stock-trade-default-rtdb.firebaseio.com/").getReference("Users").child(userid);

                                            HashMap<String,String> hashMap =  new HashMap<>();
                                            hashMap.put("id", userid);
                                            hashMap.put("username", account.getDisplayName());
                                            hashMap.put("imageURL", String.valueOf(account.getPhotoUrl()));
                                            hashMap.put("status", "offline");

                                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(i);
                                                        finish();

                                                    }
                                                }
                                            });
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login by google", "signInWithCredential:failure", task.getException());
//
                        }
                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();

        }
    }

    private void Login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();

                }
                else {
                    Toast.makeText(LoginActivity.this, "Your email or password is uncorrected.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}