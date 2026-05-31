package filesystem;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DirectoryNode extends Node {
  private Map<String, Node> children;

  public DirectoryNode(long id, String name, Node parent) {
    super(id, name, parent);
    children = new TreeMap<>();
  }

  @Override
  public NodeType getType() {
    return NodeType.DIRECTORY;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visitDirectory(this);
  }

  public boolean hasChild(String name) {
    return children.containsKey(name);
  }

  public Node getChild(String name) {
    return children.get(name);
  }

  public void addChild(Node child) {
    children.put(child.getName(), child);
  }

  public void removeChild(String name) {
    children.remove(name);
  }

  public boolean hasChildren() {
    return !children.isEmpty();
  }

  public long getChildrenSize() {
    return children.size();
  }

  public List<String> getChildrenNames() {
    return List.copyOf(children.keySet());
  }

  public List<Node> getChildrenNodes() {
    return List.copyOf(children.values());
  }
}
