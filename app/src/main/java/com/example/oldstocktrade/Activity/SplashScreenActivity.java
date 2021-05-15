package com.example.oldstocktrade.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.splashscreen);
//        Thread thread = new Thread(){
//            public void run(){
//                try {
//                    sleep(3000);
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                }
//                finally {
//                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        };
//        thread.start();
//    }
private Handler handler;
    private Runnable runnableLoading = new Runnable() {
        @Override
        public void run() {

            ConnectivityManager connectivityManager = (ConnectivityManager) SplashScreenActivity.this.getSystemService(Service.CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (firebaseUser != null) {
                        String userId = firebaseUser.getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if (user.getType() == 0) {
                                    moveActivity(SplashScreenActivity.this, MainActivity.class);
                                } else {
                                    moveActivity(SplashScreenActivity.this, AdminActivity.class);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        moveActivity(SplashScreenActivity.this, LoginActivity.class);
                    }
                } else {
                    new AlertDialog.Builder(SplashScreenActivity.this)
                            .setTitle("DISCONNECTED")
                            .setMessage("You are not connect to the internet")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    handler.post(runnableLoading);
                                }
                            })
                            .show();
                }
            } else {
                Toast.makeText(SplashScreenActivity.this, "Error CONNECTIVITY SERVICE", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        handler = new Handler();

        handler.post(runnableLoading);

    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }
}