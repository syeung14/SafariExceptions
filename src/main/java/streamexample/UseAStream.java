package streamexample;

import ansi.Colors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

// FunctionReportingProblemOrSuccessInOneObject wrap(
// FunctionReportingProblemWithException f)

@FunctionalInterface
interface ExFunction<A, R> {
  R apply(A a) throws Throwable;

  static <A, R> Function<A, Optional<R>> wrap(ExFunction<A, R> op) {
    return a -> {
      try {
        return Optional.of(op.apply(a));
      } catch (Throwable t) {
        return Optional.empty();
      }
    };
  }
}

public class UseAStream {
//  public static Stream<String> getFileContents(String fn) {
//    try {
//      return Files.lines(Path.of(fn));
//    } catch (IOException ioe) {
////      throw new RuntimeException(ioe);
////      return null; // NO NO NO, yuk, can't believe this even works!
//      System.out.println(Colors.RED + "File not found: " + ioe.getMessage() + Colors.RESET);
//      return Stream.empty();
//    }
//  }

//  public static Optional<Stream<String>> getFileContents(String fn) {
//    try {
//      return Optional.of(Files.lines(Path.of(fn)));
//    } catch (IOException ioe) {
////      throw new RuntimeException(ioe);
////      return null; // NO NO NO, yuk, can't believe this even works!
//      System.out.println(Colors.RED + "File not found: " + ioe.getMessage() + Colors.RESET);
//      return Optional.empty();
//    }
//  }


  public static void main(String[] args) {
    Stream.of("a.txt", "e.txt", "c.txt")
          // one input filename -> many String lines
          //        .flatMap(UseAStream::getFileContents) // works when result is Stream.
          //        .map(UseAStream::getFileContents)

          .map(ExFunction.wrap(fn -> Files.lines(Path.of(fn))))
          // do something with "missing"
          .flatMap(item -> Stream.of(
              item,
              Optional.of(Stream.of("-----------------"))
          ))
          .peek(opt -> {
            if (opt.isEmpty()) {
              System.out.println(Colors.CYAN + "File was missing!" + Colors.RESET);
            }
          })

          .filter(Optional::isPresent)
          .flatMap(opt -> opt.get())
          .forEach(System.out::println);
  }

  public static Optional<Stream<String>> gtFileContents(String fn) {
    try {
      return Optional.of(Files.lines(Path.of(fn)));
    } catch (IOException e) {
      System.out.println(Colors.RED + "File not found: " + e.getMessage());
      return Optional.empty();
    }
  }

}
