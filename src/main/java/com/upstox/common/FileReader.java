package com.upstox.common;

import java.util.stream.Stream;

public interface FileReader<T> {
    Stream<T> readAll();
    Stream<T> readAllOutside();
}
