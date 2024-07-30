package com.example.curiouscurators;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Card.getCards(this);
        RecyclerView rv = findViewById(R.id.searchRecyclerView);
        SearchRecyclerViewAdapter adapter = new SearchRecyclerViewAdapter(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
}

class SearchRecyclerViewAdapter
        extends RecyclerView.Adapter
        <SearchRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<Card> cards;

    public SearchRecyclerViewAdapter(Context context) {
        this.context = context;
        this.cards = new ArrayList<Card>(Card.getCards(context).values());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Card current = this.cards.get(position);
        holder.name.setText(current.name);
        holder.set.setText(current.setName);
        holder.artist.setText(current.illustrator);
    }

    @Override
    public int getItemCount() {
        return this.cards.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, set, artist;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cardName);
            set = itemView.findViewById(R.id.setName);
            artist = itemView.findViewById(R.id.artistName);
        }
    }
}
