package com.example.abdulmanan.nayapakistan1;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class SetupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private CircleImageView ProfileImage;
    private EditText UserName;
    private EditText FullName;
    private TextView Birthdate;
    final static int Gallery_Pick = 1;
    private ProgressDialog loadingBar;
    FirebaseStorage storage=FirebaseStorage.getInstance();
    StorageReference UserProfileImageRef=storage.getReferenceFromUrl("gs://nayapakistan1-a0fd9.appspot.com");
    Uri filepath;

    String currentUserID;

    public DatePickerDialog.OnDateSetListener mDataSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        UserName=(EditText) findViewById(R.id.info_user);
        FullName=(EditText) findViewById(R.id.info_name);
        Birthdate=(TextView) findViewById(R.id.info_bday);
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Saving Information...");

        ProfileImage = (CircleImageView) findViewById(R.id.profile_pic);
        Button next=(Button) findViewById(R.id.btn_next_info);


        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/+");
                galleryIntent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),Gallery_Pick);
            }
        });

        Birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH);
                int d = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SetupActivity.this,android.R.style.Theme_Holo_Dialog_MinWidth,mDataSetListener,y,m,d);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String DOB = dayOfMonth + "/" + month + "/" + year;
                Birthdate.setText(DOB);
            }
        };


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = UserName.getText().toString();
                String fullname = FullName.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Please write your username...", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(fullname)) {
                    Toast.makeText(getApplicationContext(), "Please write your full name...", Toast.LENGTH_SHORT).show();
                } else {


                    HashMap userMap = new HashMap();
                    userMap.put("username", username);
                    userMap.put("fullname", fullname);
                    userMap.put("birthdate",Birthdate.getText());
                    UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                if (filepath!=null)
                                {
                                    loadingBar.show();
                                    final StorageReference childRef = UserProfileImageRef.child(currentUserID + ".jpg");
                                    childRef.putFile(filepath);
                                    UploadTask uploadTask=childRef.putFile(filepath);

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            childRef.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        final String downloadUrl = task.getResult().getDownloadUrl().toString();

                                                        UsersRef.child("profileimage").setValue(downloadUrl)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                    {
                                                                        if(task.isSuccessful())
                                                                        {
                                                                            SendUserToSelection();
                                                                            loadingBar.dismiss();
                                                                        }
                                                                        else
                                                                        {
                                                                            String message = task.getException().getMessage();
                                                                            Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                                            loadingBar.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });

                                            loadingBar.dismiss();
                                            Toast.makeText(getApplicationContext(),"Upload Successfully... ",Toast.LENGTH_SHORT);

                                            SendUserToSelection();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loadingBar.dismiss();
                                            Toast.makeText(getApplicationContext(),"Uploading Failed... ",Toast.LENGTH_SHORT);
                                        }
                                    });


                                }


                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            filepath=data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                ProfileImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }
    private void SendUserToSelection() {
        Intent login_intent=new Intent(SetupActivity.this,Selection.class);
        startActivity(login_intent);
        finish();

    }

}
