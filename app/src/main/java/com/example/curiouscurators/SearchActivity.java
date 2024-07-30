package com.example.curiouscurators;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
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
import java.util.HashSet;

public class SearchActivity extends AppCompatActivity {
    private static final String[] searchTypesArray = {"By name", "By artist"};
    private RecyclerView cardRecycler;
    private SearchRecyclerViewAdapter cardAdapter;
    private CardSubset searchSubset;
    private Spinner searchType;
    private ArrayAdapter<String> searchAdapter;

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
        Card.initialize(this);
        this.cardRecycler = findViewById(R.id.searchRecyclerView);
        this.searchType = findViewById(R.id.searchType);
        this.searchAdapter = new ArrayAdapter<String>(this, R.id.searchType, searchTypesArray);
        this.searchSubset = new CardSubset();
        this.cardAdapter = new SearchRecyclerViewAdapter(this, this.searchSubset);
        this.cardRecycler.setAdapter(this.cardAdapter);
        this.cardRecycler.setLayoutManager(new LinearLayoutManager(this));
        SearchView search = findViewById(R.id.searchBar);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextSubmit(String s) {
                SearchActivity.this.searchSubset.setNameFilter(s);
                SearchActivity.this.cardAdapter.notifyDataSetChanged();
                return true;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String s) {
                SearchActivity.this.searchSubset.setNameFilter(s);
                SearchActivity.this.cardAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }
}

class SearchRecyclerViewAdapter
        extends RecyclerView.Adapter
        <SearchRecyclerViewAdapter.MyViewHolder> {
    Context context;
    CardSubset cards;

    public SearchRecyclerViewAdapter(Context context, CardSubset subset) {
        this.context = context;
        this.cards = subset;
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
        String id = this.cards.getContained().get(position)[1];
        Card current = Card.getCardById(id);
        holder.name.setText(current.name);
        holder.setLogo.setImageDrawable(Card.getLogoById(current.setId));
        holder.artist.setText(current.illustrator);
    }

    @Override
    public int getItemCount() {
        return this.cards.getContained().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, artist;
        ImageView setLogo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cardName);
            setLogo = itemView.findViewById(R.id.setLogo);
            artist = itemView.findViewById(R.id.artistName);
        }
    }
}
