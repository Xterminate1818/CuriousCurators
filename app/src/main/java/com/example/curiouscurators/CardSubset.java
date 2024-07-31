package com.example.curiouscurators;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CardSubset {
    public enum FilterType {
        Name, Artist, Set
    }
    String filter;
    FilterType filterType;
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
        switch (this.filterType) {
            case Name:
                return Card.getCardsByName();
            case Artist:
                return Card.getCardsByArtist();
            case Set:
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
     * Applies the current filter without clearing the contained subset.
     */
    private void applyFilter(ArrayList<String[]> searchSet) {
        ArrayList<String[]> next = new ArrayList<>();
        System.out.println(searchSet.size());
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
        while (low <= high) {
            int middle = (low + high) / 2;
            String atMiddle = searchSet.get(middle)[0];
            String substring = atMiddle.substring(0, Integer.min(wordLength, atMiddle.length()));
            if (substring.compareTo(this.filter) > 0) {
                high = middle - 1;
            } else if (substring.compareTo(this.filter) < 0) {
                low = middle + 1;
            } else {
                rangeEnd = middle;
                low = middle + 1;
            }
        }

        if (rangeEnd == -1) {
            this.contained.clear();
            return;
        }

        List<String[]> reducedList = searchSet.subList(rangeStart, rangeEnd);

        for (String[] card : reducedList) {
            String name = card[0];
            if (name.startsWith(this.filter)) {
                next.add(card);
            }
        }
        this.contained = next;
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
        this.filterType = filterType;
        this.contained.clear();
        this.applyFilter(this.getSearchSet());
    }
}
