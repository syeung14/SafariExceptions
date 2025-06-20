package ben;

import java.util.function.Function;

public interface ThrowingFunction<T, R> extends Function<T, R> {

    R applyThrows(T elem) throws Exception;

    @Override
    default R apply(T t) {
        try {
            return applyThrows(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T, R> Function<T, R> uncheck(ThrowingFunction<T, R> fn) {
        return fn::apply;
    }

    record Result<V, E extends Throwable>(V value, E Throwable, boolean isSuccess) {

        public static <V, E extends Throwable> Result<V, E> success(V value) {
            return new Result<>(value, null, true);
        }

        public static <V, E extends Throwable> Result<V, E> failure(E throwable) {
            return new Result<>(null, throwable, true);
        }
    }
}
