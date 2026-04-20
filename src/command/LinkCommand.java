package command;

import filesystem.FileSystem;

public class LinkCommand implements Command {
  private String sourcePath;
  private String targetPath;

  public LinkCommand(String sourcePath, String targetPath) {
    this.sourcePath = sourcePath;
    this.targetPath = targetPath;
  }

  @Override
  public void execute() {
    FileSystem.addLink(targetPath, sourcePath);
  }
}
