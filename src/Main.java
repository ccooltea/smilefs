import java.util.Scanner;

import command.Command;
import util.CommandLineUtil;

public class Main {
  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      while (scanner.hasNextLine()) {
        // System.out.print("> ");
        String line = scanner.nextLine();
        try {
          Command command = CommandLineUtil.parse(line);
          command.execute();
        } catch (Exception e) {
          // e.printStackTrace();
        }
      }
    }
  }
}
