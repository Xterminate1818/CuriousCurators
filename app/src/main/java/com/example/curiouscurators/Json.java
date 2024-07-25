package com.example.curiouscurators;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

public abstract class Json {
    HashMap<String, Json> children;

    public static Json fromString(String in) throws ParsingException {
        return parse(in).head;
    }

    public Json get(String key) {
        Json child = children.get(key);
        if (child == null) {
            child = Value.nil();
        }
        return child;
    }

    public Collection<Json> getAll() {
        return children.values();
    }


    public String value() {
        return toString();
    }

    public String valueOrDefault(String _default) {
        String value = this.value();
        if (value.isEmpty()) {
            return _default;
        } else {
            return value;
        }
    }

    @NonNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (String key : this.children.keySet()) {
            sb.append(key);
            sb.append(":");
            sb.append(this.children.get(key).toString());
        }
        sb.append("}");
        return sb.toString();
    }

    Json() {
        this.children = new HashMap<String, Json>();
    }

    static Parse<Json, String> parse(String in) throws ParsingException {
        if (in.isEmpty()) {
            throw new ParsingException(in);
        }
        char first = in.charAt(0);
        if (first == '{') {
            return Map.parse(in);
        } else if (first == '[') {
            return Array.parse(in);
        }
        else {
            return Value.parse(in);
        }
    }

    protected void addChild(String key, Json value) {
        this.children.put(key, value);
    }

    private static class Value extends Json {
        private final String value;

        private Value(String value) {super(); this.value=value;}

        static Json nil() {
            return new Value("");
        }

        static Parse<Json, String> parse(String in) {
            Parse<String, String> tup = Parse.parseString(in);
            return new Parse<Json, String>(new Value(tup.head), tup.tail);
        }

        @NonNull
        public String toString() {
            return value;
        }
    }

    private static class Map extends Json {
        private Map() { super(); }
        static Parse<Json, String> parse(String in) throws ParsingException {
            Map map = new Map();
            Parse<Boolean, String> t1;
            Parse<String, String> t2;
            Parse<Json, String> t3;
            t1 = Parse.parseChar(in, '{');
            if (!t1.head) {
                throw new ParsingException(in);
            }
            String contents = t1.tail;
            while (!contents.isEmpty() && contents.charAt(0) != '}') {
                // Expects format:
                // key : value,
                // where key is a (possibly quoted) literal and value is any json blob.
                // Note: there is no trailing comma allowed after the last element
                contents = Parse.parseWhitespace(contents).tail;
                t2 = Parse.parseString(contents);
                String key = t2.head;
                contents = t2.tail;
                contents = Parse.parseWhitespace(contents).tail;
                t1 = Parse.parseChar(contents, ':');
                if (!t1.head) {
                    throw new ParsingException(in);
                }
                contents = t1.tail;
                contents = Parse.parseWhitespace(contents).tail;
                t3 = Json.parse(contents);
                map.addChild(key, t3.head);
                contents = t3.tail;
                contents = Parse.parseWhitespace(contents).tail;
                t1 = Parse.parseChar(contents, ',');
                contents = t1.tail;
                if (!t1.head) {
                    break;
                }
            }
            t1 = Parse.parseChar(contents, '}');
            if (!t1.head) {
                throw new ParsingException(in);
            }
            contents = t1.tail;
            return new Parse<Json, String>(map, contents);
        }
    }

    private static class Array extends Json {
        private Array() { super(); }
        static Parse<Json, String> parse(String in) throws ParsingException {
            Array array = new Array();
            Parse<Boolean, String> t1;
            Parse<String, String> t2;
            Parse<Json, String> t3;
            t1 = Parse.parseChar(in, '[');
            if (!t1.head) {
                throw new ParsingException(in);
            }
            String contents = t1.tail;
            int index = 0;
            while (!contents.isEmpty() && contents.charAt(0) != ']') {
                // Expects format:
                // [a, b, c]
                contents = Parse.parseWhitespace(contents).tail;
                t3 = Json.parse(contents);
                array.addChild(Integer.toString(index), t3.head);
                index += 1;
                contents = t3.tail;
                contents = Parse.parseWhitespace(contents).tail;
                t1 = Parse.parseChar(contents, ',');
                contents = t1.tail;
                if (!t1.head) {
                    break;
                }
            }
            t1 = Parse.parseChar(contents, ']');
            if (!t1.head) {
                throw new ParsingException(in);
            }
            contents = t1.tail;
            return new Parse<Json, String>(array, contents);
        }

        @NonNull
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (Json value : this.children.values()) {
                sb.append(value.toString());
                sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        }
    }
    public static class ParsingException extends Exception {
        ParsingException(String chokedOn) {
            super(chokedOn);
        }
    }

    private static class Parse<A, B> {

        private static final char[] WHITESPACE = {' ', '\r', '\n', '\t'};

        public final A head;
        public final B tail;

        public Parse(A a, B b) {
            this.head = a;
            this.tail = b;
        }

        private static boolean isWhitespace(char test) {
            for (char c : WHITESPACE) {
                if (c == test) {
                    return true;
                }
            }
            return false;
        }

        private static boolean isAlphaNumeric(char test) {
            return ((test >= 'a' && test <= 'z') ||
                    (test >= 'A' && test <= 'Z') ||
                    (test >= '0' && test <= '9'));
        }

        public static Parse<String, String> parseWhitespace(String in) {
            int index = 0;
            for (int i = 0; i < in.length(); i++) {
                char character = in.charAt(i);
                if (isWhitespace(character)) {
                    index = i + 1;
                } else {
                    break;
                }
            }

            return new Parse<>(in.substring(0, index), in.substring(index));
        }

        public static Parse<Boolean, String> parseChar(String in, char test) {
            if (!in.isEmpty() && in.charAt(0) == test) {
                return new Parse<>(true, in.substring(1));
            } else {
                return new Parse<>(false, in);
            }
        }

        // Consume characters inside quotes
        public static Parse<String, String> parseQuotedString(String in) {
            if (in.isEmpty()) {
                return new Parse<>("", in);
            }

            char quote_type = ' ';
            if (in.charAt(0) == '"') {
                quote_type = '"';
            } else if (in.charAt(0) == '\'') {
                quote_type = '\'';
            } else {
                return new Parse<>("", in);
            }

            int index = 0;
            for (int i = 1; i < in.length(); i++) {
                char character = in.charAt(i);
                index = i;
                // Skip escape sequences
                if (character == '\\') {
                    i += 2;
                    continue;
                }
                if (character == quote_type) {
                    break;
                }
            }
            return new Parse<>(in.substring(1, index), in.substring(index + 1));
        }


        public static Parse<String, String> parseString(String in) {
            if (!in.isEmpty() && (in.charAt(0) == '\'' || in.charAt(0) == '"')) {
                return parseQuotedString(in);
            }
            int index = 0;
            for (int i = 1; i < in.length(); i++) {
                char character = in.charAt(i);
                index = i;
                if (!isAlphaNumeric(character)) {
                    break;
                }
            }
            return new Parse<>(in.substring(0, index), in.substring(index));
        }
        public static Parse<String, String> parseKey(String in) {
            int dot = in.indexOf('.');
            if (dot == -1) {
                dot = in.length();
            }
            return new Parse<>(in.substring(0, dot), in.substring(dot));
        }
    }
}
