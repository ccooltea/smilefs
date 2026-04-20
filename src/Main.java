import java.util.Scanner;

import command.Command;
import util.CommandLineUtil;

public class Main {
  public static void main(String[] args) {
    System.out.println("Hello from smilefs!");

    try (Scanner scanner = new Scanner(System.in)) {
      while (true) {
        System.out.print("> ");
        String line = scanner.nextLine();
        try {
          Command command = CommandLineUtil.parse(line);
          command.execute();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
