package com.example.oldstocktrade;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Adapter.ImageSlider;
import com.example.oldstocktrade.Adapter.ListViewCommentAdapter;
import com.example.oldstocktrade.Model.Comment;
import com.example.oldstocktrade.Model.User;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;


public class ParticularPageActivity extends AppCompatActivity {
    private TextView descriptionPart, pricePart, dayPostPart, addressPart;
    private TextView nameUser, phoneUser;
    private DatabaseReference df;
    private ViewPager productImagePart;
    private Button btnReport, call, sendSMS, deletePart,btnDirection;
    private ImageView arrow;
    private Double longitudeS = 0.0,latitudeS = 0.0,longitudeD = 0.0,latitudeD = 0.0;
    String addressS,addressD;


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_particular_product);
            String receiveID= getIntent().getExtras().get("id").toString();
            String receiveUserID= getIntent().getExtras().get("userID").toString();
            int sizeImageURL= (int)(getIntent().getExtras().get("sizeImageURL"));
            longitudeS = (Double) (getIntent().getExtras().get("longitude"));
            latitudeS = (Double) (getIntent().getExtras().get("latitude"));
            addressS = "111";

            descriptionPart= findViewById(R.id.descriptionPart);
            pricePart= findViewById(R.id.pricePart);
            dayPostPart= findViewById(R.id.dayPostPart);
            addressPart= findViewById(R.id.addressPart);
            nameUser= findViewById(R.id.nameUser);
            phoneUser= findViewById(R.id.phoneUser);
            productImagePart= findViewById(R.id.productImagePart);
            btnReport= findViewById(R.id.btnReport);
            arrow= findViewById(R.id.btnBack);
            call= findViewById(R.id.call);
            sendSMS= findViewById(R.id.sendSMS);
            deletePart= findViewById(R.id.deletePart);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String myID= user.getUid();
            btnDirection = findViewById(R.id.btnDirection);
            btnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ParticularPageActivity.this,DirectionMap.class);
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
                    startActivity(new Intent(ParticularPageActivity.this,MainActivity.class));
                    finish();
                }
            });
            df= FirebaseDatabase.getInstance().getReference();
            df.child("Products").child(receiveID).addValueEventListener(new ValueEventListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if((snapshot.child("Seller").getValue().toString()).equals(myID)){
                        deletePart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                df.child("Products").child(receiveID).removeValue();
                                startActivity(new Intent(ParticularPageActivity.this,MainActivity.class));
                                finish();
                            }
                        });
                    }else{
                        deletePart.setVisibility(View.GONE);
                    }
                    String des= snapshot.child("Description").getValue().toString();
                    String price= snapshot.child("Price").getValue().toString();
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
                    descriptionPart.setText("Description: "+ des);
                    pricePart.setText("Price: "+price);
                    dayPostPart.setText("Day Post: "+dayPost);
                    addressPart.setText("Address: "+ add);
                    df.child("Report").child(receiveID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            int countUser= (int) snapshot2.getChildrenCount();
                            if(snapshot2.exists()){
                                for(int i=1;i<=countUser;i++){
                                    if((snapshot2.child(String.valueOf(i)).getValue().toString()).equals(myID)){
                                        btnReport.setEnabled(false);
                                        break;
                                    }
                                }

                            }
                            btnReport.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    df.child("Report").child(receiveID).child(String.valueOf(countUser+1)).setValue(myID);
                                    int report= Integer.parseInt(snapshot.child("Report").getValue().toString());
                                    report++;
                                    df.child("Products").child(receiveID).child("Report").setValue(report);
                                    btnReport.setEnabled(false);
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
                    nameUser.setText("Username: "+ nu);
                    phoneUser.setText("Phone user: "+ pu);

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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



//
//                    View view = LayoutInflater.from(this).inflate(R.layout.layout_bottomesheet_comment,
//                            this.findViewById(R.id.bottomshett_comment_container));
//
//                    view.findViewById(R.id.bottomsheet_commend_send).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    });
//                    ImageView tIm = view.findViewById(R.id.current_user_image);
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    df.child("Users").orderByChild(receiveUserID).equalTo(user.getUid())
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    for (DataSnapshot ds: snapshot.getChildren()){
//                                        tmp = ds.getValue(User.class);
//                                        Glide.with(tIm).load(tmp.getImageURL()).into(tIm);
//                                    }
//                                }
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                    Button sendComment = view.findViewById(R.id.bottomsheet_commend_send);
//                    sendComment.setOnClickListener(new View.OnClickListener() {
//                        @RequiresApi(api = Build.VERSION_CODES.M)
//                        @Override
//                        public void onClick(View v) {
//                            String mKey = df.child("Comments").push().getKey();
//                            Comment newCommet = new Comment(
//                                    ((TextView) view.findViewById(R.id.current_user_comment)).getText().toString(),
//                                    tmp.getId(),
//                                    receiveID,
//                                    System.currentTimeMillis(),
//                                    tmp.getId());
//                            df.child("Comments").child(mKey).setValue(newCommet);
//                            ((TextView) view.findViewById(R.id.current_user_comment)).setText("");
//                            hanldeComment(view,receiveID);
//                        }
//                    });
//                    hanldeComment(view,receiveID);
//            ViewGroup layout= (ViewGroup) findViewById(R.id.viewPart);
//            layout.addView(view,0);
//
////                    ???
//                }
//
//            public void hanldeComment(View bottomShettView,String proID){
//                df.child("Comments").orderByChild("id").equalTo(proID).
//                        addListenerForSingleValueEvent(new ValueEventListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.N)
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                ArrayList<Comment> arrComment = new ArrayList<>();
//                                for (DataSnapshot ds: snapshot.getChildren()){
//                                    Comment tmp = ds.getValue(Comment.class);
//                                    arrComment.add(tmp);
//                                }
//                                arrComment.sort(Comparator.comparing(Comment::getTimestamp));
//
//                                ListViewCommentAdapter listViewCommentAdapter = new ListViewCommentAdapter(arrComment,ParticularPageActivity.this);
//
//                                RecyclerView commentView = bottomShettView.findViewById(R.id.bottomsheet_coment);
//                                commentView.setAdapter(listViewCommentAdapter);
//                                commentView.setLayoutManager(new LinearLayoutManager(ParticularPageActivity.this));
//
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });



        }
}
