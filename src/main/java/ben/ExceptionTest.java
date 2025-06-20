package ben;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ExceptionTest {

    public static void main(String[] args) {
        var tryit = Try.<Path, String>of(Files::readString);
    }


    public static String readString(Path path) throws IOException {
        throw new IOException("test IO");
    }

    public static String safeReadString(Path path) {
        try {
            return readString(Path.of("/"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public <E extends Throwable> void sneaky(Throwable e) throws E {
        throw (E) e;
    }

    public static void callLambda() {
        Path p1 = Path.of("");
        Path p2 = Path.of("");
        Stream.of(p1, p2)
              .map(path -> safeReadString(path))
              .filter(Objects::nonNull)
              .forEach(System.out::println);
    }

    public Optional<String> safeLambdaOpt(Path path) {
        try {
            var content = Files.readString(path);
            return Optional.of(content);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
