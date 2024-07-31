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

public class SingleCardActivity extends AppCompatActivity {
    ImageView cardImage;
    TextView cardName, artistName, setName;
    ExecutorService downloadThread;

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
        this.cardImage = findViewById(R.id.cardImage);
        this.cardName = findViewById(R.id.cardName);
        this.artistName = findViewById(R.id.artistName);
        this.setName = findViewById(R.id.setName);
        // Default ID for testing
        String defaultId = "xy8-79";
        String id = getIntent().getStringExtra("id");
        if (id == null) {
            id = defaultId;
        }
        Card card = Card.getCardById(id);
        this.cardName.setText(card.name);
        this.setName.setText(card.setName);
        this.artistName.setText(card.illustrator);

        this.downloadThread = Executors.newSingleThreadExecutor();
        this.downloadThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream stream = new URL(card.image + "/high.webp").openStream();
                    Bitmap bm = BitmapFactory.decodeStream(stream);
                    SingleCardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cardImage.setImageBitmap(bm);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}


