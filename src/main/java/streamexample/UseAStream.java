package streamexample;

import ansi.Colors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

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

  public static Optional<Stream<String>> getFileContents(String fn) {
    try {
      return Optional.of(Files.lines(Path.of(fn)));
    } catch (IOException ioe) {
//      throw new RuntimeException(ioe);
//      return null; // NO NO NO, yuk, can't believe this even works!
      System.out.println(Colors.RED + "File not found: " + ioe.getMessage() + Colors.RESET);
      return Optional.empty();
    }
  }

  public static void main(String[] args) {
    Stream.of("a.txt", "b.txt", "c.txt")
        // one input filename -> many String lines
//        .flatMap(UseAStream::getFileContents) // works when result is Stream.
        .map(UseAStream::getFileContents)
        // do something with "missing"
        .forEach(System.out::println);
  }
}
