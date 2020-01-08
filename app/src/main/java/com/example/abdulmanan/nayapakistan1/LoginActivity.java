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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class LoginActivity extends AppCompatActivity {

    private Button LogIn;
    private EditText Email;
    private EditText Password;
    private TextView AccountLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadbar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        LogIn=(Button) findViewById(R.id.login_account);
        Email=(EditText) findViewById(R.id.login_email);
        Password=(EditText) findViewById(R.id.login_password);
        AccountLink=(TextView) findViewById(R.id.register_account_link);

        AccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegister();
            }
        });
        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogIn();
            }
        });

    }

    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        if (currentUser!=null)
        {
            SendUserToMainActivity();
        }

    }

    private void AllowUserToLogIn() {
        String Uemail=Email.getText().toString();
        String Upassword=Password.getText().toString();

        if (TextUtils.isEmpty(Uemail))
        {
            Toast.makeText(this,"Please write your email...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Upassword))
        {
            Toast.makeText(this,"Please write your Password...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadbar.setTitle("LogIn");
            loadbar.setMessage("Please wait, while we are allowing you to login into your Account...");
            loadbar.show();
            loadbar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(Uemail,Upassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {

                        SendUserToMainActivity();
                        loadbar.dismiss();
                    }
                    else
                    {
                        String Message=task.getException().getMessage();
                        Toast.makeText(LoginActivity.this,"Error Occur: "+Message,Toast.LENGTH_SHORT).show();
                        loadbar.dismiss();
                    }
                }
            });

        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegister() {
        Intent register_intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(register_intent);

    }
}
