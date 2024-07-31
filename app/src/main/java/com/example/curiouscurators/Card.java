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

/**
 * Abstract base class representing a generic card.
 * This class provides shared attributes and functionality for all types of cards.
 */
public abstract class Card {
    // Whether the card dataset has been loaded
    private static boolean initialized = false;
    // Card objects indexed by global ID
    private static final HashMap<String, Card> cardsById = new HashMap<>();
    // Sorted table of {name, globalId}
    private static final ArrayList<String[]> cardsByName = new ArrayList<>();
    // Sorted table of {illustrator, globalId}
    private static final ArrayList<String[]> cardsByArtist = new ArrayList<>();
    // Sorted table of {illustrator, globalId}
    private static final ArrayList<String[]> cardsBySet= new ArrayList<>();

    private static final HashMap<String, Drawable> setLogos = new HashMap<>();

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

    /**
     * Constructs a Card object from a JSON structure.
     * @param js the JSON object containing card data
     * @throws Json.ParsingException if JSON parsing fails
     */
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

    /**
     * Initializes the card database by loading card data from JSON.
     * @param context the application context
     */
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
                setNames.recycle();
                setLogos.recycle();
            } catch (IOException | Json.ParsingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Cleans a card name by removing punctuation and converting to lower case.
     * @param in the original card name
     * @return the cleaned card name
     */
    public static String cleanName(String in) {
        return in.strip()
                .toLowerCase()
                .replaceAll("'", "")
                .replaceAll("\"", "")
                .replaceAll("\\.", "");
    }

    /**
     * Retrieves a card by its ID.
     * @param id the global ID of the card
     * @return the Card object
     */
    public static Card getCardById(String id) {
        if (!Card.initialized) {
            throw new RuntimeException("Cards not initialized. Do `Card.initialize()` first.");
        }
        return cardsById.get(id);
    }

    /**
     * Retrieves the logo associated with a set ID.
     * @param id the set ID
     * @return the Drawable logo
     */
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

    /**
     * Retrieves a list of cards sorted by name.
     * @return the list of cards
     */
    public static ArrayList<String[]> getCardsByName() {
        if (!Card.initialized) {
            throw new RuntimeException("Cards not initialized. Do `Card.initialize()` first.");
        }
        return cardsByName;
    }

    /**
     * Retrieves a list of cards sorted by artist.
     * @return the list of cards
     */
    public static ArrayList<String[]> getCardsByArtist() {
        if (!Card.initialized) {
            throw new RuntimeException("Cards not initialized. Do `Card.initialize()` first.");
        }
        return cardsByArtist;
    }

    /**
     * Retrieves a list of cards sorted by set.
     * @return the list of cards
     */
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

    /**
     * Represents a specific type of card: Energy.
     * These cards provide energy necessary for Pokemon to perform actions.
     */
    public static class Energy extends Card {
        private final String effect, type;

        /**
         * Constructs an Energy card with specific attributes related to energy effects.
         * @param js the JSON object containing energy card data
         * @throws Json.ParsingException if JSON parsing fails
         */
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

    /**
     * Represents a Pokemon card, detailing a creature with various attributes.
     */
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

        /**
         * Constructs a Pokemon card with attributes related to the creature and its abilities.
         * @param js the JSON object containing Pokemon card data
         * @throws Json.ParsingException if JSON parsing fails
         */
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

        /**
         * Inner class representing an attack associated with a Pokemon card.
         */
        public static class Attack {
            private final ArrayList<String> cost;
            private final String name, effect, damage;

            /**
             * Constructs an Attack with specific attributes like cost and effect.
             * @param js the JSON object containing attack data
             */
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

    /**
     * Represents a Trainer card, providing special effects or actions within the game.
     */
    public static class Trainer extends Card {
        private final String effect, type;

        /**
         * Constructs a Trainer card with attributes specific to game mechanics.
         * @param js the JSON object containing trainer card data
         * @throws Json.ParsingException if JSON parsing fails
         */
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
