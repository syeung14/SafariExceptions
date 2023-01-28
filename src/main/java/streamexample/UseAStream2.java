package streamexample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class UseAStream2 {

    public static void main(String[] args) {
        Stream.of("a.txt", "b.txt", "c.txt")
              .flatMap(fn -> {
                  try {
                      return Files.lines(Path.of(fn));
                  } catch (IOException ioe) {
//                      throw new RuntimeException(ioe);
                      return null;
                  }
              }).forEach(System.out::println);

//        try {
//            Files.lines(Path.of(""));
//        } catch (IOException e) {
//            System.out.println(e);
//            throw new RuntimeException(e); // TODO
//        }
    }
}


@FunctionalInterface
interface ExFunction2<A, R> {
    R apply(A a) throws Throwable;

}