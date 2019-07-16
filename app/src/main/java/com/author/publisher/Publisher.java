package com.author.publisher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.author.Authentication.LoginActivity;
import com.author.Author.MainActivity;
import com.author.R;
import com.author.model.Book;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Publisher extends AppCompatActivity {

    RecyclerView recyle;
    private LinearLayout mProgressLayout;
    private Boolean isLoggingOut = false;
     private DatabaseReference Books;
     TextView text;
    private FirebaseRecyclerAdapter<Book, BooksViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher);
        text = findViewById(R.id.tv_messages_error);
        mProgressLayout = findViewById(R.id.layout_discussions_progress);
        recyle = findViewById(R.id.reyclerview_book_list);
        recyle.setLayoutManager(new LinearLayoutManager(this));


        String bookKey = getIntent().getStringExtra("bookKey");
        Log.e("TopicsActivity","" + bookKey);

         Books = FirebaseDatabase.getInstance().getReference().child("Books");

    }


    @Override
    protected void onStart() {
        super.onStart();

        Query dbRefFirstTimeCheck = Books.orderByChild("review").equalTo(false);

        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mProgressLayout.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                }
                loadData();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });
    }
    private void loadData(){
        Query query = Books.orderByChild("review").equalTo(false).limitToFirst(20);


        FirebaseRecyclerOptions<Book> options =
                new FirebaseRecyclerOptions.Builder<Book>()
                        .setQuery(query, Book.class)
                        .setLifecycleOwner(this)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Book, BooksViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final BooksViewHolder holder, final int position, @NonNull final Book model) {
                holder.booktitle.setText(model.getBookTitle());
                holder.shortdescription.setText(model.getShortDescription());
                holder.mDateCreatedTv.setText(model.getDateCreated());
                mProgressLayout.setVisibility(View.GONE);


//                FirebaseDatabase.getInstance().getReference().child("Books")
//                        .child(model.getBookTitle()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        String username = dataSnapshot.child("Book Author").getValue(String.class);
//
//
//                        holder.booktitle.setText(username);
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent topicsIntent = new Intent(Publisher.this, Publisher_Edit.class);
                        topicsIntent.putExtra("bookKey", getRef(position).getKey());
                        startActivity(topicsIntent);

                    }
                });


            }

            @NonNull
            @Override
            public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return  new BooksViewHolder(LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.book_item, parent, false));

            }
        };

        adapter.startListening();
        recyle.setAdapter(adapter);

    }



    private class BooksViewHolder extends RecyclerView.ViewHolder{
        TextView booktitle, shortdescription, mDateCreatedTv;

        private View mView;

        public BooksViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            booktitle = itemView.findViewById(R.id.book_name);
            shortdescription = itemView.findViewById(R.id.short_discription);
            mDateCreatedTv = itemView.findViewById(R.id.tv_date_created);


        }
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
            Intent intent = new Intent(Publisher.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }
}
