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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.author.Author.MainActivity;
import com.author.R;
import com.author.publisher.Publisher;
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

public class RegisterActivity extends AppCompatActivity {
    private EditText mEmail, mPassword,mEditTextPhone, Firstname, SecondName,Username,PasswordAgain;
    private Button mRegistration;
    private RadioGroup mRadioGroup;
    private ProgressDialog mProgressDialog;
    TextView mlogin;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private String mRole;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);
        mlogin = findViewById(R.id.login);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mRadioGroup.check(R.id.authour);

        mAuth = FirebaseAuth.getInstance();


        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

            }
        });
//        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if(user!=null){
//                    if (mRole.equals("Author"))
//                        intent = new Intent(RegisterActivity.this, MainActivity.class);
//                    else if(mRole.equals("Publisher"))
//                        intent = new Intent(RegisterActivity.this, Publisher.class);
//
//                    startActivity(intent);
//                    finish();
//                    return;
//                }
//            }
//        };

        mEmail = (EditText) findViewById(R.id.email);

        mPassword = (EditText) findViewById(R.id.password);

        mEditTextPhone = (EditText) findViewById(R.id.login_edittext_phone);



        mRegistration = (Button) findViewById(R.id.register);

        mProgressDialog = new ProgressDialog(this);

        Firstname = (EditText) findViewById(R.id.firstname);

        SecondName = (EditText) findViewById(R.id.lastname);

        Username = (EditText) findViewById(R.id.username);

        PasswordAgain = (EditText) findViewById(R.id.conf_password);


        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String mPhone = mEditTextPhone.getText().toString().trim();

                final String firstname = Firstname.getText().toString();
                final String secondname = SecondName.getText().toString();
                final String username = Username.getText().toString();
                final String PassAgain = PasswordAgain.getText().toString();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                int selectId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(TextUtils.isEmpty(firstname)){
                    Firstname.setError("Firstname required");
                    Firstname.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Firstname required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(secondname)){
                    SecondName.setError("LastNmae required");
                    SecondName.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Lastname required", Toast.LENGTH_SHORT).show();

                    return;
                }

                if(TextUtils.isEmpty(username)){
                    Username.setError("Username required");
                    Username.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Username required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required");
                    mEmail.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Email required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!email.matches(emailPattern)){
                    mEmail.setError("Enter valid Email ");
                    mEmail.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mPhone)){
                    mEditTextPhone.setError("EnterPhone");
                    mEditTextPhone.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Phone Number required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPhone.length() <9||mPhone.length() ==9  ){
                    mEditTextPhone.setError("Phone Number Must be 10 digits");
                    mEditTextPhone.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password required");
                    mPassword.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Paasword required", Toast.LENGTH_SHORT).show();

                    return;
                }
                 if (password.length() < 6){
                    mPassword.setError("Password Require minimum of 6 characters");
                     mPassword.requestFocus();
                     Toast.makeText(RegisterActivity.this, "Password Require minimum of 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }


               if(password.length() != PassAgain.length()){
                    PasswordAgain.setError("Password Not Match");
                   PasswordAgain.requestFocus();
                   Toast.makeText(RegisterActivity.this, "Password Not Match", Toast.LENGTH_SHORT).show();
                    return;
                }


               if (radioButton.getText() == null){
                    return;
                }
                mProgressDialog.setMessage("Registering");
                mProgressDialog.show();
//
                mRole = radioButton.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }else{
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("name");
                            DatabaseReference current_user_role = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("role");
                            DatabaseReference current_user_email = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("email");
                            DatabaseReference current_user_phone = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("phone");
                            DatabaseReference current_user_first = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("FirstName");
                            DatabaseReference current_user_second = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("second");


                            current_user_email.setValue(email);
                            current_user_phone.setValue(mPhone);
                            current_user_first.setValue(firstname);
                            current_user_second.setValue(secondname);
                            current_user_db.setValue(username);
                            current_user_role.setValue(mRole);

                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                            mUserDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                        if(map.get("role")!=null){
                                            mRole = map.get("role").toString();

                                            if (mRole.equals("Author"))
                                                intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            else if(mRole.equals("Publisher"))
                                                intent = new Intent(RegisterActivity.this, Publisher.class);

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
//                            Toast.makeText(RegisterActivity.this, "SUCCESSFUL", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                            mProgressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
//        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
