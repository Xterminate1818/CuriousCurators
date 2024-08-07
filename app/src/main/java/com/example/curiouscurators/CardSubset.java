package com.example.curiouscurators;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a subset of cards with filtering and sorting options.
 * This class allows for filtering and sorting of card collections based on different criteria.
 * It includes enums to specify filter and sort types, and a list to hold the filtered card data.
 */
public class CardSubset {
    // Filters cards by Name, Artist, and Set
    public enum FilterType {
        Name, Artist, Set
    }

    /**
     * Enum representing the different types of sorting that can be applied to the card subset.
     */
    public enum SortType {
        Name, Artist, Set
    }

    // The filter string used to match against card attributes
    String filter;

    // The type of filter applied to the card subset
    FilterType filterType;

    // List of card data that matches the filter criteria
    ArrayList<String[]> contained;

    /**
     * Constructs a CardSubset with default settings.
     * Initializes the subset with a filter type of Name and an empty filter string.
     */
    public CardSubset() {
        this.filter = "";
        this.filterType = FilterType.Name;
        this.contained = new ArrayList<>();
        this.applyFilter(this.getSearchSet());
    }

    /**
     * Retrieves the appropriate card set based on the current filter type.
     * @return ArrayList of String arrays representing the filtered set of cards.
     */
    @NonNull
    private ArrayList<String[]> getSearchSet() {
        // Determine which set of cards to return based on the filter type
        switch (this.filterType) {
            case Name: // Return cards sorted by name
                return Card.getCardsByName();
            case Artist: // Return cards sorted by artist
                return Card.getCardsByArtist();
            case Set: // Return cards sorted by set
                return Card.getCardsBySet();
        }
        throw new RuntimeException("Unreachable"); // Unreachable but needed for compilation
    }

    /**
     * Resets the current filter and applies it anew to populate the contained subset.
     */
    private void resetAndFilter() {
        this.contained.clear();
        ArrayList<String[]> searchSet = this.getSearchSet();
        assert searchSet != null;
        for (String[] card : searchSet) {
            String name = card[0];
            if (name.startsWith(this.filter)) {
                this.contained.add(card);
            }
        }
    }

    /**
     * Applies a filter to a list of string arrays and updates the `contained` list with the filtered results.
     *
     * This method performs a binary search on a sorted `searchSet` to find the range of entries that begin with the given filter.
     * It first finds the start of the range where the filter matches, then finds the end of the range, and updates the `contained`
     * list with the entries within that range. If no matching entries are found, the `contained` list is cleared.
     *
     * @param searchSet ArrayList of string arrays where each string array contains elements to be filtered.
     * The list is expected to be sorted based on the first element of each string array.
     */
    private void applyFilter(ArrayList<String[]> searchSet) {
        int wordLength = this.filter.length();
        // Use binary search to find the range of values which begin with the filter
        int low = 0, high = searchSet.size() - 1, rangeStart = -1, rangeEnd = -1;
        // Find start
        while (low <= high) {
            int middle = (low + high) / 2;
            String atMiddle = searchSet.get(middle)[0];
            String substring = atMiddle.substring(0, Integer.min(wordLength, atMiddle.length()));
            if (substring.compareTo(this.filter) > 0) {
                high = middle - 1;
            } else if (substring.compareTo(this.filter) < 0) {
                low = middle + 1;
            } else {
                rangeStart = middle;
                high = middle - 1;
            }
        }
        if (rangeStart == -1) {
            this.contained.clear();
            return;
        }

        // Find end
        low = 0;
        high = searchSet.size() - 1;
        // Perform binary search to find the end of the range
        while (low <= high) {
            int middle = (low + high) / 2;
            String atMiddle = searchSet.get(middle)[0];
            String substring = atMiddle.substring(0, Integer.min(wordLength, atMiddle.length()));
            // Compare the substring with the filter value
            if (substring.compareTo(this.filter) > 0) {
                high = middle - 1;
            } else if (substring.compareTo(this.filter) < 0) {
                low = middle + 1;
            } else {
                rangeEnd = middle;
                low = middle + 1;
            }
        }

        // If no end range is found, clear the contained list and exit
        if (rangeEnd == -1) {
            this.contained.clear();
            return;
        }

        // Create a sublist from the start to the end of the found range
        List<String[]> reducedList = searchSet.subList(rangeStart, rangeEnd);
        this.contained = new ArrayList<>(reducedList);
    }

    /**
     * Gets the subset of cards that match the current filter settings.
     * @return ArrayList of String arrays representing the filtered cards.
     */
    public ArrayList<String[]> getContained() {
        return this.contained;
    }

    /**
     * Sets the filter string and updates the contained subset accordingly.
     * @param newFilter the new filter string to apply.
     */
    public void setFilter(String newFilter) {
        newFilter = Card.cleanName(newFilter);
        if (newFilter.startsWith(this.filter)) {
            this.filter = newFilter;
            this.applyFilter(this.contained);
        } else {
            this.filter = newFilter;
            this.contained.clear();
            this.applyFilter(this.getSearchSet());
        }
    }

    /**
     * Sets the type of filter to apply and resets the filter accordingly.
     * @param filterType the type of filter to apply (Name, Artist, or Set)
     */
    public void setFilterType(FilterType filterType) {
        this.filterType = filterType; // Update the filter type
        this.contained.clear(); // Clears the previously filtered list of cards
        this.applyFilter(this.getSearchSet()); // Applies the filter with the updated filter type
    }
}
