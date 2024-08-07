package com.example.curiouscurators;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Collections;
import java.util.Random;

/**
 * HomeView is an activity that serves as the main screen of the card app.
 * It includes a bottom navigation bar for navigating between different sections of the app.
 * This activity initializes the bottom navigation view, sets up navigation item selection handling,
 * and adjusts padding for window insets to ensure content is not obscured by system bars.
 */
public class HomeView extends AppCompatActivity {
    private RecyclerView recentlyRecyclerView;
    private HomeRecyclerViewAdapter recentlyAdapter;
    private ArrayList<Card> ownedCards;
    private ArrayList<Card> allCards;
    private Button randomCardButton;
    private TextView totalCardsCollectedTextView;
    private TextView totalCardsLoadedTextView;

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
        this.allCards = Card.getAllCards();

        // Update the RecyclerView for Recently Viewed Cards
        this.recentlyRecyclerView = findViewById(R.id.recentlyRecyclerView);

        // Retrieve and sort recently viewed cards
        ArrayList<Card> recentlyViewedCards = new ArrayList<>(SingleCardActivity.getRecentlyViewedCards());
        Collections.reverse(recentlyViewedCards); // Sort in reverse order

        this.recentlyAdapter = new HomeRecyclerViewAdapter(this, recentlyViewedCards);
        this.recentlyRecyclerView.setAdapter(this.recentlyAdapter);
        this.recentlyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.randomCardButton = findViewById(R.id.randomCard);
        this.randomCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the complete list of cards
                ArrayList<Card> allCards = new ArrayList<>(Card.getAllCards());
                if (!allCards.isEmpty()) {
                    // Randomly select a card from the entire dataset
                    Random random = new Random();
                    Card randomCard = allCards.get(random.nextInt(allCards.size()));

                    // Start the SingleCardActivity with the selected card's global ID
                    Intent intent = new Intent(HomeView.this, SingleCardActivity.class);
                    intent.putExtra("id", randomCard.globalId);
                    startActivity(intent);
                }
            }
        });
        this.totalCardsCollectedTextView = findViewById(R.id.totalCardsCollected);
        updateTotalCardsCollected();

        this.totalCardsLoadedTextView = findViewById(R.id.totalCardsLoaded);
        updateTotalCardsLoaded();
    }

    /**
     * Updates the totalCardsCollected TextView with the number of collected cards.
     */
    private void updateTotalCardsCollected() {
        int totalCardsCollected = this.ownedCards.size();
        String text = "\t\t\t\t\t Cards\n\t\t\t\tCollected\n\t\t\t\t\t  #" + totalCardsCollected;
        this.totalCardsCollectedTextView.setText(text);
    }

    private void updateTotalCardsLoaded() {
        int totalCardsLoaded = this.allCards.size();
        String text = "\t\t\t\t Cards In\n\t\t\t\tDatabase\n\t\t\t\t #" + totalCardsLoaded;
        this.totalCardsLoadedTextView.setText(text);
    }
}

/**
 * Adapter class for displaying a list of cards in a RecyclerView on the Home screen.
 * Handles creating and binding view holders for each item in the list.
 */
class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.MyViewHolder> {
    Context context; // Context to access application resources and start activities
    ArrayList<Card> cards; // List of cards to be displayed in the RecyclerView

    /**
     * Constructor for the HomeRecyclerViewAdapter.
     * @param context The context of the activity or application.
     * @param cards   The list of cards to be displayed in the RecyclerView.
     */
    public HomeRecyclerViewAdapter(Context context, ArrayList<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual items in the RecyclerView
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Get the current card based on position
        Card current = this.cards.get(position);

        // Set the card details to the views in the ViewHolder
        holder.name.setText(current.name);
        holder.setLogo.setImageDrawable(Card.getLogoById(current.setId));
        holder.artist.setText(current.illustrator);
        holder.set.setText(current.setName);

        // Set an OnClickListener to handle item clicks
        holder.itemView.setOnClickListener(view -> {
            // Start SingleCardActivity and pass the card's globalId as an extra
            Intent i = new Intent(context, SingleCardActivity.class);
            i.putExtra("id", current.globalId);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        // Return the total number of cards in the list
        return this.cards.size();
    }

    /**
     * ViewHolder for the card items in the RecyclerView.
     * Holds references to the views in each item of the RecyclerView.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // UI elements to display card details
        TextView name, artist, set;
        ImageView setLogo;

        /**
         * Constructor for the ViewHolder. Initializes the UI elements for the item view.
         *
         * @param itemView The view of the item within the RecyclerView.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find and initialize the TextViews and ImageView from the item view layout
            name = itemView.findViewById(R.id.cardName);
            setLogo = itemView.findViewById(R.id.setLogo);
            artist = itemView.findViewById(R.id.artistName);
            set = itemView.findViewById(R.id.setName);
        }
    }
}