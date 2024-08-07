package com.example.curiouscurators;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity to display detailed information and image of a specific card.
 */
public class SingleCardActivity extends AppCompatActivity {
    ImageView cardImage, typeImage, setImage;
    TextView cardName, artistName, setName, rarity, localId, category;
    Button returnButton, addButton;
    ExecutorService downloadThread;
    Card card;

    private static final List<Card> recentlyViewedCards = new ArrayList<>();
    private static final int MAX_RECENTLY_VIEWED = 10;


    /**
     * Initializes the activity, setting up the user interface and loading the card details.
     * This method sets the layout, applies window insets, initializes card data,
     * and starts an image download thread.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_card);

        // Set up the bottom navigation vie
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_search);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) { // Navigate to the home activity
                startActivity(new Intent(getApplicationContext(), HomeView.class));
                finish(); // Closes activity
                return true;
            } else if (itemId == R.id.bottom_search) { // Navigate to the search activity
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                finish(); // Closes activity
                return true;
            } else if (itemId == R.id.bottom_collection) { // Navigate to the collection activity
                startActivity(new Intent(getApplicationContext(), CollectionView.class));
                finish(); // Closes activity
                return true;
            }
            return false;
        });

        // Adjust padding to account for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Card.initialize(this); // Initialize card data
        String id = getIntent().getStringExtra("id"); // Get the card ID from the intent extras
        if (id == null) {
            id = "xy8-79"; // Default ID if none is provided
        }

        // Retrieve the card data based on the ID
        this.card = Card.getCardById(id);

        // Initialize UI elements
        this.cardImage = findViewById(R.id.cardImage);
        this.typeImage = findViewById(R.id.type);
        this.setImage = findViewById(R.id.setImage);
        this.cardName = findViewById(R.id.name);
        this.artistName = findViewById(R.id.artistName);
        this.setName = findViewById(R.id.setName);
        this.rarity = findViewById(R.id.rarity);
        this.localId = findViewById(R.id.localId);
        this.category = findViewById(R.id.category);
        this.returnButton = findViewById(R.id.returnButton);
        this.addButton = findViewById(R.id.addButton);

        // Set the images and text views with card details
        this.setImage.setImageDrawable(Card.getLogoById(card.setId));
        this.cardName.setText(card.name);
        this.artistName.setText(card.illustrator);
        this.setName.setText(card.setName);
        this.rarity.setText(card.rarity);
        this.localId.setText(card.localId);
        this.category.setText(card.category);

        // Set the type image based on card category
        if (card.category.equals("Pokemon")) {
            Card.Pokemon pkm = (Card.Pokemon) card;
            String type = pkm.types.get(0);
            typeImage.setImageDrawable(Card.getEnergySymbol(type));
        }
        else if (card.category.equals("Energy")) {
            Card.Energy energy = (Card.Energy) card;
            String type = energy.type;
            typeImage.setImageDrawable(Card.getEnergySymbol(type));
        }

        this.setAddButtonText(); // Update the add button text based on card ownership
        this.addButton.setOnClickListener(new View.OnClickListener() { // Set up the click listener for the add button
            @Override
            public void onClick(View view) {
                boolean owned = Card.isCardOwned(card.globalId);
                Card.setCardOwned(SingleCardActivity.this, card.globalId, !owned);
                setAddButtonText(); // Update the button text after adding/removing the card
            }
        });

        // Set up the click listener for the return button
        this.returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               SingleCardActivity.this.finish();
            }
        });

        // Set up an executor for downloading the card image in a background thread
        this.downloadThread = Executors.newSingleThreadExecutor();
        this.downloadThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construct the image URL and open an input stream
                    InputStream stream = new URL(card.image + "/high.webp").openStream();
                    Bitmap bm = BitmapFactory.decodeStream(stream); // Decode the image stream
                    SingleCardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cardImage.setImageBitmap(bm); // Update the ImageView on the main thread
                        }
                    });
                } catch (IOException e) {
                    System.out.println("Could not get image");
                }
            }
        });

        // Add current card to Recently Viewed Cards
        addToRecentlyViewed(card);

    }

    /**
     * Updates the text of the add button based on whether the card is owned.
     */
    private void setAddButtonText() {
        boolean owned = Card.isCardOwned(this.card.globalId);
        if (owned) {
            this.addButton.setText("Remove from Collection");
        } else {
            this.addButton.setText("Add to Collection");
        }
    }

    /**
     * Adds a card to the list of recently viewed cards.
     * If the list exceeds the maximum size, the oldest card is removed.
     * @param card The card to be added to the recently viewed list.
     */
    private void addToRecentlyViewed(Card card) {
        // Check if the card is already in the list
        for (Card c : recentlyViewedCards) {
            if (c.globalId.equals(card.globalId)) {
                // Card is already in the list, so return early
                return;
            }
        }

        // If the card is not in the list, add it
        if (recentlyViewedCards.size() >= MAX_RECENTLY_VIEWED) {
            recentlyViewedCards.remove(0); // Remove the oldest card if the list exceeds the max size
        }
        recentlyViewedCards.add(card);
    }

    /**
     * Retrieves the list of recently viewed cards.
     * @return A list of recently viewed cards.
     */
    public static List<Card> getRecentlyViewedCards() {
        return recentlyViewedCards;
    }
}


