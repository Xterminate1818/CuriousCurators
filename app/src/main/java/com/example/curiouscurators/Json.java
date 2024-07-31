package com.example.curiouscurators;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Represents a JSON object with functionalities to parse from string,
 * retrieve properties, and handle different JSON structures such as maps and arrays.
 */
public abstract class Json {
    HashMap<String, Json> children;

    /**
     * Parses a JSON string into a Json object.
     * @param in JSON string to be parsed.
     * @return Json object representing the parsed JSON string.
     * @throws ParsingException if the input string is not a valid JSON.
     */
    public static Json fromString(String in) throws ParsingException {
        return parse(in).head;
    }

    /**
     * Retrieves a Json child by key.
     * @param key The key of the child to retrieve.
     * @return Json child or a nil Json object if the key does not exist.
     */
    public Json get(String key) {
        Json child = children.get(key);
        if (child == null) {
            child = Value.nil();
        }
        return child;
    }

    /**
     * Retrieves all children of the current Json object.
     * @return Collection of Json children.
     */
    public Collection<Json> getAll() {
        return children.values();
    }

    /**
     * Retrieves the string representation of the Json object.
     * @return String value of this Json object.
     */
    public String value() {
        return toString();
    }

    /**
     * Retrieves the string representation of the Json object or default value if empty.
     * @param _default Default value to return if the Json object has an empty value.
     * @return String value of this Json or the default value if Json value is empty.
     */
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

    /**
     * Default constructor initializing children of this Json object.
     */
    Json() {
        this.children = new HashMap<String, Json>();
    }

    /**
     * Parses a JSON string and returns a Json object along with unparsed remainder of the string.
     * @param in JSON string to parse.
     * @return A parse containing the Json object and any remaining unparsed string.
     * @throws ParsingException if the string cannot be parsed into any known Json structure.
     */
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

    /**
     * Adds a child Json object to this object with the specified key.
     * @param key The key under which the child is added.
     * @param value The Json object to add as a child.
     */
    protected void addChild(String key, Json value) {
        this.children.put(key, value);
    }

    /**
     * Handles the parsing and represents a basic JSON value (non-composite type).
     */
    private static class Value extends Json {
        private final String value;

        /**
         * Constructor for creating a Json value.
         * @param value The string value of this Json object.
         */
        private Value(String value) {super(); this.value=value;}

        /**
         * Returns a nil Json value.
         * @return Json object representing a nil value.
         */
        static Json nil() {
            return new Value("");
        }

        /**
         * Parses a JSON value from a string.
         * @param in String to parse.
         * @return Parse object containing the created Json value and the remaining string.
         */
        static Parse<Json, String> parse(String in) {
            Parse<String, String> tup = Parse.parseString(in);
            return new Parse<Json, String>(new Value(tup.head), tup.tail);
        }

        @NonNull
        public String toString() {
            return value;
        }
    }

    /**
     * Represents a JSON object structure as a map from keys to Json objects
     */
    private static class Map extends Json {
        private Map() { super(); }

        /**
         * Parses a string into a JSON Map object.
         * @param in the string to parse
         * @return a Parse object containing the Map and the remainder of the string
         * @throws ParsingException if the input string does not properly represent a JSON Map
         */
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
                /**
                 * Expects a JSON object with key-value pairs where:
                 * Key is a (possibly quoted) literal string.
                 * Value can be any valid JSON data type.
                 * Note: there is no trailing comma allowed after the last element
                 */
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

    /**
     * Represents a JSON array as a list of Json objects.
     */
    private static class Array extends Json {
        private Array() { super(); }

        /**
         * Parses a string into a JSON Array object.
         * @param in the string to parse
         * @return a Parse object containing the Array and the remainder of the string
         * @throws ParsingException if the input string does not properly represent a JSON Array
         */
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

    /**
     * Custom exception type for JSON parsing errors.
     */
    public static class ParsingException extends Exception {
        ParsingException(String chokedOn) {
            super(chokedOn);
        }
    }

    /**
     * A utility class to handle parsing operations.
     * @param <A> Type of the object that is parsed.
     * @param <B> Type of the remaining part after parsing.
     */
    private static class Parse<A, B> {

        private static final char[] WHITESPACE = {' ', '\r', '\n', '\t'};

        public final A head;
        public final B tail;

        /**
         * Constructs a new Parse object with parsed head and remaining tail.
         * @param a the parsed object
         * @param b the remaining string after parsing
         */
        public Parse(A a, B b) {
            this.head = a;
            this.tail = b;
        }

        /**
         * Checks if a character is whitespace.
         * @param test the character to check
         * @return true if the character is a whitespace; otherwise, false.
         */
        private static boolean isWhitespace(char test) {
            for (char c : WHITESPACE) {
                if (c == test) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Checks if a character is alphanumeric, i.e., a letter (a-z, A-Z) or a digit (0-9).
         *
         * @param test The character to test.
         * @return true if the character is alphanumeric, false otherwise.
         */
        private static boolean isAlphaNumeric(char test) {
            return ((test >= 'a' && test <= 'z') ||
                    (test >= 'A' && test <= 'Z') ||
                    (test >= '0' && test <= '9'));
        }

        /**
         * Parses leading whitespace characters from the input string and returns
         * the whitespace substring and the remainder of the input.
         *
         * @param in The input string to parse.
         * @return A Parse object containing the parsed whitespace and the remainder of the string.
         */
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

        /**
         * Parses the specified character from the beginning of the input string if it matches.
         *
         * @param in   The input string to parse.
         * @param test The character to match at the start of the string.
         * @return A Parse object containing a boolean indicating success or failure,
         *         and the remainder of the string after the matched character.
         */
        public static Parse<Boolean, String> parseChar(String in, char test) {
            if (!in.isEmpty() && in.charAt(0) == test) {
                return new Parse<>(true, in.substring(1));
            } else {
                return new Parse<>(false, in);
            }
        }

        /**
         * Parses a quoted string (enclosed within either single or double quotes) from the input string.
         * Handles escaped characters within the quotes.
         *
         * @param in The input string to parse.
         * @return A Parse object containing the extracted string and the remainder of the input after the closing quote.
         */
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
                if (character == '\\') { // Handle escape sequences
                    i += 2;
                    continue;
                }
                if (character == quote_type) {
                    break;
                }
            }
            return new Parse<>(in.substring(1, index), in.substring(index + 1));
        }

        /**
         * Parses an alphanumeric string from the input.
         *
         * @param in The input string to parse.
         * @return A Parse object containing the parsed alphanumeric string and the remainder of the input.
         */
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

        /**
         * Parses a key from the input string up to the next dot or the end of the string.
         *
         * @param in The input string to parse.
         * @return A Parse object containing the key and the remainder of the input after the dot.
         */
        public static Parse<String, String> parseKey(String in) {
            int dot = in.indexOf('.');
            if (dot == -1) {
                dot = in.length();
            }
            return new Parse<>(in.substring(0, dot), in.substring(dot));
        }
    }
}
