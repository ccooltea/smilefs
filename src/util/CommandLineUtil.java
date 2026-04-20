package util;

import command.Command;
import command.InfoCommand;
import command.ListCommand;
import command.MakeDirectoryCommand;
import command.RemoveCommand;
import command.TouchCommand;
import exception.CommandLineParseException;

public class CommandLineUtil {
  public static Command parse(String line) throws CommandLineParseException {
    String[] parts = line.trim().split(" +");
    String cmd = parts[0].toLowerCase();
    switch (cmd) {
      case "ls":
        if (parts.length == 2) {
          return new ListCommand(parts[1]);
        } else {
          throw new CommandLineParseException("Invalid arguments for ls command");
        }
      case "mkdir":
        if (parts.length == 2) {
          return new MakeDirectoryCommand(parts[1]);
        } else {
          throw new CommandLineParseException("Invalid arguments for mkdir command");
        }
      case "touch":
        if (parts.length == 3) {
          return new TouchCommand(parts[1], Long.valueOf(parts[2]));
        } else {
          throw new CommandLineParseException("Invalid arguments for touch command");
        }
      case "info":
        if (parts.length == 2) {
          return new InfoCommand(parts[1]);
        } else {
          throw new CommandLineParseException("Invalid arguments for info command");
        }
      case "find":
        if (parts.length == 3) {
          return new command.FindCommand(parts[1], parts[2]);
        } else {
          throw new CommandLineParseException("Invalid arguments for find command");
        }
      case "rm":
        if (parts.length == 2) {
          return new RemoveCommand(parts[1]);
        } else {
          throw new CommandLineParseException("Invalid arguments for rm command");
        }
      case "link":
        if (parts.length == 3) {
          return new command.LinkCommand(parts[1], parts[2]);
        } else {
          throw new CommandLineParseException("Invalid arguments for link command");
        }
      default:
        throw new CommandLineParseException("Unknown command: " + cmd);
    }
  }
}
