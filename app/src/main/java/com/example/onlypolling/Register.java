package com.example.onlypolling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    Button backToLogin;
    Button register_finish;
    String name="";
    String email="";
    String password="";
    ProgressBar p1;
    private FirebaseAuth mAuth;
    boolean arr1[] = {false, false,false, false};
    String confirm_password="";
    EditText name1;
    EditText email1;
    EditText password1;
    EditText confirm_password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_register);
        name1=findViewById(R.id.edtNickname);
        email1=findViewById(R.id.edtEmailAddress);
        password1=findViewById(R.id.edtPassword1);
        confirm_password1=findViewById(R.id.edtPasswordconfirm);
        backToLogin =findViewById(R.id.login1);
        register_finish=findViewById(R.id.register);
        email=email1.getText().toString();
        name=name1.getText().toString();
        password=password1.getText().toString();
        confirm_password=confirm_password1.getText().toString();
        mAuth=FirebaseAuth.getInstance();
        p1=findViewById(R.id.progressBar2);
        p1.setVisibility(View.INVISIBLE);
        confirm_password=confirm_password1.getText().toString();
        name=getIntent().getStringExtra("name");
        email=getIntent().getStringExtra("email");
        password=getIntent().getStringExtra("password");
        if(name!=null)
            name1.setText(name);
        else
            name="";
        if(email!=null)
            email1.setText(email);
        else
            email="";
        if(password!=null)
        {
            password1.setText(password);
            confirm_password=password;
            confirm_password1.setText(password);
        }
        else
        {
            password="";
            confirm_password="";
        }
        check(name,0);
        check(email,1);
        check(password,2);
        check(confirm_password,3);
        run_a_check1();

        name1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                run_a_check1();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name=name1.getText().toString().trim();
                check(name, 0);
                run_a_check1();
            }

            @Override
            public void afterTextChanged(Editable s) {
                run_a_check1();
            }
        });

        email1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                run_a_check1();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email=email1.getText().toString().trim();
                check(email,1);
                run_a_check1();
        }

            @Override
            public void afterTextChanged(Editable s) {
                run_a_check1();
            }
        });

        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                run_a_check1();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password=password1.getText().toString();
                check(password,2);
                run_a_check1();
            }

            @Override
            public void afterTextChanged(Editable s) {
                run_a_check1();
            }
        });

        confirm_password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                run_a_check1();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirm_password=confirm_password1.getText().toString();
                check(confirm_password,3);
                run_a_check1();
            }

            @Override
            public void afterTextChanged(Editable s) {
                run_a_check1();
            }
        });

        register_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(run_a_check2()) {
                        name1.clearFocus();
                        email1.clearFocus();
                        password1.clearFocus();
                        confirm_password1.clearFocus();
                        p1.setVisibility(View.VISIBLE);
                        setTimer(3);
                    }
                    else
                        return;
                }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Register.this,MainActivity.class);
                startActivity(in);
            }
        });
    }


    public void run_a_check1() {
        if(arr1[0] && arr1[1] && arr1[2] && arr1[3])
        {
            register_finish.setTextColor((int)0xFFFFFFFF);
            register_finish.setEnabled(true);
        }
        else {
            register_finish.setTextColor((int)0x43150202);
            register_finish.setEnabled(false);
        }
    }  //final check on the input to decide to enable the button or not


    public void check(String a, int index) {
        if(a.length()!=0)
            arr1[index]=true;
        else
            arr1[index]=false;
    }  //check empty string in any input field


    public boolean run_a_check2() {
        int n=name.length();
        for(int i=0;i<n;i++) {
            if (name.charAt(i) >= 'a' && name.charAt(i) <= 'z')
                continue;
            if (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z')
                continue;
            name1.requestFocus();
            Toast.makeText(getApplicationContext(),"Name must contain only letters",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            email1.requestFocus();
            Toast.makeText(getApplicationContext(),"Email does not match the format",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length()<6)
        {
            password1.requestFocus();
            Toast.makeText(getApplicationContext(),"Password must contain >= 6 characters",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.equals(confirm_password))
            return true;
        else
        {
            confirm_password1.requestFocus();
            Toast.makeText(getApplicationContext(),"Passwords do not match",Toast.LENGTH_SHORT).show();
            return false;
        }
    }  //checking for absurd input the register form


    public void setTimer(int time) {
        CountDownTimer cc = new CountDownTimer(100*time +10,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                verify_final();
                return;
            }
        }.start();
    } //verify_final method called from here, can be used to put some delay in the code


    public void verify_final() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Success", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent in=new Intent(Register.this,VerifyEmail.class);
                            in.putExtra("email", email);
                            in.putExtra("password", password);
                            in.putExtra("name", name);
                            startActivity(in);
                            p1.setVisibility(View.INVISIBLE);
                        } else {
                            Log.w("Failure", "createUserWithEmail:failure", task.getException());
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            switch (errorCode) {

                                case "ERROR_INVALID_CREDENTIAL":
                                    Toast.makeText(Register.this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                                    email1.requestFocus();
                                    break;

                                case "ERROR_INVALID_EMAIL":
                                    Toast.makeText(Register.this, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                                    email1.requestFocus();
                                    break;

                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                    Toast.makeText(Register.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    Toast.makeText(Register.this, "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                    Toast.makeText(Register.this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_OPERATION_NOT_ALLOWED":
                                    Toast.makeText(Register.this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_WEAK_PASSWORD":
                                    Toast.makeText(Register.this, "The given password is invalid.", Toast.LENGTH_LONG).show();
                                    break;

                            }
                        }
                    }
                });
        p1.setVisibility(View.INVISIBLE);
        return;
    }  //finally verfication
}