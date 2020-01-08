package com.example.abdulmanan.nayapakistan1;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth myAuth;
    private DatabaseReference user_refrence, PostsRef,agreeRef,disagreeRef;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageButton AddNewPostButton;
    boolean AgreeChecker,disagreeChecker;


    private String currentUserID;
    int countLike;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (myAuth==null)
        {
            myAuth=FirebaseAuth.getInstance();
        }
        if (myAuth!=null)
        {
            currentUserID = myAuth.getCurrentUser().getUid();
        }

        user_refrence= FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        agreeRef = FirebaseDatabase.getInstance().getReference().child("Agree");
        disagreeRef = FirebaseDatabase.getInstance().getReference().child("DisAgree");


        mToolbar= (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout= (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView= (NavigationView) findViewById(R.id.navigation_view);
        View navView= navigationView.inflateHeaderView(R.layout.navigation_header);

        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);

        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);


        postList = (RecyclerView) findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);


        user_refrence.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Glide.with(getApplicationContext()).load(image).into(NavProfileImage);

                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToPostActivity();
            }
        });

        DisplayAllUsersPosts();



    }

    private void DisplayAllUsersPosts()
    {
        FirebaseRecyclerOptions<Posts> options=new FirebaseRecyclerOptions.Builder<Posts>().setQuery(PostsRef,Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {

                    @NonNull
                    @Override
                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout,parent,false);
                        PostsViewHolder viewHolder=new PostsViewHolder(view);
                        return viewHolder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Posts model) {

                        holder.setFullname(model.getFullname());
                        holder.setTime(model.getTime());
                        holder.setDate(model.getDate());
                        holder.setDescription(model.getDescription());
                        holder.setProfileimage(getApplicationContext(), model.getProfileimage());
                        holder.setPostimage(getApplicationContext(),model.getPostimage());

                        final String PostKey=getRef(position).getKey();

                        holder.setAgreeBtnStatus(PostKey);
                        holder.setDisAgreeBtnStatus(PostKey);

                        holder.disAgreeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                disagreeChecker=true;
                                disagreeRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (disagreeChecker==true)
                                        {
                                            if (dataSnapshot.child(PostKey).hasChild(currentUserID))
                                            {
                                                disagreeRef.child(PostKey).child(currentUserID).removeValue();
                                                disagreeChecker=false;
                                            }
                                            else
                                            {
                                                disagreeRef.child(PostKey).child(currentUserID).setValue(true);
                                                disagreeChecker=false;
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        });



                        holder.AgreeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AgreeChecker=true;
                                agreeRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (AgreeChecker==true)
                                        {
                                            if (dataSnapshot.child(PostKey).hasChild(currentUserID))
                                            {
                                                agreeRef.child(PostKey).child(currentUserID).removeValue();
                                                AgreeChecker=false;
                                            }
                                            else
                                            {
                                                agreeRef.child(PostKey).child(currentUserID).setValue(true);
                                                AgreeChecker=false;
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        });

                    }

                };
        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        ImageButton AgreeButton,disAgreeButton,cmntbtn;
        TextView No_Agree,No_disagree;
        int countagree;
        String currentId;
        DatabaseReference AgreeR;

        DatabaseReference disAgreeR;
        int countdisagree;

        public PostsViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;

            AgreeButton=(ImageButton) mView.findViewById(R.id.agreebtn);
            disAgreeButton=(ImageButton) mView.findViewById(R.id.disagreebtn);
            cmntbtn=(ImageButton) mView.findViewById(R.id.cmmnt);

            No_Agree=(TextView) mView.findViewById(R.id.no_agree);
            No_disagree=(TextView) mView.findViewById(R.id.no_disagree);

            AgreeR = FirebaseDatabase.getInstance().getReference().child("Agree");
            disAgreeR = FirebaseDatabase.getInstance().getReference().child("DisAgree");
            currentId=FirebaseAuth.getInstance().getCurrentUser().getUid();



        }

        public void setAgreeBtnStatus(final String PostKey)
        {
            AgreeR.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).hasChild(currentId))
                    {
                        countagree=(int) dataSnapshot.child(PostKey).getChildrenCount();
                        AgreeButton.setImageResource(R.drawable.fillagree);
                        No_Agree.setText(Integer.toString(countagree)+(" Agree"));
                    }
                    else
                    {
                        countagree=(int) dataSnapshot.child(PostKey).getChildrenCount();
                        AgreeButton.setImageResource(R.drawable.agree);
                        No_Agree.setText(Integer.toString(countagree)+(" Agree"));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void setDisAgreeBtnStatus(final String PostKey)
        {
            disAgreeR.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).hasChild(currentId))
                    {
                        countdisagree=(int) dataSnapshot.child(PostKey).getChildrenCount();
                        disAgreeButton.setImageResource(R.drawable.filldisagree);
                        No_disagree.setText(Integer.toString(countdisagree)+(" Disagree"));
                    }
                    else
                    {
                        countagree=(int) dataSnapshot.child(PostKey).getChildrenCount();
                        disAgreeButton.setImageResource(R.drawable.disagree);
                        No_disagree.setText(Integer.toString(countdisagree)+(" Disagree"));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setFullname(String fullname)
        {
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileimage(Context ctx, String profileimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Glide.with(ctx).load(profileimage).into(image);
        }

        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("    " + time);
        }

        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("    " + date);
        }

        public void setDescription(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostimage(Context ctx1, String postimage)
        {
            ImageView PostImage11 = (ImageView) mView.findViewById(R.id.post_image);
            Glide.with(ctx1).load(postimage).into(PostImage11);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= myAuth.getCurrentUser();
        if (currentUser==null)
        {
            SendUserToLogin();
        }
        else
        {
            CheckUserExistence();
        }

    }

    private void CheckUserExistence() {
        final String current_user_id=myAuth.getCurrentUser().getUid();
        user_refrence.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild("fullname"))
                {
                    SendUserToSetup();
                }
                if (!dataSnapshot.hasChild("halkaNo"))
                {
                    SendUserToSelection();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSelection() {
        Intent setupIntent = new Intent(MainActivity.this, Selection.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }

    private void SendUserToSetup() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }
    private void SendUserToPostActivity()
    {
        Intent addNewPostIntent = new Intent(MainActivity.this, PostActivity.class);
        startActivity(addNewPostIntent);
    }

    private void SendUserToLogin() {
        Intent login_intent=new Intent(MainActivity.this,LoginActivity.class);
        login_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login_intent);
        finish();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_1:
                SendUserToPostActivity();
                break;
            case R.id.nav_2:
                Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_3:
                Toast.makeText(this,"Opinion Poll",Toast.LENGTH_SHORT).show();
                SendUserToOpinion();
                break;
            case R.id.nav_4:
                Toast.makeText(this,"Followers",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_5:
                Toast.makeText(this,"Work in Progress",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_6:
                Toast.makeText(this,"Work Done",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_7:
                Toast.makeText(this,"Setting",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_8:
                myAuth.signOut();
                SendUserToLogin();
                Toast.makeText(this,"Logout",Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void SendUserToOpinion() {
        Intent poll_intent=new Intent(MainActivity.this,OpinionPollActivity.class);
        startActivity(poll_intent);


    }

}
