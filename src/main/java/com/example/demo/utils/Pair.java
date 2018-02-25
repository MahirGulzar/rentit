package com.example.demo.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<F,S> {
    F first;
    S second;
    @Override public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Pair)) return false;
        Pair other = (Pair) o;
        return first.equals(other.first) && second.equals(other.second);
    }
}