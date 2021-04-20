package com.example.oldstocktrade;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oldstocktrade.Adapter.MessageAdapter;
import com.example.oldstocktrade.Model.Chat;
import com.example.oldstocktrade.Model.Conversation;
import com.example.oldstocktrade.Model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private static final String MAPVIEW_BUNDLE_KEY = "BUNDLE_KEY";
    private CircleImageView profile_image;
    private TextView txt_username;
    private RecyclerView recyclerView;

    private FirebaseUser fuser;
    private DatabaseReference reference;
    private ImageButton btn_send;
    private ImageButton btn_img, btn_location, btn_recv_location;
    private ImageButton btn_call;
    private EditText txt_msg;
    private String conversation_id = null;
    private DatabaseReference conversation_reference = null;
    private MessageAdapter messageAdapter;
    private List<Chat> mChats;
    private String imageURL;
    private ImageView img_on;
    private ImageView img_off;
    private StorageTask uploadTask;
    private Uri fileUri;
    private String userid = "";
    private ProgressDialog dialog;
    private User user;
    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private Geocoder geocoder;
    ValueEventListener valueEventListener;
    MapView mapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        // Set up
        img_on = findViewById(R.id.img_on);
        img_off = findViewById(R.id.img_off);
        profile_image = findViewById(R.id.profile_image);
        txt_username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        btn_img = findViewById(R.id.btn_img);
        btn_location = findViewById(R.id.btn_location);
        btn_recv_location = findViewById(R.id.btn_location1);
        btn_call = findViewById(R.id.btn_call);
        txt_msg = findViewById(R.id.txt_msg);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Create toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reference.removeEventListener(valueEventListener);
                finish();
                //startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });


        // Load receiver's profile image
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                txt_username.setText(user.getUsername().toString());
                imageURL = user.getImageURL();
                if (imageURL.equals("" +
                        "default"))
                    profile_image.setImageResource(R.mipmap.ic_launcher_round);
                else Glide.with(MessageActivity.this).load(imageURL).into(profile_image);
                if (user.getStatus().equals("online")) {
                    img_on.setVisibility(View.VISIBLE);
                    img_off.setVisibility(View.GONE);
                } else {
                    img_off.setVisibility(View.VISIBLE);
                    img_on.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //reference.addValueEventListener(valueEventListener);

        // Find conversation between 2 users
        findConversationId(fuser.getUid(), userid);

        // Send message
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = txt_msg.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), userid, msg);
                    txt_msg.setText("");
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        //share your location
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomSheetDialog(savedInstanceState, true);


            }
        });
        btn_recv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomSheetDialog(savedInstanceState, false);


            }
        });
        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i.createChooser(i,"Select Image"),438);
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+user.getPhone()));
                startActivity(i);
            }
        });
        txt_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, SellerFeedActivity.class).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                intent.putExtra("userid",user.getId());
                startActivity(intent);
            }
        });

    }

    private void setBottomSheetDialog(Bundle savedInstanceState, boolean isSend) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MessageActivity.this, R.style.BottomSheetDialogdTheme);
        View bottomSheetView;
        if(isSend){
            bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.bottomsheet_sharelocation,
                    (LinearLayout) findViewById(R.id.bottomsheetContainer));
        }
        else
            bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.bottomsheet_receivelocation,
                    (LinearLayout) findViewById(R.id.bottomsheetContainer));
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
        Log.d("btnLocation", "onClick: click on btnLocation");
        mapView = bottomSheetView.findViewById(R.id.map);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
        geocoder = new Geocoder(getApplicationContext());
        client = LocationServices.getFusedLocationProviderClient(MessageActivity.this);
        if (ActivityCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Log.d("onmapready", "onMapReady: oko");
                    mMap = googleMap;
                    //mMap.getUiSettings().setZoomControlsEnabled(true);
                    if (ActivityCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(isSend){
                                if (location != null) {
                                    ArrayList<Address> addresses = null;
                                    try {
                                        addresses = (ArrayList<Address>) geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    LatLng now = new LatLng(location.getLatitude(),location.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(now,20));
                                    //mMap.addMarker(new MarkerOptions().position(now).title("You're here"));
                                    mapView.onResume();
                                    if (addresses.size() != 0) {
                                        Address address = addresses.get(0);
                                        ((TextView) bottomSheetView.findViewById(R.id.margin)).setText(Double.toString(now.latitude) + "-" + Double.toString(now.longitude)+"-"+address.getAddressLine(0));


                                    }
                                    bottomSheetView.findViewById(R.id.btnShareLocation).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //gui longitude, latitude, address
                                        }
                                    });


                                }

                            }
                            else{
                                if (location != null) {

                                    LatLng sender = new LatLng(10.801315806188832, 106.61737850991582);
                                    mMap.addMarker(new MarkerOptions().position(sender).title("Sender"));
                                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sender,20));
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(sender);
                                    builder.include(new LatLng(location.getLatitude(),location.getLongitude()));
                                    LatLngBounds bounds = builder.build();
                                    mMap.setPadding(100, 100, 100, 200);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                                    mMap.setPadding(0,0,0,0);
                                    mapView.onResume();
                                    bottomSheetView.findViewById(R.id.btnGetLocation).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                    Uri.parse("http://maps.google.com/maps?saddr="+Double.toString(location.getLatitude())+","+Double.toString(location.getLatitude())+"&daddr="+Double.toString(sender.latitude)+","+Double.toString(sender.longitude)));
                                            startActivity(intent);
                                        }
                                    });


                                    }

                                }


                        }
                    });


                }});


        } else {
            ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 438 && resultCode ==RESULT_OK && data != null && data.getData() != null){
            dialog = new ProgressDialog(this);
            dialog.setMessage("Sending image...");
            dialog.show();
            fileUri = data.getData();
            StorageReference reference_storage = FirebaseStorage.getInstance().getReference().child("Images");
            if (conversation_reference == null) {
                createConversation(fuser.getUid(),userid);
            }
            DatabaseReference messagePushRef = conversation_reference.child("Chats").push();
            String messagePushID = messagePushRef.getKey();

            final StorageReference filePath = reference_storage.child(messagePushID+".jpg");
            uploadTask = filePath.putFile(fileUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        dialog.dismiss();
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl = task.getResult();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender",fuser.getUid());
                        hashMap.put("receiver",userid);
                        hashMap.put("message",downloadUrl.toString());
                        hashMap.put("type","image");
                        hashMap.put("time",System.currentTimeMillis());
                        messagePushRef.setValue(hashMap);

                        reference.child("Conversations").child(conversation_reference.getKey()).child("recent_msg").setValue(hashMap);
                        ref.child(conversation_reference.getKey()).child("recent_msg").setValue(hashMap);
                        dialog.dismiss();
                    }
                    else dialog.dismiss();
                }
            });

        }
    }
    DatabaseReference ref;
    void findConversationId(String user1, String user2) {
        ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("Conversations");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    Conversation conversation = data.getValue(Conversation.class);
                    if ((conversation.getUser1().equals(user1) && conversation.getUser2().equals(user2)) || conversation.getUser1().equals(user2) && conversation.getUser2().equals(user1)){
                        conversation_id = data.getKey();
                        break;
                    }

                }
                if (conversation_id != null && conversation_reference == null){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Conversations").child(conversation_id);
                    conversation_reference = reference.getRef();
                    loadMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void sendMessage(String sender,String receiver, String msg){
        if (conversation_reference == null) {
            createConversation(sender,receiver);
        }
        DatabaseReference ref_chats = conversation_reference.child("Chats").push();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",msg);
        hashMap.put("type","text");
        hashMap.put("id",ref_chats.getKey());
        hashMap.put("time", System.currentTimeMillis());
        ref_chats.setValue(hashMap);

        reference.child("Conversations").child(conversation_reference.getKey()).child("recent_msg").setValue(hashMap);
        ref.child(conversation_reference.getKey()).child("recent_msg").setValue(hashMap);
    }
    void createConversation(String sender, String receiver){
        DatabaseReference ref_con = FirebaseDatabase.getInstance().getReference("Conversations");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("user1", sender);
        hashMap.put("user2", receiver);
        conversation_reference = ref_con.push();
        conversation_reference.setValue(hashMap);

        reference.child("Conversations").child(conversation_reference.getKey()).setValue(hashMap);
        ref.child(conversation_reference.getKey()).setValue(hashMap);
        loadMessage();
    }
    void loadMessage() {
        if (conversation_reference != null){
            DatabaseReference reference = conversation_reference.child("Chats");
            mChats = new ArrayList<>();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mChats.clear();
                    for(DataSnapshot data : snapshot.getChildren()){
                        Chat chat = data.getValue(Chat.class);
                        mChats.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChats, imageURL, conversation_reference);
                    recyclerView.setAdapter(messageAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView!=null)
            mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView!=null)
            mapView.onStop();
    }

    @Override
    protected void onResume() {

        super.onResume();
        reference.addValueEventListener(valueEventListener);
        if (mapView!=null)
            mapView.onResume();

    }

    @Override
    protected void onPause() {
        if (mapView!=null)
            mapView.onPause();
        super.onPause();
        reference.removeEventListener(valueEventListener);

    }

    @Override
    protected void onDestroy() {
        if (mapView!=null)
            mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {

        super.onLowMemory();
        if (mapView!=null)
            mapView.onLowMemory();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

}