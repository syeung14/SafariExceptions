package streamstuff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

interface ExFunction1<A, R> {
    R apply(A a) throws Throwable;
}


public class Example {
//    public static SimplefunctionthatreportsfailureusingOptional wrap(Functionthatreportsfailurebyexception op){}
    public static <A, R> Function<A, Optional<R>> wrap(ExFunction1<A, R> op) {
        return a -> {
            try {
                return Optional.of(op.apply(a));
            } catch (Throwable t) {
                return Optional.empty();
            }
        };
    }

//    public static Stream<String> getFileContents(String fn) {
//        try {
//            return Files.lines(Path.of(fn));
//        } catch (Throwable t) {
//            throw new RuntimeException(t);
//        }
//    }

//    public static Optional<Stream<String>> getFileContents(String fn) {
//        try {
//            return Optional.of(Files.lines(Path.of(fn)));
//        } catch (Throwable t) {
//            return Optional.empty();
//        }
//    }
    public static void main(String[] args) {
        Function<String, Optional<Stream<String>>> getFileContents =
                wrap(fn -> Files.lines(Path.of(fn)));

        Stream.of("a.txt", "b.txt", "c.txt")
//                .map(s -> s.toUpperCase())
//                .flatMap(fn -> Example.getFileContents(fn)) // works for stream return
//                .map(fn -> Example.getFileContents(fn))
                .map(getFileContents)
                // indicate it worked, here's the result, OR it broke, here's why
                .peek(opt -> {
                    if (opt.isEmpty()) {
                        System.out.println("***There was a problem!!!");
                    }
                })
                .filter(opt -> opt.isPresent())
                .flatMap(opt -> opt.get())
                .forEach(s -> System.out.println(s));
    }
}

/*
something that might be present, or might not:
 - null vs non-null
 - array containing either zero, or one item!
 - List? of zero or one
 - Stream of zero or one (Monad-like -- i.e. map, flatMap, filter, foreach)
 - Optional - like a Stream for spefically ZERO OR ONE item
 */