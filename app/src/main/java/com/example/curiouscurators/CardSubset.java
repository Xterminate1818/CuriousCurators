package com.example.curiouscurators;

import java.util.ArrayList;

public class CardSubset {
    public enum FilterType {
        Name, Artist, Set
    }
    String filter;
    FilterType filterType;
    ArrayList<String[]> contained;

    public CardSubset() {
        this.filter = "";
        this.filterType = FilterType.Name;
        this.contained = new ArrayList<String[]>();
        this.resetAndFilter();
    }
    private ArrayList<String[]> getSearchSet() {
        switch (this.filterType) {
            case Name:
                return Card.getCardsByName();
            case Artist:
                return Card.getCardsByArtist();
            case Set:
                return Card.getCardsBySet();
        }
        // Unreachable
        return null;
    }

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

    public ArrayList<String[]> getContained() {
        return this.contained;
    }

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

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
        this.resetAndFilter();
    }
}
