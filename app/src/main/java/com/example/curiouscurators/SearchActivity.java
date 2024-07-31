package com.example.curiouscurators;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * SearchActivity provides functionality for searching and filtering a list of cards.
 * This activity includes a RecyclerView for displaying search results, a SearchView for query input,
 * and a Spinner for selecting filter types. It initializes the UI components and sets up listeners
 * for user interactions.
 */
public class SearchActivity extends AppCompatActivity {
    private RecyclerView cardRecycler;
    private SearchRecyclerViewAdapter cardAdapter;
    private CardSubset searchSubset;
    private Spinner searchType;
    private ArrayAdapter<CharSequence> searchAdapter;

    /**
     * Called when the activity is first created.
     * <p>
     * Sets up the layout, initializes the bottom navigation view, configures the search filter spinner,
     * and sets up the search functionality.
     * </p>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState(Bundle)}.
     *                           Otherwise, it is null.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_search);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), HomeView.class));
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
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
        this.cardRecycler = findViewById(R.id.searchRecyclerView);
        this.searchType = findViewById(R.id.searchType);
        this.searchAdapter = ArrayAdapter.createFromResource(
                this, R.array.searchDropdown,
                android.R.layout.simple_spinner_item);
        this.searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.searchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                System.out.println(pos);
                switch(pos) {
                    case 0:
                        SearchActivity.this.searchSubset.setFilterType(CardSubset.FilterType.Name);
                        break;
                    case 1:
                        SearchActivity.this.searchSubset.setFilterType(CardSubset.FilterType.Artist);
                        break;
                    case 2:
                        SearchActivity.this.searchSubset.setFilterType(CardSubset.FilterType.Set);
                        break;
                }
                SearchActivity.this.cardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                SearchActivity.this.searchSubset.setFilterType(CardSubset.FilterType.Name);
            }
        });
        this.searchType.setAdapter(this.searchAdapter);
        this.searchSubset = new CardSubset();
        this.cardAdapter = new SearchRecyclerViewAdapter(this, this.searchSubset);
        this.cardRecycler.setAdapter(this.cardAdapter);
        this.cardRecycler.setLayoutManager(new LinearLayoutManager(this));
        SearchView search = findViewById(R.id.searchBar);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextSubmit(String s) {
                SearchActivity.this.searchSubset.setFilter(s);
                SearchActivity.this.cardAdapter.notifyDataSetChanged();
                return true;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String s) {
                SearchActivity.this.searchSubset.setFilter(s);
                SearchActivity.this.cardAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }
}

/**
 * Adapter for displaying a list of cards in a RecyclerView within SearchActivity.
 * This adapter binds card data to the views in the RecyclerView and handles item click events
 * to navigate to the detailed view of a selected card.
 */
class SearchRecyclerViewAdapter
        extends RecyclerView.Adapter
        <SearchRecyclerViewAdapter.MyViewHolder> {
    Context context;
    CardSubset cards;

    /**
     * Constructs a SearchRecyclerViewAdapter.
     *
     * @param context The context used to access resources and start activities.
     * @param subset  The subset of cards to be displayed in the RecyclerView.
     */

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
        holder.set.setText(current.setName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SingleCardActivity.class);
                i.putExtra("id", current.globalId);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.cards.getContained().size();
    }

    /**
     * ViewHolder for the card items in the RecyclerView.
     * Holds references to the views in each item of the RecyclerView.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, artist, set;
        ImageView setLogo;

        /**
         * Constructs a MyViewHolder.
         *
         * @param itemView The view of the item in the RecyclerView.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cardName);
            setLogo = itemView.findViewById(R.id.setLogo);
            artist = itemView.findViewById(R.id.artistName);
            set = itemView.findViewById(R.id.setName);
        }
    }
}
