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

public class LoadingActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable runnableLoading = new Runnable() {
        @Override
        public void run() {

            ConnectivityManager connectivityManager = (ConnectivityManager) LoadingActivity.this.getSystemService(Service.CONNECTIVITY_SERVICE);

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
                                    moveActivity(LoadingActivity.this, MainActivity.class);
                                } else {
                                    moveActivity(LoadingActivity.this, AdminActivity.class);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        moveActivity(LoadingActivity.this, LoginActivity.class);
                    }
                } else {
                    new AlertDialog.Builder(LoadingActivity.this)
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
                Toast.makeText(LoadingActivity.this, "Error CONNECTIVITY SERVICE", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        handler = new Handler();

        handler.post(runnableLoading);

    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }
}