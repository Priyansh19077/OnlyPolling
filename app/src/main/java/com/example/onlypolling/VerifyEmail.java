package com.example.onlypolling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmail extends AppCompatActivity {

    String email;
    String name;
    TextView title;
    Button back;
    Button verify;
    FirebaseAuth auth;
    ProgressBar loading;
    FirebaseUser user;
    TextView textDescription;
    String password;
    boolean counter=false;
    int counter1=0;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_verify_email);
        name=getIntent().getStringExtra("name");
        email=getIntent().getStringExtra("email");
        password=getIntent().getStringExtra("password");
        textDescription=findViewById(R.id.textDescription);
        title=findViewById(R.id.textName);
        title.setText("Hi "+name+"...");
        loading=findViewById(R.id.progressBar);
        loading.setVisibility(View.INVISIBLE);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        back=findViewById(R.id.back);
        verify=findViewById(R.id.verify);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.getCurrentUser().delete();
                auth.signOut();
                Intent in=new Intent(VerifyEmail.this,Register.class);
                in.putExtra("email",email);
                in.putExtra("password",password);
                in.putExtra("name",name);
                startActivity(in);
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter1==5)
                {
                    auth.signOut();
                    Intent in = new Intent(VerifyEmail.this,MainActivity.class);
                    startActivity(in);
                }
                else {
                    counter = true;
                    verify.setEnabled(false);
                    verify.setTextColor((int) 0x43150202);
                    loading.setVisibility(View.VISIBLE);
                    setTimer(3);
            }
                return;
            }
        });
    }


    public void sendEmail() {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            loading.setVisibility(View.INVISIBLE);
                            verify.setEnabled(true);
                            verify.setText("Log In");
                            verify.setTextColor((int)0x94000000);
                            counter1=5;
                            textDescription.setText("We have sent you an email containing the verification link. You may click on it to get yourself verified. You can now go to the LogIn screen and LogIn with your credentials. \nNote: If you do not verify your email we will have to unfortunately delete this temporary account in near future\nHappy Polling...");
                            back.setVisibility(View.INVISIBLE);
                            back.setEnabled(false);
                            Log.i("VerificationEmailSent", "Email sent.");
                        }
                        else
                        {
                            loading.setVisibility(View.INVISIBLE);
                            counter=false;
                            verify.setEnabled(true);
                            verify.setTextColor((int)0x94000000);
                            Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return;
    }  //sending email is happening here. Look into this function for any erros in sending of email


    public void setTimer(int time) {
        CountDownTimer cc = new CountDownTimer(100*time +10,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                sendEmail();
                return;
            }
        }.start();
    } //emailsending function is called here can be used to set some delay in the code
}