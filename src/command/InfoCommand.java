package command;

import filesystem.FileSystem;

public class InfoCommand implements Command {
  private String path;

  public InfoCommand(String path) {
    this.path = path;
  }

  @Override
  public void execute() {
    long size = FileSystem.sizeOf(path);
    System.out.println(size);
  }
}
