package com.author.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.author.Authentication.LoginActivity;
import com.author.R;
import com.author.publisher.Publisher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class view extends AppCompatActivity {

    private EditText  shortiput, longinpit;
    private DatabaseReference Books;
    private String home;
    private TextView mtext;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);

        mtext = findViewById(R.id.mtext);
        Books = FirebaseDatabase.getInstance().getReference().child("Books")
                .child(getIntent().getStringExtra("key"));
        Display();


       }

       void Display() {


           Books.addListenerForSingleValueEvent(new ValueEventListener() {
               public void onDataChange(DataSnapshot dataSnapshot) {

                   String LongDescription = (String) dataSnapshot.child("LongDescription").getValue();

                   mtext.setText(LongDescription);


               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
                   throw databaseError.toException(); // never ignore errors
               }
           });
       }

    }


