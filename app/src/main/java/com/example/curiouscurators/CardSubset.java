package com.example.curiouscurators;

import java.util.ArrayList;

/**
 * Represents a subset of cards based on specified filters.
 * This class allows filtering by name, artist, or set.
 */
public class CardSubset {
    /**
     * Enum to specify the type of filter applied to the card subset.
     */
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
        this.contained = new ArrayList<String[]>();
        this.resetAndFilter();
    }

    /**
     * Retrieves the appropriate card set based on the current filter type.
     * @return ArrayList of String arrays representing the filtered set of cards.
     */
    private ArrayList<String[]> getSearchSet() {
        switch (this.filterType) {
            case Name:
                return Card.getCardsByName();
            case Artist:
                return Card.getCardsByArtist();
            case Set:
                return Card.getCardsBySet();
        }
        return null; // Unreachable but needed for compilation
    }

    /**
     * Resets the current filter and applies it anew to populate the contained subset.
     */
    private void resetAndFilter() {
        System.out.println("reset");
        this.contained.clear();
        ArrayList<String[]> searchSet = this.getSearchSet();
        for (String[] card : searchSet) {
            String name = card[0];
            if (name.startsWith(this.filter)) {
                this.contained.add(card);
            }
        }
    }

    /**
     * Applies the current filter without clearing the contained subset.
     * Used when the filter is adjusted to be more specific.
     */
    private void keepAndFilter() {
        System.out.println("keep");
        ArrayList<String[]> next = new ArrayList<String[]>();
        ArrayList<String[]> searchSet = this.getSearchSet();
        for (String[] card : searchSet) {
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
     * @param newFilter the new filter string to apply
     */
    public void setFilter(String newFilter) {
        newFilter = Card.cleanName(newFilter);
        if (newFilter.startsWith(this.filter)) {
            this.filter = newFilter;
            this.keepAndFilter();
        } else {
            this.filter = newFilter;
            this.resetAndFilter();
        }
    }

    /**
     * Sets the type of filter to apply and resets the filter accordingly.
     * @param filterType the type of filter to apply (Name, Artist, or Set)
     */
    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
        this.resetAndFilter();
    }
}
