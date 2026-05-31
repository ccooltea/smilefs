package filesystem;

import java.util.HashSet;
import java.util.Set;

import exception.CyclicLinkException;

public class LinkNode extends Node {
  private String path;

  public LinkNode(long id, String name, Node parent, String path) {
    super(id, name, parent);
    this.path = path;
  }

  @Override
  public NodeType getType() {
    return NodeType.LINK;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visitLink(this);
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Node getLink() {
    Set<Long> visited = new HashSet<>();
    Node current = this;
    while (current != null && current.getType() == NodeType.LINK) {
      if (visited.contains(current.getId())) {
        throw new CyclicLinkException("Cyclic link detected at: " + current.getName());
      }
      visited.add(current.getId());
      LinkNode link = (LinkNode) current;
      current = link.getDirectLink();
    }
    return current;
  }

  public Node getDirectLink() {
    return FileSystem.getNode(path);
  }
}
