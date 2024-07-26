package com.example.curiouscurators;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Card {
    // Whether the card dataset has been loaded
    private static boolean initialized = false;
    // Global variable containing the card dataset
    private static HashMap<String, Card> cards = new HashMap<String, Card>();

    protected final String id,
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
        this.id = js.get("id").value();
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

    // Get card list, and read it from memory if we haven't already
    public static HashMap<String, Card> getCards(Context context) {
        // Load cards if not initialized
        if (!Card.initialized) {
            try {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("cards.json"))
                );
                String line;
                while ((line = reader.readLine()) != null) {
                    Card c = Card.fromString(line);
                    Card.cards.put(c.id, c);
                }
                Card.initialized = true;
            } catch (IOException | Json.ParsingException e) {
                throw new RuntimeException(e);
            }
        }
        return cards;
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
