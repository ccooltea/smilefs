package filesystem;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.nio.file.Path;
import java.util.ArrayList;

public class SearchVisitor extends NodeVisitor {
  private Path path;
  private String name;
  private Set<Long> visited = new HashSet<>();
  private List<String> results = new ArrayList<>();

  public SearchVisitor(String path, String name) {
    this.path = Path.of(path).normalize();
    this.name = name;
  }

  private void pushPath(String name) {
    path = path.resolve(name);
  }

  private void popPath() {
    path = path.getParent();
  }

  @Override
  public void visitFile(FileNode file) {
    if (visited.contains(file.getId()))
      return;
    visited.add(file.getId());
    if (file.getName().equals(name)) {
      results.add(path.toString());
    }
  }

  @Override
  public void visitDirectory(DirectoryNode dir) {
    if (visited.contains(dir.getId()))
      return;
    visited.add(dir.getId());
    for (Node child : dir.getChildrenNodes()) {
      pushPath(child.getName());
      child.accept(this);
      popPath();
    }
  }

  @Override
  public void visitLink(LinkNode link) {
    if (visited.contains(link.getId()))
      return;
    visited.add(link.getId());
    if (link.getName().equals(name)) {
      results.add(path.toString());
    }
    Node target = link.getLink();
    if (target.getType() == NodeType.DIRECTORY) {
      DirectoryNode dir = (DirectoryNode) target;
      for (Node child : dir.getChildrenNodes()) {
        pushPath(child.getName());
        child.accept(this);
        popPath();
      }
    }
  }

  public List<String> getResults() {
    results.sort(String::compareTo);
    return results;
  }
}
