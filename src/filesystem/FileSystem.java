package filesystem;

import java.util.List;

import exception.InvalidFileSizeException;
import exception.InvalidPathException;
import exception.RemoveNonEmptyDirectoryException;
import util.PathUtil;

public class FileSystem {
  private static FileSystem instance = new FileSystem();

  private Node root = new DirectoryNode(0, "/", null);
  private long nextId = 1;

  private static long getNextID() {
    long id = instance.nextId;
    ++instance.nextId;
    return id;
  }

  public static Node getNode(String path) {
    String[] parts = PathUtil.split(path);
    Node current = instance.root;
    for (String part : parts) {
      if (part.equals("/")) {
        continue;
      }
      if (part.isEmpty()) {
        throw new InvalidPathException("Invalid path: " + path);
      }
      if (current.getType() == NodeType.LINK) {
        LinkNode link = (LinkNode) current;
        current = link.getLink();
        if (current == null) {
          throw new InvalidPathException("Link target does not exist: " + link.getPath());
        }
      }
      if (current.getType() != NodeType.DIRECTORY) {
        throw new InvalidPathException("Not a directory: " + current.getName());
      }
      DirectoryNode dir = (DirectoryNode) current;
      current = dir.getChild(part);
      if (current == null) {
        return null;
      }
    }
    return current;
  }

  public static List<String> list(String path) {
    Node node = getNode(PathUtil.normalize(path));
    if (node == null) {
      throw new InvalidPathException("Node does not exist: " + path);
    }
    if (node.getType() == NodeType.LINK) {
      LinkNode link = (LinkNode) node;
      Node linkNode = link.getLink();
      if (linkNode == null) {
        throw new InvalidPathException("Link target does not exist: " + link.getPath());
      }
      if (linkNode.getType() == NodeType.DIRECTORY) {
        node = linkNode;
      }
    }
    if (node.getType() == NodeType.FILE || node.getType() == NodeType.LINK) {
      return List.of(node.getName());
    } else {
      DirectoryNode dir = (DirectoryNode) node;
      return dir.getChildrenNames();
    }
  }

  public static void addDirectory(String path) {
    Node parent = getNode(PathUtil.getParent(PathUtil.normalize(path)));
    if (parent == null) {
      throw new InvalidPathException("Parent node does not exist: " + path);
    }
    if (parent.getType() == NodeType.LINK) {
      LinkNode link = (LinkNode) parent;
      parent = link.getLink();
      if (parent == null) {
        throw new InvalidPathException("Link target does not exist: " + link.getPath());
      }
    }
    if (parent.getType() == NodeType.FILE) {
      throw new InvalidPathException("Parent node is a file:" + path);
    }
    DirectoryNode parentDir = (DirectoryNode) parent;
    String name = PathUtil.getName(path);
    if (parentDir.hasChild(name) && parentDir.getChild(name).getType() == NodeType.DIRECTORY) {
      throw new InvalidPathException("Directory already exists: " + path);
    }
    DirectoryNode dir = new DirectoryNode(getNextID(), name, parentDir);
    parentDir.addChild(dir);
  }

  public static void addFile(String path, long size) {
    if (size < 0) {
      throw new InvalidFileSizeException("Invalid file size" + size);
    }
    Node parent = getNode(PathUtil.getParent(PathUtil.normalize(path)));
    if (parent == null) {
      throw new InvalidPathException("Parent node does not exist: " + path);
    }
    if (parent.getType() == NodeType.LINK) {
      LinkNode link = (LinkNode) parent;
      parent = link.getLink();
      if (parent == null) {
        throw new InvalidPathException("Link target does not exist: " + link.getPath());
      }
    }
    if (parent.getType() == NodeType.FILE) {
      throw new InvalidPathException("Parent node is a file:" + path);
    }
    DirectoryNode parentDir = (DirectoryNode) parent;
    String name = PathUtil.getName(path);
    FileNode file = new FileNode(getNextID(), name, parentDir, size);
    parentDir.addChild(file);
  }

  public static void addLink(String path, String targetPath) {
    Node targetNode = getNode(PathUtil.normalize(targetPath));
    if (targetNode == null) {
      throw new InvalidPathException("Target node does not exist: " + targetPath);
    }
    Node parent = getNode(PathUtil.getParent(PathUtil.normalize(path)));
    if (parent == null) {
      throw new InvalidPathException("Parent node does not exist: " + path);
    }
    if (parent.getType() == NodeType.LINK) {
      LinkNode link = (LinkNode) parent;
      parent = link.getLink();
      if (parent == null) {
        throw new InvalidPathException("Link target does not exist: " + link.getPath());
      }
    }
    if (parent.getType() == NodeType.FILE) {
      throw new InvalidPathException("Parent node is a file:" + path);
    }
    DirectoryNode parentDir = (DirectoryNode) parent;
    String name = PathUtil.getName(path);
    LinkNode link = new LinkNode(getNextID(), name, parentDir, PathUtil.normalize(targetPath));
    parentDir.addChild(link);
  }

  public static void remove(String path) {
    Node node = getNode(PathUtil.normalize(path));
    if (node == null) {
      throw new InvalidPathException("Node does not exist: " + path);
    }
    DirectoryNode parentDir = (DirectoryNode) node.getParent();
    if (parentDir == null) {
      throw new InvalidPathException("Cannot remove root directory");
    }
    if (node.getType() == NodeType.FILE || node.getType() == NodeType.LINK) {
      parentDir.removeChild(node.getName());
    } else {
      DirectoryNode dir = (DirectoryNode) node;
      if (dir.hasChildren()) {
        throw new RemoveNonEmptyDirectoryException("Directory is not empty: " + path);
      }
      parentDir.removeChild(node.getName());
    }
  }

  public static long sizeOf(String path) {
    Node node = getNode(PathUtil.normalize(path));
    if (node == null) {
      throw new InvalidPathException("Node does not exist: " + path);
    }
    SizeVisitor visitor = new SizeVisitor();
    node.accept(visitor);
    return visitor.getSize();
  }

  public static List<String> search(String path, String name) {
    String normedPath = PathUtil.normalize(path);
    Node node = getNode(normedPath);
    if (node == null) {
      throw new InvalidPathException("Node does not exist: " + path);
    }
    SearchVisitor visitor = new SearchVisitor(path.equals("/") ? path : PathUtil.getParent(normedPath), name);
    node.accept(visitor);
    return visitor.getResults();
  }
}
