package command;

import java.util.List;

import filesystem.FileSystem;

public class ListCommand implements Command {
  private String path;

  public ListCommand(String path) {
    this.path = path;
  }

  @Override
  public void execute() {
    List<String> items = FileSystem.list(path);
    for (String item : items) {
      System.out.println(item);
    }
  }

  public String getPath() {
    return path;
  }
}
