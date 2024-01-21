package sideeffectsbad;

import java.util.stream.Stream;

public class PeekProblem {
    public static void main(String[] args) {
        int[] count = {0};
        long itemCount = Stream.of(1, 2, 3, 4)
                .peek(i -> System.out.println("I see " + i))
                .map(i -> {
                    count[0]++;
                    return i;
                })
                .filter(i -> true)
                .count();
//                .forEach(i -> {});
        System.out.println("I counted " + itemCount + " items and count is " + count[0]);

    }
}
