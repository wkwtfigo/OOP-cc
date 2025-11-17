package org.example.runtime;

import java.util.ArrayList;

/**
 * Helper methods that simplify code generation for built-in O language types.
 * These utilities are used by the {@code CodeGenerator} to avoid emitting
 * complex bytecode sequences for common list/array operations.
 */
public final class BuiltinSupport {

    private BuiltinSupport() {
    }

    /**
     * Returns a shallow copy of the provided list that omits the first element.
     * If the source list contains zero or one element, an empty list is returned.
     */
    public static ArrayList<Object> tail(ArrayList<Object> source) {
        if (source == null || source.size() <= 1) {
            return new ArrayList<>();
        }
        return new ArrayList<>(source.subList(1, source.size()));
    }

    /**
     * Creates an {@link ArrayList} with the specified length that is pre-filled
     * with {@code null} values. This is used to model the O language {@code Array}
     * type semantics where all elements exist immediately after construction.
     */
    public static ArrayList<Object> createArray(int length) {
        ArrayList<Object> array = new ArrayList<>(Math.max(length, 0));
        for (int i = 0; i < length; i++) {
            array.add(null);
        }
        return array;
    }
}

