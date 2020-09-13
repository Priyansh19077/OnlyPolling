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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    //instance variables
    private static final int RC_SIGN_IN = 101 ;
    Button createAccount;
    EditText emailAddress;
    String email;
    EditText password;
    Button google;
    String pass;
    Button login;
    boolean arr[] = {false, false};
    FirebaseAuth mAuth;
    ProgressBar p1;
    boolean check;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();  // checking for currently signed in user
        if (mAuth.getCurrentUser() != null) {
            Log.i("AlreadySignedIn", "True");
            if (mAuth.getCurrentUser().isEmailVerified()) {
                Intent in = new Intent(MainActivity.this, Dashboard.class);
                Log.i("SignedInAndVerified", "True");
                startActivity(in);
            } else {
                Log.i("SignedInAndVerified", "False");
                mAuth.signOut();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);    //making the activity full screen
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        setContentView(R.layout.activity_main);

        emailAddress = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPassword);
        login = findViewById(R.id.Login);
        google = findViewById(R.id.googlesign);
        p1 = findViewById(R.id.progressBar3);
        createAccount = findViewById(R.id.CreateAccount);

        email = emailAddress.getText().toString();
        pass = password.getText().toString();
        p1.setVisibility(View.INVISIBLE);

        check_for_empty(email, 0);
        check_for_empty(pass, 1);
        run_a_check();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        emailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = emailAddress.getText().toString();
                check_for_empty(email, 0);
                run_a_check();
                Log.i("EmailChanged", "True");
            }

            @Override
            public void afterTextChanged(Editable s) {
                email = emailAddress.getText().toString();
                check_for_empty(email, 0);
                run_a_check();
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pass = password.getText().toString();
                check_for_empty(pass, 1);
                run_a_check();
                Log.i("PasswordChanged", "True");
            }

            @Override
            public void afterTextChanged(Editable s) {
                pass = password.getText().toString();
                check_for_empty(pass, 1);
                run_a_check();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress.clearFocus();
                password.clearFocus();
                Log.i("CreateAccountClicked", "True");
                Intent in = new Intent(MainActivity.this, Register.class);
                startActivity(in);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress.clearFocus();
                password.clearFocus();
                Log.i("LogInClicked", "True");
                if (run_a_check2()) {
                    login.setEnabled(false);
                    p1.setVisibility(View.VISIBLE);
                    setTimer(0);
                }
                return;
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("GoogleSignInClicked", "True");
                signIn();
            }
        });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.i("GooglePopUpGenerated", "True");
    }  //pop for google sign in


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    } //getting result from google pop up


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            int x = e.getStatusCode();
            Log.i("FailureInSignInGoogle", String.valueOf(e.getStatusCode()));
            if (x == 12501) {
                Toast.makeText(getApplicationContext(), "Cancelled Signing in...", Toast.LENGTH_SHORT).show();
            } else if (x == 12502) {
                Toast.makeText(getApplicationContext(), "Please Avoid making multiple requests...", Toast.LENGTH_SHORT).show();
            } else if (x == 12500) {
                Toast.makeText(getApplicationContext(), "Unsuccessful attempt, Try again...", Toast.LENGTH_SHORT).show();
            } else if (x == 7) {
                Toast.makeText(getApplicationContext(), "Poor Network connection, Try again...", Toast.LENGTH_SHORT).show();
            } else if (x == 4) {
                Toast.makeText(getApplicationContext(), "You are not signed into the selected google account, sign in and try again...", Toast.LENGTH_LONG).show();
            } else if (x == 5) {
                Toast.makeText(getApplicationContext(), "Sorry but that is not a valid account...", Toast.LENGTH_LONG).show();
            } else if (x == 3) {
                Toast.makeText(getApplicationContext(), "Please activate Google Play Services on your device and try again...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Please try again...", Toast.LENGTH_LONG).show();
            }

        }
    } //handle sign in result


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("SignInWithGoogleSuccess", "True");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent in = new Intent(MainActivity.this, Dashboard.class);
                            Toast.makeText(getApplicationContext(), "Welcome...", Toast.LENGTH_SHORT).show();
                            startActivity(in);
                        } else {
                            Log.i("SignInWithGoogleFailure", task.getException().toString());
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.i("InvalidPassword", "True");
                                Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthInvalidUserException) {
                                Log.i("InvalidEmail", "True");
                                Toast.makeText(getApplicationContext(), "No such email registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("NetworkError", "True");
                                Toast.makeText(getApplicationContext(), "Check your network and try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }  //sign in with google


    public void run_a_check() {
        if (arr[0] && arr[1]) {
            login.setTextColor((int)0xFFFFFFFF);
            login.setEnabled(true);
        } else {
            login.setEnabled(false);
            login.setTextColor((int)0x43150202);
        }
    } // to activate or deactivate the login button to prevent null pointer exceptions


    public void check_for_empty(String a, int index) {
        if (a.length() == 0)
            arr[index] = false;
        else
            arr[index] = true;
    }  //checking empty String


    public boolean run_a_check2() {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAddress.requestFocus();
            Toast.makeText(getApplicationContext(), "INVALID EMAIL ADDRESS", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }  //pattern checking for email


    public void setTimer(int time) {
        CountDownTimer cc = new CountDownTimer(1000 * time + 10, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                signing_in();
                return;
            }
        } .start();
    } //calling signing in inside, can be used to put some delay in code


    public boolean signing_in() {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("SignInWithEmailSuccess", "True");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                login.setEnabled(true);
                                p1.setVisibility(View.INVISIBLE);
                                Intent in = new Intent(MainActivity.this, Dashboard.class);
                                Log.i("SignedInAndVerified1", "True");
                                Toast.makeText(getApplicationContext(), "Welcome...", Toast.LENGTH_SHORT).show();
                                startActivity(in);
                                check = true;
                            } else {
                                Log.i("SignedInAndVerified1", "False");
                                Toast.makeText(getApplicationContext(), "Please verify your email first", Toast.LENGTH_SHORT).show();
                                check = false;
                            }
                        } else {
                            Log.i("SignInWithEmailFailure", task.getException().toString());
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.i("InvalidPassword", "True");
                                Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthInvalidUserException) {
                                Log.i("InvalidEmail", "True");
                                Toast.makeText(getApplicationContext(), "No such email registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("NetworkError", "True");
                                Toast.makeText(getApplicationContext(), "Check your network and try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        login.setEnabled(true);
        p1.setVisibility(View.INVISIBLE);
        return false;
    }  //sign in with email
}