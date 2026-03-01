package com.startechnology.start_core.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StarTStringUtils {

    /**
     * Converts a lower_snake_case string into lowerCamelCase format.
     * <p>
     * Multiple consecutive underscores are treated as a single separator.
     * Leading and trailing underscores are ignored.
     * </p>
     *
     * <pre>
     * Examples:
     *   "user_name"        -> "userName"
     *   "first__last"      -> "firstLast"
     *   "_private_value"   -> "privateValue"
     * </pre>
     *
     * @param str the input string in lower_snake_case format; must not be {@code null}
     * @return the converted string in lowerCamelCase format
     * @throws NullPointerException if {@code str} is {@code null}
     */
    public static String snakeCaseToCamelCase(String str) {
        return Arrays.stream(str.split("_+"))
                .filter(s -> !s.isEmpty())
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining())
                .replaceFirst("^[A-Z]",
                        String.valueOf(Character.toLowerCase(str.charAt(0))));
    }

    /**
     * Converts a lower_snake_case string into sentence-style format.
     * <p>
     * Each word is separated by a space and capitalized.
     * Fully uppercase words (e.g., acronyms) are preserved.
     * Multiple consecutive underscores are treated as a single separator.
     * </p>
     *
     * <pre>
     * Examples:
     *   "user_name"          -> "User Name"
     *   "first__last"        -> "First Last"
     * </pre>
     *
     * @param str the input string in lower_snake_case format; must not be {@code null}
     * @return the converted string in sentence-style format
     * @throws NullPointerException if {@code str} is {@code null}
     */
    public static String snakeCaseToSentence(String str) {
        return Arrays.stream(str.split("_+"))
                .filter(s -> !s.isEmpty())
                .map(s -> s.equals(s.toUpperCase()) ? s : Character.toUpperCase(s.charAt(0)) +
                        s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
