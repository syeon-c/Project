package com.example.project_1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import model.BuyModel;
import model.ImageModel;
import model.UserModel;

public class BuyPopUpActivity extends Activity {

    TextView titleView;
    TextView addressView;
    TextView descriptionView;
    TextView hostView;
    TextView targetNumView;
    TextView currentNumView;
    ImageView imageView;
    LinearLayout hostLayout;
    String title;
    String address;
    String image;
    String description;
    String id;
    String hostName;
    String targetNum;
    String currentNum;
    String uid;
    String userUid;
    String imageUrl;
    private boolean addressFlag;
    Handler handler = new Handler();
    private FirebaseDatabase database;
    private DatabaseReference ref_user = FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference ref_buy = FirebaseDatabase.getInstance().getReference().child("Buy");
    private DatabaseReference ref_buyImage = FirebaseDatabase.getInstance().getReference().child("BuyImages");
    private DatabaseReference ref_chatlist = FirebaseDatabase.getInstance().getReference().child("Chatlist");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_buy_popup);

        titleView = (TextView) findViewById(R.id.title);
        addressView = (TextView) findViewById(R.id.userAddress);
        descriptionView = (TextView) findViewById(R.id.description);
        imageView = (ImageView) findViewById(R.id.imageView);
        hostView = (TextView) findViewById(R.id.hostName);
        targetNumView = (TextView) findViewById(R.id.targetNOP);
        currentNumView = (TextView) findViewById(R.id.currentNOP);
        hostLayout = (LinearLayout) findViewById(R.id.number_of_people);


        //데이터 가져오기
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        //데이터 설정하기
        ref_buyImage.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot imageSnapshot) {
                ref_buy.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ImageModel imageModel = imageSnapshot.getValue(ImageModel.class);
                        BuyModel buyModel = dataSnapshot.getValue(BuyModel.class);

                        imageUrl = imageModel.getUrl();
                        uid = buyModel.host;
                        title = buyModel.title;
                        description = buyModel.description;
                        targetNum = Integer.toString(buyModel.targetNOP);
                        currentNum = Integer.toString(buyModel.currentNOP);

                        ref_user.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                UserModel userModel = userSnapshot.getValue(UserModel.class);

                                hostName = userModel.name;

                                Thread imageThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!(imageUrl).equals("default")) {
                                                    Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
                                                }
                                                titleView.setText(title);
                                                descriptionView.setText(description);
                                                targetNumView.setText(targetNum);
                                                currentNumView.setText(currentNum);
                                                hostView.setText(hostName);
                                            }
                                        });
                                    }
                                });
                                imageThread.start();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Button openChat;
        openChat = (Button) findViewById(R.id.chat_button);
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 채팅방 버튼 클릭시 이동
        openChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref_user.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        addressFlag = userModel.addressFlag;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                ref_chatlist.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String gueststat = dataSnapshot.child(id).child("guest").getValue(String.class);
                        String hoststat = dataSnapshot.child(id).child("host").getValue(String.class);
                        if (addressFlag == false) {
                            Toast.makeText(getApplicationContext(),"동네 인증이 필요합니다", Toast.LENGTH_SHORT).show();
                        } else {
                            if (hoststat.equals(userUid)) {
                                //host가 입장한 경우

                                Intent intent = new Intent(BuyPopUpActivity.this, MessageActivity.class);
                                intent.putExtra("userid", uid);
                                intent.putExtra("chatid", id);

                                startActivity(intent);
                            } else if (gueststat.equals("null")) {
                                //게스트가 null인 경우 (초기)
                                ref_chatlist.child(id).child("guest").setValue(userUid);

                                Intent intent = new Intent(BuyPopUpActivity.this, MessageActivity.class);
                                intent.putExtra("userid", uid);
                                intent.putExtra("chatid", id);
                                startActivity(intent);

                            } else if (gueststat.equals(userUid)) {

                                Intent intent = new Intent(BuyPopUpActivity.this, MessageActivity.class);
                                intent.putExtra("userid", uid);
                                intent.putExtra("chatid", id);
                                startActivity(intent);

                            } else {
                                Toast.makeText(BuyPopUpActivity.this, hoststat + " " + gueststat + "   인원이 찬 대화방 입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                /*Intent intent = new Intent(BuyPopUpActivity.this, MessageActivity.class);
                intent.putExtra("userid", uid);
                intent.putExtra("chatid", id);
                startActivity(intent);*/

            }
        });

    }
}
    //바깥 레이어 클릭시 안닫히게
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//            return false;
//        }
//        return true;
//    }

