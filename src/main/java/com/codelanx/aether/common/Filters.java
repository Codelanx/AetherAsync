package com.codelanx.aether.common;

import java.util.function.Function;
import java.util.function.Predicate;

public class Filters {

    public static <T, S> Predicate<T> of(Function<T, S> mapper, Predicate<S> filter) {
        return item -> filter.test(mapper.apply(item));
    }
}
