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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.oldstocktrade.Adapter.MessageAdapter;
import com.example.oldstocktrade.Model.Chat;
import com.example.oldstocktrade.Model.Conversation;
import com.example.oldstocktrade.Model.User;
import com.example.oldstocktrade.Notification.APIService;
import com.example.oldstocktrade.Notification.Client;
import com.example.oldstocktrade.Notification.Data;
import com.example.oldstocktrade.Notification.Response;
import com.example.oldstocktrade.Notification.Sender;
import com.example.oldstocktrade.Notification.Token;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class MessageActivity extends AppCompatActivity {
    private static final String MAPVIEW_BUNDLE_KEY = "BUNDLE_KEY";
    private CircleImageView profile_image;
    private TextView txt_username;
    private RecyclerView recyclerView;

    private FirebaseUser fuser;
    private DatabaseReference reference;
    private ImageButton btn_send;
    private ImageButton btn_img, btn_location;
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
    private User user, me;
    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private Geocoder geocoder;
    private  ImageButton btn_sell;
    ValueEventListener valueEventListener;
    MapView mapView = null;
    Boolean isSend,isReceiver;
    View bottomSheetView;
    Double lati,longi;
    Bundle savedInstanceState2;
    private String myavatarURL;

    APIService apiService;
    boolean notify = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        savedInstanceState2 = savedInstanceState;
        // Set up
        img_on = findViewById(R.id.img_on);
        img_off = findViewById(R.id.img_off);
        profile_image = findViewById(R.id.profile_image);
        txt_username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        btn_img = findViewById(R.id.btn_img);
        btn_location = findViewById(R.id.btn_location);
        //btn_recv_location = findViewById(R.id.btn_location1);
        btn_call = findViewById(R.id.btn_call);
        btn_sell = findViewById(R.id.btn_sell);
        txt_msg = findViewById(R.id.txt_msg);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

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
        updateToken(FirebaseInstanceId.getInstance().getToken());
        FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                myavatarURL = user.getImageURL();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                notify = true;
                String msg = txt_msg.getText().toString();
                if (!msg.equals("")) {
                    if (conversation_reference == null) {
                        createConversation(fuser.getUid(),userid);
                    }
                    DatabaseReference ref_chats = conversation_reference.child("Chats").push();
                    sendMessage(fuser.getUid(), userid, msg,"text",ref_chats);
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
                setBottomSheetDialog(savedInstanceState, true,0.0,0.0,false);


            }
        });
