package com.example.abdulmanan.nayapakistan1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



public class RegisterActivity extends AppCompatActivity {


    private EditText UserEmail,UserPassword,UserConfirmPassword;
    private Button CreateAccountBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog loadbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UserEmail=(EditText) findViewById(R.id.register_email);
        UserPassword=(EditText) findViewById(R.id.register_password);
        UserConfirmPassword=(EditText) findViewById(R.id.register_confirm_password);
        CreateAccountBtn=(Button) findViewById(R.id.register_create_account);
        mAuth=FirebaseAuth.getInstance();
        loadbar=new ProgressDialog(this);

        CreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });


    }


    private void CreateNewAccount() {
        String Email=UserEmail.getText().toString();
        String Password=UserPassword.getText().toString();
        String ConfirmPassword=UserConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(Email))
        {
            Toast.makeText(this,"Please write your email...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Password))
        {
            Toast.makeText(this,"Please write your Password...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(ConfirmPassword))
        {
            Toast.makeText(this,"Please confirm your password...",Toast.LENGTH_SHORT).show();
        }
        else if (!Password.equals(ConfirmPassword))
        {
            Toast.makeText(this,"Your password not match with your confirm password...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadbar.setTitle("Creating new Account");
            loadbar.setMessage("Please wait, while we are creating your new Account");
            loadbar.show();
            loadbar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(Email,Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this,"Successfully Register",Toast.LENGTH_SHORT).show();
                                SendUserToMain();
                                loadbar.dismiss();


                            }
                            else
                            {
                                String Message=task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this,"Error Occur: "+Message,Toast.LENGTH_SHORT).show();
                                loadbar.dismiss();
                            }
                        }
                    });
        }

    }
    private void SendUserToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this,SetupActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
