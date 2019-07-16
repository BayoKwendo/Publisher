package com.author.publisher;

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
import com.author.Author.MainActivity;
import com.author.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Publisher_Edit extends AppCompatActivity {

    private EditText  shortiput, longinpit;
    private DatabaseReference Books;
    private Boolean isLoggingOut = false;
    private TextView title, author;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publisher_editor);

        title = findViewById(R.id.title);
        author = findViewById(R.id.author_name);
        shortiput = findViewById(R.id.description);
        longinpit = findViewById(R.id.shipper_field);
        Books = FirebaseDatabase.getInstance().getReference().child("Books")
                .child(getIntent().getStringExtra("bookKey"));
        btn = findViewById(R.id.post);

        Display();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(Publisher_Edit.this);
                progressDialog.setTitle("Updating...Please Wait");
                progressDialog.show();
                final String  mshortiput= shortiput.getText().toString();
                final String mlonginput = longinpit.getText().toString();

                Books.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                             progressDialog.dismiss();
                            dataSnapshot.getRef().child("SHortDescription").setValue(mshortiput);
                            dataSnapshot.getRef().child("LongDescription").setValue(mlonginput);
                            dataSnapshot.getRef().child("review").setValue(true);

                        dialogue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException(); // never ignore errors
                    }
                });


            }});
       }

       void Display() {


           Books.addListenerForSingleValueEvent(new ValueEventListener() {
               public void onDataChange(DataSnapshot dataSnapshot) {

                   String mauthor = (String) dataSnapshot.child("BookAuthor").getValue();
                   String BookTitle = (String) dataSnapshot.child("BookTitle").getValue();
                   String SHortDescription = (String) dataSnapshot.child("SHortDescription").getValue();
                   String LongDescription = (String) dataSnapshot.child("LongDescription").getValue();
                   // Do something


                   title.setText(BookTitle);
                   author.setText(mauthor);
                   shortiput.setText(SHortDescription);
                   longinpit.setText(LongDescription);


               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
                   throw databaseError.toException(); // never ignore errors
               }
           });
       }
    void dialogue(){
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("SUCCESS")
                .setContentText("You have Updated Successful")
                .setConfirmText("OK")
                .setConfirmClickListener(sDialog -> {
                    // reuse previous dialog instance
                    startActivity(new Intent(Publisher_Edit.this, Publisher.class));

                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Return true to display menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as Forums specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            isLoggingOut = true;

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Publisher_Edit.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }

    }