//        btn_recv_location.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Lat of long of sender
//                setBottomSheetDialog(savedInstanceState, false, 10.766724451581517, 106.69376915409575);
//
//
//            }
//        });
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
        btn_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, SellProductActivity.class).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                intent.putExtra("userid",user.getId());
                startActivity(intent);
            }
        });
    }
    BottomSheetDialog bottomSheetDialog;
    private void setBottomSheetDialog(Bundle savedInstanceState, boolean isSend, Double lati, Double longi,boolean isReceiver) {
        bottomSheetDialog = new BottomSheetDialog(MessageActivity.this, R.style.BottomSheetDialogdTheme);

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
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
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
            getCurrentLocation(isSend, bottomSheetView,lati,longi,isReceiver);



        } else {
            this.isSend =isSend;this.bottomSheetView = bottomSheetView; this.lati = lati; this.longi = longi; this.isReceiver = isReceiver;//save data to serve in onRequestPermissionResult
            ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }



    }

    private void getCurrentLocation(boolean isSend, View bottomSheetView, Double lati, Double longi, boolean isReceiver) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("onmapready", "onMapReady: oko");
                mMap = googleMap;
                //mMap.getUiSettings().setZoomControlsEnabled(true);
                if (ActivityCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                mapView.onResume();
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
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(now,15));
                                //mMap.addMarker(new MarkerOptions().position(now).title("You're here"));

                                if (addresses != null && addresses.size() != 0) {
                                    Address address = addresses.get(0);
                                    ((TextView) bottomSheetView.findViewById(R.id.margin)).setText(Double.toString(now.latitude) + "#" + Double.toString(now.longitude)+"#"+address.getAddressLine(0));


                                }
                                bottomSheetView.findViewById(R.id.btnShareLocation).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Send to receiver
                                        sendLocation(fuser.getUid(),userid, now.latitude, now.longitude);
                                        bottomSheetDialog.dismiss();
                                    }
                                });


                            }

                        }
                        else{
                            if (location != null) {
//                                    lati = 10.801315806188832;
//                                    longi = 106.61737850991582;
                                String username = txt_username.getText().toString();
                                LatLng sender = new LatLng(lati, longi);

                                //edit marker: avatar

                                String avatarMarker = isReceiver?imageURL:myavatarURL;
                                Glide.with(MessageActivity.this)
                                        .asBitmap()
                                        .load(avatarMarker.equals("" + "default")?R.mipmap.ic_launcher_round:avatarMarker)
                                        .apply(new RequestOptions().override(150, 150))
                                        .circleCrop()
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(sender)
                                                        .title(username)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                                );

                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }
                                        });




                                //mMap.addMarker(new MarkerOptions().position(sender).title("Sender"));
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
                                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?saddr="+Double.toString(location.getLatitude())+","+Double.toString(location.getLongitude())+"&daddr="+Double.toString(sender.latitude)+","+Double.toString(sender.longitude)));
                                        startActivity(intent);
                                    }
                                });


                            }

                        }


                    }
                });


            }});
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(isSend, bottomSheetView,lati,longi,isReceiver);
            }
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 438 && resultCode ==RESULT_OK && data != null && data.getData() != null){
            sendImage(data);

        }
    }
    void sendImage(Intent data){
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

                    sendMessage(fuser.getUid(),userid,downloadUrl.toString(),"image",messagePushRef);
//                    HashMap<String, Object> hashMap = new HashMap<>();
//                    hashMap.put("id", messagePushID);
//                    hashMap.put("sender",fuser.getUid());
//                    hashMap.put("receiver",userid);
//                    hashMap.put("message",downloadUrl.toString());
//                    hashMap.put("type","image");
//                    hashMap.put("time",System.currentTimeMillis());
//                    messagePushRef.setValue(hashMap);
//
//                    reference.child("Conversations").child(conversation_reference.getKey()).child("recent_msg").setValue(hashMap);
//                    ref.child(conversation_reference.getKey()).child("recent_msg").setValue(hashMap);

                    dialog.dismiss();
                }
                else dialog.dismiss();
            }
        });
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
    public void showLocation(Double latitude, Double longitude, boolean isReceiver){
        setBottomSheetDialog(savedInstanceState2, false, latitude, longitude,isReceiver);
    }
    void sendLocation(String sender, String receiver, Double latitude, Double longitude) {
        if (conversation_reference == null) {
            createConversation(sender,receiver);
        }
        DatabaseReference ref_chats = conversation_reference.child("Chats").push();
        String msg = Double.toString(latitude) +","+Double.toString(longitude);
        sendMessage(sender,receiver,msg,"location",ref_chats);
    }

    void sendMessage(String sender,String receiver, String msg, String type, DatabaseReference ref_chats){
        Chat chat = new Chat(sender,receiver,msg,type,ref_chats.getKey(),System.currentTimeMillis(),false);
        ref_chats.setValue(chat);

        reference.child("Conversations").child(conversation_reference.getKey()).child("recent_msg").setValue(chat);
        ref.child(conversation_reference.getKey()).child("recent_msg").setValue(chat);
        switch (type){
            case "text":
                break;
            case "location":
                msg = "Sent you a location.";
                break;
            case "image":
                msg = "Sent you an image.";
                break;

        }
        createNotification(msg, receiver);
    }
    private void createNotification(String message, String receiver){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                    sendNotification(receiver, user.getUsername(), message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, String username, String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), username + ": " + message, "New Message", userid, R.drawable.ic_conversation);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    if (response.code() == 200){
                                        if (response.body().success != 1){
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    ValueEventListener loadMessageListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            mChats.clear();
            Chat chat = null;
            for(DataSnapshot data : snapshot.getChildren()){
                chat = data.getValue(Chat.class);
                if (chat.getSender().equals(userid))
                    data.getRef().child("seen").setValue(true);
                mChats.add(chat);
            }
            if (chat != null && chat.getSender().equals(userid))
                ref.child(conversation_reference.getKey()).child("recent_msg").setValue(chat);
            messageAdapter = new MessageAdapter(MessageActivity.this, mChats, imageURL, conversation_reference);
            recyclerView.setAdapter(messageAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    DatabaseReference loadMsg_ref = null;
    void loadMessage() {
        if (conversation_reference != null){
            loadMsg_ref = conversation_reference.child("Chats");
            mChats = new ArrayList<>();

            loadMsg_ref.addValueEventListener(loadMessageListener);
        }

    }
    public void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(fuser.getUid()).setValue(mToken);
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
        if (loadMsg_ref != null)
            loadMsg_ref.addValueEventListener(loadMessageListener);
    }

    @Override
    protected void onPause() {
        if (mapView!=null)
            mapView.onPause();
        super.onPause();
        reference.removeEventListener(valueEventListener);
        if (loadMsg_ref != null)
            loadMsg_ref.removeEventListener(loadMessageListener);
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
        if(mapView!= null)
            mapView.onSaveInstanceState(mapViewBundle);
    }

}