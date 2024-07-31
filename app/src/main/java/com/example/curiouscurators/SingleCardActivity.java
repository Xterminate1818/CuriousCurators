package com.example.curiouscurators;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.URL;

/**
 * Activity to display detailed information and image of a specific card
 */
public class SingleCardActivity extends AppCompatActivity {
    ImageView cardImage;
    TextView cardName, artistName, setName;
    ExecutorService downloadThread;

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Card.initialize(this);

        // Hook up the UI components to their corresponding views in the layout
        this.cardImage = findViewById(R.id.cardImage);
        this.cardName = findViewById(R.id.cardName);
        this.artistName = findViewById(R.id.artistName);
        this.setName = findViewById(R.id.setName);

        // Retrieve the card ID from the intent or use a default for demonstration
        String defaultId = "xy8-79";
        String id = getIntent().getStringExtra("id");
        if (id == null) {
            id = defaultId;
        }
        Card card = Card.getCardById(id);

        // Update UI elements with the card details
        this.cardName.setText(card.name);
        this.setName.setText(getString(R.string.setNamePrefix, card.setName));
        this.artistName.setText(getString(R.string.artistNamePrefix, card.illustrator));

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
                    throw new RuntimeException(e); // Handle potential IO errors
                }
            }
        });
    }
}


