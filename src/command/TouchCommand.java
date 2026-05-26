package command;

import filesystem.FileSystem;

public class TouchCommand implements Command {
  private String path;
  private long size;

  public TouchCommand(String path, long size) {
    this.path = path;
    this.size = size;
  }

  @Override
  public void execute() {
    FileSystem.addFile(path, size);
  }

  public String getPath() {
    return path;
  }

  public long getSize() {
    return size;
  }
}
