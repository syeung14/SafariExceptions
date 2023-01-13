package useoptional;

import java.util.Map;
import java.util.Optional;

public class UseOptional {
  public static String getFirstName() {
    return "Fred";
  }
  public static void main(String[] args) {
    Map<String, String> names = Map.of(
        "Fred", "Jones"
    );

    String firstName = getFirstName();
    String lastName = names.get(firstName);
    if (lastName != null) {
      String message = "Dear " + lastName.toUpperCase();
      if (message != null) {
        System.out.println(message);
      }
    }

    Optional<Map<String, String>> oMap = Optional.of(names);
    oMap.map(m -> m.get(firstName))
        .map(ln -> ln.toUpperCase())
        .map(ln -> "Dear " + ln)
//        .ifPresent(System.out::println);
        .ifPresentOrElse(System.out::println,
            () -> System.out.println("No data found!"));
  }
}
