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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_search);

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
        String id = getIntent().getStringExtra("id");
        if (id == null) {
            id = "xy8-79";
        }
        this.card = Card.getCardById(id);
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

        this.setImage.setImageDrawable(Card.getLogoById(card.setId));
        this.cardName.setText(card.name);
        this.artistName.setText(card.illustrator);
        this.setName.setText(card.setName);
        this.rarity.setText(card.rarity);
        this.localId.setText(card.localId);
        this.category.setText(card.category);

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

        this.setAddButtonText();
        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean owned = Card.isCardOwned(card.globalId);
                Card.setCardOwned(card.globalId, !owned);
                setAddButtonText();
            }
        });

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
    }

    private void setAddButtonText() {
        boolean owned = Card.isCardOwned(this.card.globalId);
        if (owned) {
            this.addButton.setText("Remove from Collection");
        } else {
            this.addButton.setText("Add to Collection");
        }
    }
}


