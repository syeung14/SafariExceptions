package either;

import ansi.Colors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

interface ExFunction<A, R> {
  R apply(A a) throws Throwable;

  static <A, R> Function<A, Either<R, Throwable>> wrap(ExFunction<A, R> op) {
    return a -> {
      try {
        return Either.success(op.apply(a));
      } catch (Throwable t) {
        return Either.failure(t);
      }
    };
  }
}

// Most implementations of "either" represent "left" and "right"
// for historical bigotry (we have traditionally associated left with bad)
// right is success, and left is failure

public class Either<S, F> {
  private S success;
  private F failure;

  private Either() {
  }

  public static <S, F> Either<S, F> success(S s) {
    Either<S, F> self = new Either<>();
    self.success = s;
    return self;
  }

  public static <S, F> Either<S, F> failure(F f) {
    Either<S, F> self = new Either<>();
    self.failure = f;
    return self;
  }

  public boolean isSuccess() {
    return failure == null;
  }

  public boolean isFailure() {
    return failure != null;
  }

  public S get() {
    if (isSuccess()) {
      return success;
    } else {
      throw new IllegalStateException(
          "Attempt to get success value from a failure");
    }
  }

  public F getFailure() {
    if (isFailure()) {
      return failure;
    } else {
      throw new IllegalStateException(
          "Attempt to get failure value from a success");
    }
  }

  public Either<S, F> report(Consumer<F> op) {
    if (isFailure()) {
      op.accept(failure);
    }
    return this;
  }
}


class UseEither {
  private static Map<String, String> backup = Map.of(
      "b.txt", "d.txt"
  );

  private static Either<Stream<String>, Throwable> tryBackup(
      Either<Stream<String>, Throwable> e) {
    if (e.isFailure()) {
      String failedName = e.getFailure().getMessage();
      String backupName = backup.get(failedName);
      System.out.println(Colors.PURPLE
          + "recovering from missing: " + failedName
          + " with " + backupName + Colors.RESET);
      e = ExFunction.wrap((String f) -> Files.lines(Path.of(f)))
          .apply(backupName);
    }
    return e;
  }

  public static void main(String[] args) {
    Stream.of("a.txt", "b.txt", "c.txt")
        .map(ExFunction.wrap(fn -> Files.lines(Path.of(fn))))
        .map(e -> e.report(f -> System.out.println(
            Colors.PURPLE + "File not found: " + f.getMessage() + Colors.RESET)))
        .map(UseEither::tryBackup)
        .filter(Either::isSuccess)
        .flatMap(Either::get)
        .forEach(System.out::println);
    ;
  }
}