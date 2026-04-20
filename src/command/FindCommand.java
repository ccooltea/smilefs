package command;

import java.util.List;

import filesystem.FileSystem;

public class FindCommand implements Command {
  private String path;
  private String name;

  public FindCommand(String path, String name) {
    this.path = path;
    this.name = name;
  }

  @Override
  public void execute() {
    List<String> paths = FileSystem.search(path, name);
    for (String p : paths) {
      System.out.println(p);
    }
  }
}
