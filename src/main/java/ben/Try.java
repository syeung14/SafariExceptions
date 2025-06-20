package ben;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class Try<T, R> {

    private final Function<T, R> fn;
    private final Function<RuntimeException, R> failureFn;

    public static <T, R> Try<T, R> of(ThrowingFunction<T, R> fn) {
        Objects.requireNonNull(fn);
        return new Try<>(fn, null);
    }

    private Try(Function<T, R> fn, Function<RuntimeException, R> failureFn) {
        this.fn = fn;
        this.failureFn = failureFn;
    }

    public Try<T, R> success(Function<R, R> successFn) {
        Objects.requireNonNull(successFn);
        var composedFn = this.fn.andThen(successFn);
        return new Try<>(composedFn, this.failureFn);
    }

    public Try<T, R> failure(Function<RuntimeException, R> failureFn) {
        Objects.requireNonNull(failureFn);
        return new Try<>(this.fn, failureFn);
    }

    public Optional<R> apply(T value) {
        try {
            var result = this.fn.apply(value);
            return Optional.ofNullable(result);
        } catch (RuntimeException e) {
            if (this.failureFn != null) {
                var result = this.failureFn.apply(e);
                return Optional.ofNullable(result);
            }
        }
        return Optional.empty();
    }
}
