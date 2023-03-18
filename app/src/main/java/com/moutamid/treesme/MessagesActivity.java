package com.moutamid.treesme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moutamid.treesme.Adapters.ChatRoomAdapter;
import com.moutamid.treesme.Hairdresser.MainDashboard;
import com.moutamid.treesme.Model.Chat;
import com.moutamid.treesme.Model.Conversation;
import com.moutamid.treesme.Model.User;
import com.moutamid.treesme.User.MainScreenDashboard;
import com.moutamid.treesme.User.ReservationScreen;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView profileImg,sendImg,fileImg,backImg;
    private TextView usernameTxt,reserveTxt;
    private EditText msgtxt;
    private String message="";
    public static final String EXTRAS_USER = "user";
    private DatabaseReference mChatReference,mConversationReference,mUserReference;
    private StorageReference mStorage;
    private SharedPreferencesManager pref;
    private ChatRoomAdapter adapters;
    public String id,userUid;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    Bitmap bitmap;
    public static String idFromContact = null;
    FirebaseAuth mAuth;
    FirebaseUser user;
    User mUser;
    private String type = "";
    private String default_message = "";
    private List<Chat> chatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        recyclerView = findViewById(R.id.recyclerView);
        profileImg = findViewById(R.id.profile);
        sendImg = findViewById(R.id.send);
        fileImg = findViewById(R.id.file);
        backImg = findViewById(R.id.back);
        usernameTxt = findViewById(R.id.username);
        reserveTxt = findViewById(R.id.reserved);
        msgtxt = findViewById(R.id.message);
        idFromContact = getIntent().getStringExtra("userUid");
        mUser = getIntent().getParcelableExtra(EXTRAS_USER);
        default_message = getIntent().getStringExtra("message");
        msgtxt.setText(default_message);
        id = idFromContact;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userUid = user.getUid();

        if (mUser.getUserType().equals("user")){
            reserveTxt.setVisibility(View.GONE);
        }else{
            reserveTxt.setVisibility(View.VISIBLE);
        }

        pref = new SharedPreferencesManager(MessagesActivity.this);
        getLocale();
        LinearLayoutManager manager = new LinearLayoutManager(MessagesActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        mChatReference = FirebaseDatabase.getInstance().getReference().child("chats");
        mConversationReference = FirebaseDatabase.getInstance().getReference().child("conversation");
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference();
        initializeToolbar();
        getChatData();
        //if (!default_message.equals("")){

        //}
        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contoh = msgtxt.getText().toString();
                if (!TextUtils.isEmpty(contoh)) {
                    long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                    sentChat("text",contoh,timestamp);
                }
            }
        });
        fileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        reserveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessagesActivity.this, ReservationScreen.class);
                intent.putExtra(ReservationScreen.EXTRAS_USER, mUser);
                startActivity(intent);
            }
        });

    }

    private void getLocale(){

        String lang = pref.retrieveString("lang","");
        setLocale(lang);
    }

    private void setLocale(String lng) {

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        pref.storeString("lang",lng);
    }
    private void sentChat(String type, String contoh, long timestamp) {
        Chat chatReciever = new Chat(type, contoh, user.getUid(), idFromContact, timestamp);

        Conversation conversationSender = new Conversation(type, user.getUid(), idFromContact, contoh, timestamp);
        Conversation conversationReceiver = new Conversation(type, idFromContact, user.getUid(), contoh, timestamp);

        DatabaseReference senderReference = mConversationReference.child(user.getUid()).child(idFromContact);
        senderReference.setValue(conversationSender);
        DatabaseReference receiverReference = mConversationReference.child(idFromContact).child(user.getUid());
        receiverReference.setValue(conversationReceiver);

        DatabaseReference senderReference1 = mChatReference.child(user.getUid()).child(idFromContact);
        senderReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        DatabaseReference receiverReference1 = mChatReference.child(idFromContact).child(user.getUid());
        receiverReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
        msgtxt.setText("");
    }

    private void getChatData() {
        mChatReference.child(userUid).child(idFromContact).orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            chatList.clear();
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Chat chat = ds.getValue(Chat.class);
                                chatList.add(chat);
                            }

                            adapters = new ChatRoomAdapter(MessagesActivity.this, chatList);
                            recyclerView.smoothScrollToPosition(chatList.size() - 1);
                            recyclerView.setAdapter(adapters);
                            adapters.notifyDataSetChanged();
                        }
                    }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

    private void initializeToolbar() {
        usernameTxt.setText(mUser.getName());
        if (mUser.getImageUrl().equals("")){

            Picasso.with(MessagesActivity.this)
                    .load(R.drawable.profile)
                    .into(profileImg);
        }else {
            Picasso.with(MessagesActivity.this)
                    .load(mUser.getImageUrl())
                    .placeholder(R.drawable.profile)
                    .into(profileImg);
        }
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser.getUserType().equals("user")){
                    startActivity(new Intent(MessagesActivity.this, MainDashboard.class));
                }else {
                    startActivity(new Intent(MessagesActivity.this, MainScreenDashboard.class));
                }
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                uploadImg();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String getfiletype(Uri videouri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }

    private void uploadImg(){
        if (uri != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            byte[] thumb_byte_data = byteArrayOutputStream.toByteArray();

            final StorageReference reference = mStorage.child("Messages").child("images")
                    .child(user.getUid()).child(System.currentTimeMillis() + ".jpg");
            final UploadTask uploadTask = reference.putBytes(thumb_byte_data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                                sentChat("image",downloadUri.toString(),timestamp);
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            //    addChatList(senderId,rId);

        } else {
            Toast.makeText(MessagesActivity.this, "Please Choose a picture", Toast.LENGTH_LONG).show();
        }
    }

}