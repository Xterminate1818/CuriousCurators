package com.example.curiouscurators;

import java.util.ArrayList;

public class CardSubset {
    String nameFilter, artistFilter, setFilter;
    boolean ownedFilter;

    ArrayList<String[]> contained;

    public CardSubset(String nameFilter, String artistFilter, String setFilter, boolean ownedFilter) {
        this.nameFilter = nameFilter;
        this.artistFilter = artistFilter;
        this.setFilter = setFilter;
        this.ownedFilter = ownedFilter;
        this.contained = new ArrayList<String[]>();
        this.resetAndFilter();
    }
    public CardSubset() {
        this.nameFilter = "";
        this.artistFilter = "";
        this.setFilter = "";
        this.ownedFilter = false;
        this.contained = new ArrayList<String[]>();
        this.resetAndFilter();
    }

    private void resetAndFilter() {
        this.contained.clear();
        for (String[] card : Card.getCardsByName()) {
            String name = card[0];
            if (name.startsWith(this.nameFilter)) {
                this.contained.add(card);
            }
        }
    }

    private void keepAndFilter() {
        ArrayList<String[]> next = new ArrayList<String[]>();
        for (String[] card : this.getContained()) {
            String name = card[0];
            if (name.startsWith(this.nameFilter)) {
                next.add(card);
            }
        }
        this.contained = next;
    }

    public ArrayList<String[]> getContained() {
        return this.contained;
    }

    public void setNameFilter(String newNameFilter) {
        if (this.nameFilter.startsWith(newNameFilter)) {
            this.nameFilter = newNameFilter;
            this.resetAndFilter();
            System.out.println("reset");
        } else {
            this.nameFilter = newNameFilter;
            this.keepAndFilter();
            System.out.println("keep");
        }
    }

    public void setArtistFilter(String artistFilter) {
        this.artistFilter = artistFilter;
        this.resetAndFilter();
    }

    public void setSetFilter(String setFilter) {
        this.setFilter = setFilter;
        this.resetAndFilter();
    }

    public void setOwnedFilter(boolean ownedFilter) {
        this.ownedFilter = ownedFilter;
        this.resetAndFilter();
    }
}
