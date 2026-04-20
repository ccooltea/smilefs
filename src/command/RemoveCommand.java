package command;

import filesystem.FileSystem;

public class RemoveCommand implements Command {
  private String path;

  public RemoveCommand(String path) {
    this.path = path;
  }

  @Override
  public void execute() {
    FileSystem.remove(path);
  }
}
