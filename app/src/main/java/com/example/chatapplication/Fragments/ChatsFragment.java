package com.example.chatapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapplication.Adapters.FriendAdapter;
import com.example.chatapplication.Adapters.UsersAdapter;
import com.example.chatapplication.ChatDetailActivity;
import com.example.chatapplication.Models.Friend;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.example.chatapplication.databinding.FragmentChatsBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    View FriendView;
    RecyclerView friendList;

    FragmentChatsBinding binding;
    //ArrayList<String> list = new ArrayList<>();
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference frdRef, usersRef;
    FirebaseAuth auth;
    String currentUserId;
    public ChatsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FriendView = inflater.inflate(R.layout.fragment_chats, container, false);
        friendList = (RecyclerView) FriendView.findViewById(R.id.chatRecyclerView);
        friendList.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        frdRef = FirebaseDatabase.getInstance().getReference().child("UsersFriend").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return FriendView;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(frdRef, Friend.class).build();

        FirebaseRecyclerAdapter<Friend, FriendViewHolder> adapter = new FirebaseRecyclerAdapter<Friend, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder holder, int position, @NonNull Friend model) {
                String usersIds = getRef(position).getKey();
                final Users[] users = {new Users()};
                usersRef.child(usersIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users[0] = snapshot.getValue(Users.class);
                        /*String profilePic = snapshot.child("profilepic").getValue().toString();
                        String username = snapshot.child("userName").getValue().toString();
*/
                        holder.userName.setText(users[0].getUserName());
                        Picasso.get().load(users[0].getProfilepic()).placeholder(R.drawable.avatar).into(holder.image);
                        FirebaseDatabase.getInstance().getReference().child("chats").child(currentUserId+users[0].getUserId()).orderByChild("timestamp")
                                .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChildren()){
                                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                                        holder.lastMessage.setText(snapshot1.child("message").getValue(String.class));
                                    }
                                }
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
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ChatDetailActivity.class);
                        intent.putExtra("userId", users[0].getUserId());
                        intent.putExtra("profilePic", users[0].getProfilepic());
                        intent.putExtra("userName", users[0].getUserName());
                        getContext().startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_show_user,parent,false);
                FriendViewHolder viewHolder = new FriendViewHolder(view);
                return viewHolder;
            }
        };
        friendList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView userName, lastMessage;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userNameList);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}









// Inflate the layout for this fragment
        /*binding = FragmentChatsBinding.inflate(inflater,container,false);

        database = FirebaseDatabase.getInstance();
        UsersAdapter adapter = new UsersAdapter(list,getContext());
        //FriendAdapter adapter = new FriendAdapter(list,getContext());

        binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);
        final Users[] user = new Users[1];
        database.getReference().child("UsersFriend").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    list.clear();
                    for(DataSnapshot frddataSnapshot : snapshot.getChildren()) {
                        System.out.println(frddataSnapshot.getKey());
                        database.getReference().child("Users").child(frddataSnapshot.getKey())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot infosnapshot) {
                                        Users users = infosnapshot.getValue(Users.class);
                                        users.setUserId(infosnapshot.getKey());
                                        list.add(users);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                        //list.add(user[0]);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*database.getReference().child("UsersFriend").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String frdId = dataSnapshot.getKey();
                    System.out.println(frdId);
                    list.add(frdId);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
/*
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    users.setUserId(dataSnapshot.getKey());
                    if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                    list.add(users);}
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();*/