package com.author.Author;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;

import com.author.Authentication.LoginActivity;
import com.author.R;
import com.author.publisher.Publisher;
import com.author.publisher.Publisher_Edit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    private EditText title, author, shortiput, longinpit, mprice;
    private DatabaseReference Books;
    private ImageView image;
    private  String TAG;
    private Boolean isLoggingOut = false;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private  StorageReference refstorage;

    private final int PICK_IMAGE_REQUEST = 71;
    Button btn, choosebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        author = findViewById(R.id.author_name);
        shortiput = findViewById(R.id.description);
        image = findViewById(R.id.imgView);
        mprice = findViewById(R.id.price);
        longinpit = findViewById(R.id.shipper_field);
        choosebtn = findViewById(R.id.btnChoose);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Books = FirebaseDatabase.getInstance().getReference().child("Books");
        btn = findViewById(R.id.post);

        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Posting...Please Wait");
                progressDialog.show();
                uploadImage();
                final String mtitle = title.getText().toString();
                final String mauthor = author.getText().toString();
                final int bookprice = Integer.parseInt(mprice.getText().toString());
                final String  mshortiput= shortiput.getText().toString();
                final String mlonginput = longinpit.getText().toString();
                Map topicMap = new HashMap();
                topicMap.put("BookTitle", mtitle);
                topicMap.put("BookAuthor", mauthor);
                topicMap.put("SHortDescription", mshortiput);
                topicMap.put("price" , bookprice);
                topicMap.put("LongDescription", mlonginput);
                topicMap.put("dateCreated", getTime());
                topicMap.put("review", false);

                Books.push().setValue(topicMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            dialogue();
                        } else {
                            Toast.makeText(MainActivity.this, "EEOR", Toast.LENGTH_SHORT).show();
                        }
                    }

            });


            }});


     }

     void dialogue(){
         new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                 .setTitleText("SUCCESS")
                 .setContentText("You have Posted Successful")
                 .setConfirmText("ok")
                 .show();

        title.setText("");
       author.setText("");
       shortiput.setText("");
        longinpit.setText("");
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }



    void uploadImage(){

    if(filePath != null)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving...Please Wait");
        progressDialog.show();

        StorageReference ref = storageReference.child("CoverPage/"+ UUID.randomUUID().toString());

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = ref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //and you can convert it to string like this:
                final String sdownload_url = downloadUrl.toString();


//                Log.d(TAG, "onSuccess:" + sdownload_url);
//                Toast.makeText(MainActivity.this, "" + sdownload_url, Toast.LENGTH_SHORT).show();
//
//
                Query query = Books.limitToLast(1);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childdata : dataSnapshot.getChildren()) {
                            childdata.getRef().child("URL").setValue(sdownload_url);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException(); // never ignore errors
                    }
                });
                return;
            }
        });

    }

}

    void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        image.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());

        return currentDateandTime;
    }

    }

