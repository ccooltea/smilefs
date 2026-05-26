package filesystem;

import java.util.Set;
import java.util.HashSet;

public class SizeVisitor extends NodeVisitor {
  private long size = 0;
  private Set<Long> visited = new HashSet<>();

  @Override
  public void visitFile(FileNode file) {
    if (!visited.contains(file.getId())) {
      size += file.getSize();
      visited.add(file.getId());
    }
  }

  @Override
  public void visitDirectory(DirectoryNode dir) {
    if (!visited.contains(dir.getId())) {
      visited.add(dir.getId());
      for (Node child : dir.getChildrenNodes()) {
        child.accept(this);
      }
    }
  }

  @Override
  public void visitLink(LinkNode link) {
    Node target = link.getLink();
    if (target != null) {
      target.accept(this);
    }
  }

  public long getSize() {
    return size;
  }

}
