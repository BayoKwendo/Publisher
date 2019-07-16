package com.author.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;



import androidx.appcompat.app.AppCompatActivity;


import com.author.Author.About;
import com.author.Author.MainActivity;
import com.author.R;
import com.author.User.User;
import com.author.publisher.Publisher;
import com.author.Authentication.ResetPasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin;
    private TextView register, mview, mforgot, mabout;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference mUserDatabase;
    private String mRole;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mabout = findViewById(R.id.about);
        register = findViewById(R.id.register);
        mview = findViewById(R.id.mview);
        mforgot = findViewById(R.id.forgot);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });
        mabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, About.class));
            }
        });
        mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, User.class));
            }
        });

        mforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    String user_id = mAuth.getCurrentUser().getUid();
                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                if(map.get("role")!=null){
                                    mRole = map.get("role").toString();

                                    if (mRole.equals("Author"))
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                    else if(mRole.equals("Publisher"))
                                        intent = new Intent(LoginActivity.this, Publisher.class);

                                    startActivity(intent);
                                    finish();
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        };

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mLogin = (Button) findViewById(R.id.login);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required");
                    mEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password required");
                    mPassword.requestFocus();
                    return;
                }
                mProgressDialog.setMessage("Processing...Please Wait");
                mProgressDialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.show();
                        if(!task.isSuccessful()){
                            mProgressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
                        }
                        else {


                        }
//                            String user_id = mAuth.getCurrentUser().getUid();
//                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
//                            mUserDatabase.addValueEventListener(new ValueEventListener() {
//                                                                    @Override
//                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
//                                                                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                                                                            if(map.get("role")!=null){
//                                                                                mRole = map.get("role").toString();
//
//                                                                                if (mRole.equals("Author"))
//                                                                                    intent = new Intent(LoginActivity.this, MainActivity.class);
//                                                                                else if(mRole.equals("Publisher"))
//                                                                                    intent = new Intent(LoginActivity.this, Publisher.class);
//
//                                                                                startActivity(intent);
//                                                                                finish();
//                                                                                return;
//                                                                            }
//                                                                        }
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onCancelled(DatabaseError databaseError) {
//
//                                                                    }
//                                                                });
                            mProgressDialog.dismiss();
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
         finishAffinity();
    }
}
