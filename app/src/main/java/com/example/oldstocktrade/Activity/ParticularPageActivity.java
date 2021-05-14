package com.example.oldstocktrade.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import com.example.oldstocktrade.Adapter.ImageSlider;
import com.example.oldstocktrade.R;
import com.example.oldstocktrade.Adapter.ListViewCommentAdapter;
import com.example.oldstocktrade.Adapter.ReportDialog;
import com.example.oldstocktrade.Model.Comment;
import com.example.oldstocktrade.Model.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ParticularPageActivity extends AppCompatActivity implements ReportDialog.DialogListener{
    private TextView descriptionPart, pricePart, dayPostPart, namePart;
    private TextView nameUser, phoneUser;
    private DatabaseReference df;
    private ViewPager productImagePart;
    private Button btnReport;
    private ImageView arrow, btnDirection;
    private Double longitudeS = 0.0,latitudeS = 0.0,longitudeD = 0.0,latitudeD = 0.0;
    String addressS,addressD;
    private RelativeLayout call, sendSMS, chat;
    boolean change= false;
    String pID;
    String uID;
    int userCount;
    int sizeImageURL;
    int countReport;


    @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_particular_product);
            String receiveID= getIntent().getExtras().get("id").toString();
            String receiveUserID= getIntent().getExtras().get("userID").toString();
            countReport= Integer.parseInt(getIntent().getExtras().get("sumReport").toString());
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String myID= user.getUid();
            sizeImageURL= (int)(getIntent().getExtras().get("sizeImageURL"));
            longitudeS = (Double) (getIntent().getExtras().get("longitude"));
            latitudeS = (Double) (getIntent().getExtras().get("latitude"));
            addressS = (String)getIntent().getExtras().get("address");

            namePart= findViewById(R.id.name);
            descriptionPart= findViewById(R.id.descriptionPart);
            pricePart= findViewById(R.id.pricePart);
            dayPostPart= findViewById(R.id.dayPostPart);
            nameUser= findViewById(R.id.nameUser);
            phoneUser= findViewById(R.id.phoneUser);
            productImagePart= findViewById(R.id.productImagePart);
            btnReport= findViewById(R.id.btnReport);
            arrow= findViewById(R.id.btnBack);
            call= findViewById(R.id.call);
            sendSMS= findViewById(R.id.sendSMS);
            btnDirection = findViewById(R.id.btnDirection);
            chat= findViewById(R.id.chat);
            btnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ParticularPageActivity.this, DirectionMap.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitudeS",latitudeS);
                    bundle.putDouble("longitudeS",longitudeS);
                    bundle.putDouble("latitudeD",latitudeD);
                    bundle.putDouble("longitudeD",longitudeD);
                    bundle.putString("addressS",addressS);
                    bundle.putString("addressD",addressD);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            df= FirebaseDatabase.getInstance().getReference();
            df.child("Products").child(receiveID).addValueEventListener(new ValueEventListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name= snapshot.child("Name").getValue().toString();
                    String des= snapshot.child("Description").getValue().toString();
                    String price= snapshot.child("Price").getValue().toString()+"đ";
                    longitudeD = Double.parseDouble(snapshot.child("Longitude").getValue().toString());
                    latitudeD = Double.parseDouble(snapshot.child("Latitude").getValue().toString());
                    addressD= snapshot.child("Address").getValue().toString();
                    Log.d("getFromDBLo", "onDataChange: "+longitudeD+" "+latitudeD);
                    String dayPost= DateFormat.format("dd-MM-yyyy", Long.parseLong(snapshot.child("Timestamp").getValue().toString())).toString();
                    String add= snapshot.child("Address").getValue().toString();

                    ArrayList<String> a= new ArrayList<String>();
                    for(int i=0;i<sizeImageURL;i++){
                        a.add(snapshot.child("ImageURL").child(String.valueOf(i)).getValue(String.class));
                    }

                    String[] arrImage = a.toArray(new String[0]);
                    ImageSlider imgSliderAdapter = new ImageSlider(ParticularPageActivity.this,arrImage);
                    productImagePart.setAdapter(imgSliderAdapter);

                    namePart.setText(name);
                    descriptionPart.setText(des);
                    pricePart.setText(price);
                    dayPostPart.setText(dayPost);
                    df.child("Report").child(receiveID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            int countUser= (int) snapshot2.getChildrenCount();
                            if(snapshot2.exists()){
                                for(int i=1;i<=countUser;i++){
                                    if((snapshot2.child(String.valueOf(i)).getValue().toString()).equals(myID)){
                                        btnReport.setBackgroundResource(R.drawable.button_report_clicked);
                                        btnReport.setEnabled(false);
                                        break;
                                    }
                                }
                            }
                                btnReport.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pID= receiveID;
                                        uID= myID;
                                        userCount= countUser;
                                        reportD();
//                                        df.child("Report").child(receiveID).child(String.valueOf(countUser+1)).setValue(myID);
//                                        int report= Integer.parseInt(snapshot.child("Report").getValue().toString());
//                                        report++;
//                                        df.child("Products").child(receiveID).child("Report").setValue(report);
//                                        btnReport.setBackgroundResource(R.drawable.button_report_clicked);
//                                        btnReport.setEnabled(false);
                                    }
                                });



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            df.child("Users").child(receiveUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String nu= snapshot.child("username").getValue().toString();
                    String pu= snapshot.child("phone").getValue().toString();
                    nameUser.setText(nu);
                    phoneUser.setText(pu);
                    if(pu.matches("")|| receiveUserID.equals(myID))
                    {
                        call.setEnabled(false);
                        sendSMS.setEnabled(false);
                    }
                    else{
                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent= new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+pu));
                                startActivity(intent);
                            }
                        });

                        sendSMS.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+pu));
                                startActivity(intent);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            if(receiveUserID.equals(myID)){
                chat.setEnabled(false);
            }else{
                chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(ParticularPageActivity.this, MessageActivity.class).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        intent.putExtra("userid", receiveUserID);
                        startActivity(intent);
                    }
                });
            }
        }

    public void reportD() {
        ReportDialog rd= new ReportDialog();
        rd.show(getSupportFragmentManager(), "Report Dialog");
    }

    @Override
    public void applyChange(boolean change2) {
        change= change2;
        if(change){
            df.child("Report").child(pID).child(String.valueOf(userCount+1)).setValue(uID);
            countReport++;
            df.child("Products").child(pID).child("Report").setValue(countReport);
            btnReport.setBackgroundResource(R.drawable.button_report_clicked);
            btnReport.setEnabled(false);
        }


    }
}