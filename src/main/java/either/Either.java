package either;

import ansi.Colors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
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

//  public Either<S, F> mapIfFailure(UnaryOperator<Either<S, F>> op) {
  public Either<S, F> recover(UnaryOperator<Either<S, F>> op) {
    if (isFailure()) {
      return op.apply(this);
    } else {
      return this;
    }
  }

  public static <S, F> UnaryOperator<Either<S, F>> retries(
      UnaryOperator<Either<S, F>> op, int limit) {
    return e -> {
      int triesLeft = limit;
      while (e.isFailure() && triesLeft-- > 0) {
        e = op.apply(e);
      }
      return e;
    };
  }

  public static <S, F> UnaryOperator<Either<S, F>> retrySequence(
      UnaryOperator<Either<S, F>> ... op) {
    return e -> {
      int idx = 0;
      while (e.isFailure() && idx < op.length) {
        e = op[idx++].apply(e);
      }
      return e;
    };
  }
}


class UseEither {
  private static Map<String, String> backup = Map.of(
      "b.txt", "d.txt",
      "d.txt", "e.txt"
  );

  private static Either<Stream<String>, Throwable> retry(
      Either<Stream<String>, Throwable> e) {

    String fn = e.getFailure().getMessage();
    System.out.println(Colors.PURPLE
        + "retrying: " + fn + Colors.RESET);
    return ExFunction.wrap((String f) -> Files.lines(Path.of(f)))
        .apply(fn);
  }

  private static Either<Stream<String>, Throwable> tryBackup(
      Either<Stream<String>, Throwable> e) {
// now suitable only for "mapIfFailure" or whatever I rename it to
//    if (e.isFailure()) {
      String failedName = e.getFailure().getMessage();
      String backupName = backup.get(failedName);
      System.out.println(Colors.PURPLE
          + "recovering from missing: " + failedName
          + " with " + backupName + Colors.RESET);
      e = ExFunction.wrap((String f) -> Files.lines(Path.of(f)))
          .apply(backupName);
//    }
    return e;
  }

  private static Either<Stream<String>, Throwable> pause(
      Either<Stream<String>, Throwable> e) {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException ie) {} // ignore, not really ideal!
    return e;
  }

  public static void main(String[] args) {
    Stream.of("a.txt", "b.txt", "c.txt")
        .map(ExFunction.wrap(fn -> Files.lines(Path.of(fn))))
        .map(e -> e.report(f -> System.out.println(
            Colors.PURPLE + "File not found: " + f.getMessage() + Colors.RESET)))
//        .map(e -> e.mapIfFailure(UseEither::tryBackup))

//        .map(Either.retries(e -> e.recover(UseEither::tryBackup), 3))

//        .map(e -> e.recover(UseEither::pause))
//        .map(e -> e.recover(UseEither::retry))

        .map(Either.retrySequence(
            UseEither::pause,
            UseEither::retry,
            Either.retries(UseEither::tryBackup, 3)
            ))

        .filter(Either::isSuccess)
        .flatMap(Either::get)
        .forEach(System.out::println);
    ;
  }
}