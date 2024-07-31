package com.example.curiouscurators;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public abstract class Card {
    // Whether the card dataset has been loaded
    private static boolean initialized = false;
    // Card objects indexed by global ID
    private static HashMap<String, Card> cardsById = new HashMap<String, Card>();
    // Sorted table of {name, globalId}
    private static ArrayList<String[]> cardsByName = new ArrayList<String[]>();
    // Sorted table of {illustrator, globalId}
    private static ArrayList<String[]> cardsByArtist = new ArrayList<String[]>();
    // Sorted table of {illustrator, globalId}
    private static ArrayList<String[]> cardsBySet= new ArrayList<String[]>();

    private static HashMap<String, Drawable> setLogos = new HashMap<String, Drawable>();

    public final String globalId,
            localId,
            name,
            image,
            category,
            illustrator,
            rarity,
            setId,
            setName,
            setLogo,
            setSymbol;

    public Card(Json js) throws Json.ParsingException {
        this.globalId = js.get("id").value();
        this.localId = js.get("localId").value();
        this.name = js.get("name").value();
        this.image = js.get("image").valueOrDefault("");
        this.category = js.get("category").value();
        this.illustrator = js.get("illustrator").valueOrDefault("Unknown");
        this.rarity = js.get("rarity").valueOrDefault("None");
        this.setId = js.get("set").get("id").value();
        this.setName = js.get("set").get("name").value();
        this.setLogo = js.get("set").get("logo").value();
        this.setSymbol = js.get("set").get("symbol").value();
    }

    static public Card fromString(String in) throws Json.ParsingException {
        Json js = Json.fromString(in);
        String category = js.get("category").value();
        switch (category) {
            case "Pokemon":
                return new Pokemon(js);
            case "Energy":
                return new Energy(js);
            case "Trainer":
                return new Trainer(js);
            default:
                throw new Json.ParsingException(in);
        }
    }

    public static void initialize(Context context) {
        if (!Card.initialized) {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(context.getAssets().open("cards.json"))
                );
                // Read and parse cards
                String line;
                while ((line = reader.readLine()) != null) {
                    Card c = Card.fromString(line);
                    Card.cardsById.put(c.globalId, c);
                    Card.cardsByName.add(new String[] {cleanName(c.name), c.globalId});
                    Card.cardsByArtist.add(new String[] {cleanName(c.illustrator), c.globalId});
                    Card.cardsBySet.add(new String[] {cleanName(c.setName), c.globalId});
                }
                // Sort cards by name
                Card.cardsByName.sort(new Comparator<String[]>() {
                    @Override
                    public int compare(String[] s1, String[] s2) {
                        return s1[0].compareTo(s2[0]);
                    }
                });
                // Sort cards by artist
                Card.cardsByArtist.sort(new Comparator<String[]>() {
                    @Override
                    public int compare(String[] s1, String[] s2) {
                        return s1[0].compareTo(s2[0]);
                    }
                });
                // Sort cards by set
                Card.cardsByArtist.sort(new Comparator<String[]>() {
                    @Override
                    public int compare(String[] s1, String[] s2) {
                        return s1[0].compareTo(s2[0]);
                    }
                });
                TypedArray setNames = context.getResources().obtainTypedArray(R.array.setNames);
                TypedArray setLogos = context.getResources().obtainTypedArray(R.array.setLogos);
                for (int i = 0; i < setNames.length(); i++) {
                    String name = setNames.getString(i);
                    int logoId = setLogos.getResourceId(i, -1);
                    Drawable logo = ContextCompat.getDrawable(context, logoId);
                    Card.setLogos.put(name, logo);
                }
                Card.initialized = true;
            } catch (IOException | Json.ParsingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Strips punctuation and capitalization for easier searching
    public static String cleanName(String in) {
        return in.strip()
                .toLowerCase()
                .replaceAll("'", "")
                .replaceAll("\\.", "");
    }

    // Get card hashmap, throw exception if not initialized
    public static Card getCardById(String id) {
        if (!Card.initialized) {
            throw new RuntimeException("Cards not initialized. Do `Card.initialize()` first.");
        }
        return cardsById.get(id);
    }

    public static Drawable getLogoById(String id) {
        if (!Card.initialized) {
            throw new RuntimeException("Cards not initialized. Do `Card.initialize()` first.");
        }
        Drawable d = setLogos.get(id);
        if (d == null) {
            return setLogos.get("base1");
        } else {
            return d;
        }
    }

    // Get card sorted list, throw exception if not initialized
    public static ArrayList<String[]> getCardsByName() {
        if (!Card.initialized) {
            throw new RuntimeException("Cards not initialized. Do `Card.initialize()` first.");
        }
        return cardsByName;
    }

    // Get card sorted list, throw exception if not initialized
    public static ArrayList<String[]> getCardsByArtist() {
        if (!Card.initialized) {
            throw new RuntimeException("Cards not initialized. Do `Card.initialize()` first.");
        }
        return cardsByArtist;
    }

    // Get card sorted list, throw exception if not initialized
    public static ArrayList<String[]> getCardsBySet() {
        if (!Card.initialized) {
            throw new RuntimeException("Cards not initialized. Do `Card.initialize()` first.");
        }
        return cardsBySet;
    }

    @NonNull
    public String toString() {
        return this.setId + " " + this.setName + " " + this.setLogo;
    }

    public static class Energy extends Card {
        private final String effect, type;

        public Energy(Json js) throws Json.ParsingException {
            super(js);
            this.effect = js.get("effect").value();
            this.type = js.get("energyType").value();
        }

        @NonNull
        public String toString() {
            return this.type + " " + this.effect;
        }
    }

    public static class Pokemon extends Card {
        private final String evolveFrom,
                description,
                stage,
                suffix,
                itemName,
                itemEffect,
                regulationMark;
        private final int hp, level, retreat;
        ArrayList<String> types;
        ArrayList<Attack> attacks;

        public Pokemon(Json js) throws Json.ParsingException {
            super(js);
            this.evolveFrom = js.get("evolveFrom").valueOrDefault("None");
            this.description = js.get("description").valueOrDefault("");
            this.stage = js.get("stage").valueOrDefault("");
            this.suffix = js.get("suffix").valueOrDefault("");
            this.itemName = js.get("item").get("name").valueOrDefault("");
            this.itemEffect = js.get("item").get("effect").valueOrDefault("");
            this.regulationMark = js.get("regulationMark").valueOrDefault("N/A");
            this.hp = Integer.parseInt(js.get("hp").valueOrDefault("-1"));
            this.level = Integer.parseInt(js.get("level").valueOrDefault("-1"));
            this.retreat = Integer.parseInt(js.get("retreat").valueOrDefault("-1"));
            this.types = new ArrayList<String>();
            for (Json typeJson : js.get("types").getAll()) {
                String type = typeJson.value();
                types.add(type);
            }
            this.attacks = new ArrayList<Attack>();
            for (Json attackJson: js.get("attacks").getAll()) {
                Attack attack = new Attack(attackJson);
                this.attacks.add(attack);
            }
        }

        @NonNull
        public String toString() {
            return this.hp + " " + this.types;
        }
        public static class Attack {
            private final ArrayList<String> cost;
            private final String name, effect, damage;

            public Attack(Json js) {
                this.cost = new ArrayList<String>();
                for (Json costJson: js.get("cost").getAll()) {
                    String c = costJson.value();
                    this.cost.add(c);
                }
                this.name = js.get("name").value();
                this.effect = js.get("effect").valueOrDefault("");
                this.damage = js.get("damage").valueOrDefault("0");
            }
        }
    }

    public static class Trainer extends Card {
        private final String effect, type;

        protected Trainer(Json js) throws Json.ParsingException {
            super(js);
            this.effect = js.get("effect").value();
            this.type = js.get("trainerType").value();
        }

        @NonNull
        public String toString() {
            return this.type + " " + this.effect;
        }
    }
}
