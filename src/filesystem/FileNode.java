package filesystem;

public class FileNode extends Node {
  private long size;

  public FileNode(long id, String name, Node parent, long size) {
    super(id, name, parent);
    this.size = size;
  }

  @Override
  public NodeType getType() {
    return NodeType.FILE;
  }

  public long getSize() {
    return size;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visitFile(this);
  }

  public void setSize(long size) {
    this.size = size;
  }
}
