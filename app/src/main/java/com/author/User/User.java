package com.author.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.author.model.Book;
import com.author.R;
import com.author.mpesa.GateWay;
import com.author.publisher.Publisher;
import com.author.publisher.Publisher_Edit;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class User extends AppCompatActivity {

    RecyclerView recyle;
     private DatabaseReference Books;
    TextView text;
    private String image;
    private LinearLayout mProgressLayout;
    private FirebaseRecyclerAdapter<Book, BooksViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        text = findViewById(R.id.tv_messages_error);
        recyle = findViewById(R.id.eyclerview_book_list);
        recyle.setLayoutManager(new LinearLayoutManager(this));
        mProgressLayout = findViewById(R.id.layout_discussions_progress);


//        String bookKey = getIntent().getStringExtra("bookKey");
//        Log.e("TopicsActivity","" + bookKey);

         Books = FirebaseDatabase.getInstance().getReference().child("Books");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query dbRefFirstTimeCheck = Books.orderByChild("review").equalTo(true);

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


        Query query = Books.orderByChild("review").equalTo(true);

        FirebaseRecyclerOptions<Book> options =
                new FirebaseRecyclerOptions.Builder<Book>()
                        .setQuery(query, Book.class)
                        .setLifecycleOwner(this)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Book, BooksViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final BooksViewHolder holder, final int position, @NonNull final Book model) {

                mProgressLayout.setVisibility(View.GONE);

                String m = String.valueOf(model.getPrice());



                holder.booktitle.setText(model.getBookTitle());


                holder.mprice.setText("Ksh." + m);




                image = model.getURL();
                Glide.with(getApplication()).load(image).into(holder.imageView);




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

                holder.mView.setOnClickListener(v -> {

                    Intent topicsIntent = new Intent(User.this, GateWay.class);
                    topicsIntent.putExtra("bookKey", getRef(position).getKey());
                    startActivity(topicsIntent);

                });

            }

            @NonNull
            @Override
            public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return  new BooksViewHolder(LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.user_item, parent, false));


            }
        };

        adapter.startListening();
        recyle.setAdapter(adapter);

    }

    private class BooksViewHolder extends RecyclerView.ViewHolder{
        TextView booktitle, shortdescription, mprice;
       ImageView imageView;
        private View mView;

        public BooksViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            imageView = itemView.findViewById(R.id.profileImage);
            booktitle = itemView.findViewById(R.id.name);
            mprice = itemView.findViewById(R.id.price);


        }
    }
}
