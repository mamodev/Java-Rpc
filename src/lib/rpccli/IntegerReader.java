package lib.rpccli;

import java.util.Scanner;

public class IntegerReader {
    public static Integer read(Scanner scanner) {
    Integer res = null;
    while (res == null) {
      try {
        res = Integer.parseInt(scanner.nextLine());
      } catch (NumberFormatException e) {
        System.out.print("Invalid input, please enter a number: ");
      }
    }
    return res;
  }
}
