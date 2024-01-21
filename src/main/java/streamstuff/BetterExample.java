package streamstuff;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

class Either<S, F> {
    private S success;
    private F failure;

    private Either(S success, F failure) {
        this.success = success;
        this.failure = failure;
    }

    public static <S, F> Either<S, F> success(S s) {
        return new Either<>(s, null);
    }

    public static <S, F> Either<S, F> failure(F f) {
        return new Either<>(null, f);
    }

    public boolean isSuccess() {
        return failure == null;
    }

    public boolean isFailure() {
        return failure != null;
    }

    public F getFailure() {
        if (!isFailure()) throw new IllegalStateException("attempt to get failure from a success");
        return failure;
    }

    public S getSuccess() {
        if (isFailure()) throw new IllegalStateException("attempt to get success from a failure");
        return success;
    }

    public Either<S, F> recover(UnaryOperator<Either<S, F>> op) {
        if (isSuccess()) return this; // this is why we can't change the return type!!!
        else return op.apply(this);
    }

    public static <S, F> UnaryOperator<Either<S, F>> recoverySequence(UnaryOperator<Either<S, F>> ... ops) {
        return e -> {
            for (int idx = 0; idx < ops.length && e.isFailure(); idx++) {
                e = ops[idx].apply(e);
            }
            return e;
        };
    }

    public static <S, F> UnaryOperator<Either<S, F>> recoveryIterator(UnaryOperator<Either<S, F>> op, int count) {
        return e -> {
            for (int idx = 0; idx < count && e.isFailure(); idx++) {
                e = op.apply(e);
            }
            return e;
        };
    }
}

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

public class BetterExample {
    public static void main(String[] args) {
        Function<String, Either<Stream<String>, Throwable>> getFileContents =
                ExFunction.wrap(fn -> Files.lines(Path.of(fn)));

        UnaryOperator<Either<Stream<String>, Throwable>> tryBackupfile = e -> {
            String badFile = e.getFailure().getMessage();
            System.out.println("**** Recovering from missing file: " + badFile);
            return getFileContents.apply("d.txt");
        };

        UnaryOperator<Either<Stream<String>, Throwable>> retry = e -> {
            String badFile = e.getFailure().getMessage();
            System.out.println("**** Retrying file: " + badFile);
            return getFileContents.apply(badFile);
        };

        UnaryOperator<Either<Stream<String>, Throwable>> delay = e -> {
            System.out.println("delay!!!");
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException ie) {
                System.out.println("Interrupted?? really?");
            }
            return e;
        };

        Stream.of("a.txt", "b.txt", "c.txt")
                .map(getFileContents)
                .peek(e -> {
                    if (e.isFailure()) {
                        System.out.println("***There was a problem!!! " + e.getFailure());
                    }
                })
//                .map(e -> e.recover(delay))
//                .map(e -> e.recover(retry))
//                .map(e -> e.recover(tryBackupfile))
                .map(Either.recoverySequence(
                        Either.recoveryIterator(
                                Either.recoverySequence(delay, retry), 3),
                        tryBackupfile))
                .filter(e -> e.isSuccess())
                .flatMap(e -> e.getSuccess())
                .forEach(s -> System.out.println(s));
    }
}
