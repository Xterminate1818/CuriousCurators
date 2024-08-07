package com.example.curiouscurators;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

/**
 * Activity class for displaying a collection of owned cards in a RecyclerView.
 *
 * This activity sets up the user interface for viewing a collection of owned cards using a RecyclerView.
 * It also includes navigation handling with a BottomNavigationView and applies window insets for proper
 * padding around system bars. The `ownedCards` list is initialized and set to the RecyclerView adapter
 * for display.
 */
public class CollectionView extends AppCompatActivity {
    private RecyclerView collectionRecycler;
    private CollectionRecyclerViewAdapter collectionAdapter;
    private ArrayList<Card> ownedCards;

    /**
     * Called when the activity is first created.
     *
     * This method initializes the activity, sets the content view, configures the BottomNavigationView
     * for navigation between different activities, and sets up the RecyclerView to display the list of
     * owned cards. It also handles window insets to ensure proper padding around system bars.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state. If the activity
     * has never been created before, this value is null.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display to utilize the full screen area
        EdgeToEdge.enable(this);

        // Set the content view to the activity_collection layout
        setContentView(R.layout.activity_collection);

        // Initialize the BottomNavigationView and set the default selected item
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_collection);

        // Set an item selected listener for navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), HomeView.class));
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.bottom_collection) {
                return true;
            }
            return false;
        });

        // Set up window insets to handle system bars and apply padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Card.initialize(this);

        this.ownedCards = Card.getOwnedCards();

        // Set up the RecyclerView with an adapter and layout manager
        this.collectionRecycler = findViewById(R.id.collectionRecyclerView);
        this.collectionAdapter = new CollectionRecyclerViewAdapter(this, this.ownedCards);
        this.collectionRecycler.setAdapter(this.collectionAdapter);
        this.collectionRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}

/**
 * Adapter for displaying a list of owned cards in a RecyclerView within CollectionView.
 */
class CollectionRecyclerViewAdapter
        extends RecyclerView.Adapter<CollectionRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<Card> cards;

    public CollectionRecyclerViewAdapter(Context context, ArrayList<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    /**
     * Creates a new {@code MyViewHolder} instance by inflating the layout for a single row in the RecyclerView.
     * This method is called when a new ViewHolder is needed. It inflates the {@code search_row} layout and
     * returns a new instance of {@code MyViewHolder} to manage the views in that layout.
     * @param parent The parent ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new {@code MyViewHolder} instance.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_row, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Binds data to the {@code MyViewHolder} for a specific position in the RecyclerView.
     * This method is called to display the data at the specified position. It sets the data for the views within
     * the ViewHolder and handles item click events to start the {@code SingleCardActivity} with the card's global ID.
     * @param holder The {@code MyViewHolder} to bind data to.
     * @param position The position of the item within the adapter's data set.
     */
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
        // UI elements for displaying card details
        TextView name, artist, set;
        ImageView setLogo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the TextViews and ImageView from the itemView layout
            name = itemView.findViewById(R.id.cardName);
            setLogo = itemView.findViewById(R.id.setLogo);
            artist = itemView.findViewById(R.id.artistName);
            set = itemView.findViewById(R.id.setName);
        }
    }
}