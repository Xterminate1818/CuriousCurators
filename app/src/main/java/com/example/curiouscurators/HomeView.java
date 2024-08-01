package com.example.curiouscurators;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

/**
 * HomeView is an activity that serves as the main screen of the card app.
 * It includes a bottom navigation bar for navigating between different sections of the app.
 * This activity initializes the bottom navigation view, sets up navigation item selection handling,
 * and adjusts padding for window insets to ensure content is not obscured by system bars.
 */
public class HomeView extends AppCompatActivity {
    private RecyclerView collectionRecycler;
    private HomeRecyclerViewAdapter collectionAdapter;
    private ArrayList<Card> ownedCards;
    /**
     * Called when the activity is first created.
     * This method sets up the layout, initializes the bottom navigation view, handles item selections,
     * and applies window insets for proper padding.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState(Bundle)}.
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.bottom_collection) {
                startActivity(new Intent(getApplicationContext(), CollectionView.class));
                finish();
                return true;
            }
            return false;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Card.initialize(this);

        this.ownedCards = Card.getOwnedCards();

        this.collectionRecycler = findViewById(R.id.collectionRecyclerView);
        this.collectionAdapter = new HomeRecyclerViewAdapter(this, this.ownedCards);
        this.collectionRecycler.setAdapter(this.collectionAdapter);
        this.collectionRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}

/**
 * Adapter for displaying a list of owned cards in a RecyclerView within CollectionView.
 */
class HomeRecyclerViewAdapter
        extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<Card> cards;

    public HomeRecyclerViewAdapter(Context context, ArrayList<Card> cards) {
        this.context = context;
        this.cards = cards;
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
        holder.setLogo.setImageDrawable(Card.getLogoById(current.setId));
        holder.artist.setText(current.illustrator);
        holder.set.setText(current.setName);
        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, SingleCardActivity.class);
            i.putExtra("id", current.globalId);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return this.cards.size();
    }

    /**
     * ViewHolder for the card items in the RecyclerView.
     * Holds references to the views in each item of the RecyclerView.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, artist, set;
        ImageView setLogo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cardName);
            setLogo = itemView.findViewById(R.id.setLogo);
            artist = itemView.findViewById(R.id.artistName);
            set = itemView.findViewById(R.id.setName);
        }
    }
}