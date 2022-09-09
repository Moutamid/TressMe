package com.example.treesme.User.ui.chat;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treesme.Adapters.ChatSupportRoomAdapter;
import com.example.treesme.Adapters.OurTeamListAdapter;
import com.example.treesme.Model.Admin;
import com.example.treesme.Model.Chat;
import com.example.treesme.Model.Conversation;
import com.example.treesme.R;
import com.example.treesme.SharedPreferencesManager;
import com.example.treesme.databinding.FragmentChatBinding;
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
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ChatSupportFragment extends Fragment {

    private FragmentChatBinding binding;
    private RecyclerView teamList,recyclerView;
    private EmojiEditText msgTxt;
    private ImageView emojisImg,addImg;
    private int[] images = {R.drawable.profile,R.drawable.demo,R.drawable.demo};
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mChatReference,mConversationReference;
    private List<Chat> chatList = new ArrayList<>();
    private String type = "image";
    private StorageReference mStorage;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    private SharedPreferencesManager pref;
    Bitmap bitmap;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        teamList = root.findViewById(R.id.teams);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        teamList.setLayoutManager(manager);
        OurTeamListAdapter adapter = new OurTeamListAdapter(getActivity(),images);
        teamList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        pref = new SharedPreferencesManager(getActivity());
        getLocale();
        recyclerView = root.findViewById(R.id.chats_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        msgTxt = root.findViewById(R.id.message);
        emojisImg = root.findViewById(R.id.emojis);
        addImg = root.findViewById(R.id.addImg);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        final EmojiPopup popup = EmojiPopup.Builder
                .fromRootView(root.findViewById(R.id.rootView)).build(msgTxt);
        mChatReference = FirebaseDatabase.getInstance().getReference().child("chats");
        mConversationReference = FirebaseDatabase.getInstance().getReference().child("conversation");

        mStorage = FirebaseStorage.getInstance().getReference();
        msgTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    addImg.setImageResource(R.drawable.ic_baseline_send_24);
                    addImg.setBackgroundResource(R.drawable.send_button_background);
                    type = "text";
                }else{
                    addImg.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
                    addImg.setBackgroundResource(0);
                    type = "image";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("text")) {
                    if (!TextUtils.isEmpty(msgTxt.getText().toString())) {
                        long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                        sentChat("text", msgTxt.getText().toString(), timestamp);
                    }
                }else {
                    openFilePicker();
                }
            }
        });

        emojisImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
            }
        });

        getComposeChat();

        return root;
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
        getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());
        pref.storeString("lang",lng);
    }

    private void getComposeChat() {
        DatabaseReference adminReferenc = FirebaseDatabase.getInstance().getReference().child("Admin");
        adminReferenc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        //Admin model = ds.getValue(Admin.class);
                        //String adminId = model.getId();
                        mChatReference.child(user.getUid()).child(ds.getKey().toString()).orderByChild("timestamp")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            chatList.clear();
                                            chatList.add(new Chat("how can we help you? Do not hesitate to contact us","text"));
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                Chat chat = ds.getValue(Chat.class);
                                                chatList.add(chat);
                                            }

                                            ChatSupportRoomAdapter adapters = new ChatSupportRoomAdapter(getActivity(),
                                                    chatList);
                                            recyclerView.smoothScrollToPosition(chatList.size() - 1);
                                            recyclerView.setAdapter(adapters);
                                            adapters.notifyDataSetChanged();
                                        }else {

                                            ChatSupportRoomAdapter adapters = new ChatSupportRoomAdapter(getActivity(),
                                                    chatList);
                                            ///recyclerView.smoothScrollToPosition(chatList.size() - 1);
                                            recyclerView.setAdapter(adapters);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK &&
                data != null && data.getData() != null) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                uploadImg();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            Toast.makeText(getActivity(), "Please Choose a picture", Toast.LENGTH_LONG).show();
        }
    }

    private void sentChat(String type, String contoh, long timestamp) {
        DatabaseReference adminReferenc = FirebaseDatabase.getInstance().getReference().child("Admin");
        adminReferenc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Admin model = ds.getValue(Admin.class);
                        String adminId = model.getId();
                        Chat chatReciever = new Chat(type, contoh, user.getUid(), adminId, timestamp);

                        Conversation conversationSender = new Conversation(type, adminId, user.getUid(), contoh, timestamp);

                        DatabaseReference senderReference = mConversationReference.child(adminId).child(user.getUid());
                        senderReference.setValue(conversationSender);

                        DatabaseReference senderReference1 = mChatReference.child(user.getUid()).child(adminId);
                        senderReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
                        DatabaseReference receiverReference1 = mChatReference.child(adminId).child(user.getUid());
                        receiverReference1.child(String.valueOf(timestamp)).setValue(chatReciever);
                        msgTxt.setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}