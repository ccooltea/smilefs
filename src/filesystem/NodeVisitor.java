package filesystem;

public class NodeVisitor {
  public void visit(Node node) {
    node.accept(this);
  }

  public void visitFile(FileNode node) {
  }

  public void visitDirectory(DirectoryNode node) {
    for (Node child : node.getChildrenNodes()) {
      child.accept(this);
    }
  }

  public void visitLink(LinkNode node) {
  }
}
