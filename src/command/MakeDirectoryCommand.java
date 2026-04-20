package command;

import filesystem.FileSystem;

public class MakeDirectoryCommand implements Command {
  private String path;

  public MakeDirectoryCommand(String path) {
    this.path = path;
  }

  @Override
  public void execute() {
    FileSystem.addDirectory(path);
  }
}
