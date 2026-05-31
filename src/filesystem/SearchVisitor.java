package filesystem;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class SearchVisitor extends NodeVisitor {
  private StringBuilder path;
  private String name;
  private Set<Long> visited = new HashSet<>();
  private List<String> results = new ArrayList<>();

  public SearchVisitor(String path, String name) {
    this.path = new StringBuilder(path);
    this.name = name;
  }

  private void pushPath(String name) {
    if (path.length() > 1) {
      path.append('/');
    }
    if (!name.equals("/")) {
      path.append(name);
    }
  }

  private void popPath() {
    int lastSlash = path.lastIndexOf("/");
    path.delete(lastSlash, path.length());
    if (path.isEmpty()) {
      path.append('/');
    }
  }

  @Override
  public void visitFile(FileNode file) {
    if (visited.contains(file.getId()))
      return;
    visited.add(file.getId());
    if (file.getName().equals(name)) {
      pushPath(file.getName());
      results.add(path.toString());
      popPath();
    }
  }

  @Override
  public void visitDirectory(DirectoryNode dir) {
    if (visited.contains(dir.getId()))
      return;
    visited.add(dir.getId());
    pushPath(dir.getName());
    for (Node child : dir.getChildrenNodes()) {
      child.accept(this);
    }
    popPath();
  }

  @Override
  public void visitLink(LinkNode link) {
    if (visited.contains(link.getId()))
      return;
    visited.add(link.getId());
    Node target = link.getLink();
    if (target != null) {
      if (target.getType() == NodeType.DIRECTORY) {
        DirectoryNode dir = (DirectoryNode) target;
        pushPath(link.getName());
        for (Node child : dir.getChildrenNodes()) {
          child.accept(this);
        }
        popPath();
      } else {
        if (link.getName().equals(name)) {
          pushPath(link.getName());
          results.add(path.toString());
          popPath();
        }
      }
    }
  }

  public List<String> getResults() {
    results.sort(String::compareTo);
    return results;
  }
}
