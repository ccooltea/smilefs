package filesystem;

public class NodeVisitor {
  public void visit(Node node) {
    node.accept(this);
  }

  public void visit(FileNode node) {
  }

  public void visit(DirectoryNode node) {
    for (Node child : node.getChildrenNodes()) {
      visit(child);
    }
  }

  public void visit(LinkNode node) {
  }
}
