package filesystem;

import util.PathUtil;

public abstract class Node {
  private long id;
  private String name;
  private Node parent;

  public Node(long id, String name, Node parent) {
    this.id = id;
    this.name = name;
    this.parent = parent;
  }

  public abstract NodeType getType();

  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Node getParent() {
    return parent;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public String getPath() {
    if (parent == null) {
      return name;
    } else {
      return PathUtil.join(parent.getPath(), name);
    }
  }
}
