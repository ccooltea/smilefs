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
    if (!PathUtil.isAbsolute(path)) {
      throw new InvalidPathException("Path must be absolute: " + path);
    }
    String normedPath = PathUtil.normalize(path);
    String[] parts = PathUtil.split(normedPath);
    Node current = instance.root;
    for (String part : parts) {
      if (current.getType() == NodeType.LINK) {
        LinkNode link = (LinkNode) current;
        current = link.getLink();
      }
      if (current.getType() != NodeType.DIRECTORY) {
        throw new InvalidPathException("Not a directory: " + current.getName());
      }
      DirectoryNode dir = (DirectoryNode) current;
      current = dir.getChild(part);
      if (current == null) {
        throw new InvalidPathException("Node does not exist: " + path);
      }
    }
    return current;
  }

  public static List<String> list(String path) {
    Node node = getNode(path);
    if (node.getType() == NodeType.LINK) {
      LinkNode link = (LinkNode) node;
      Node linkNode = link.getLink();
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
    String normedPath = PathUtil.normalize(path);
    Node parent = getNode(PathUtil.getParent(normedPath));
    if (parent.getType() == NodeType.LINK) {
      LinkNode link = (LinkNode) parent;
      parent = link.getLink();
    }
    if (parent.getType() != NodeType.DIRECTORY) {
      throw new InvalidPathException("Parent node is not a directory: " + path);
    }
    DirectoryNode parentDir = (DirectoryNode) parent;
    String name = PathUtil.getName(normedPath);
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
    String normedPath = PathUtil.normalize(path);
    Node parent = getNode(PathUtil.getParent(normedPath));
    if (parent.getType() == NodeType.LINK) {
      LinkNode link = (LinkNode) parent;
      parent = link.getLink();
    }
    if (parent.getType() != NodeType.DIRECTORY) {
      throw new InvalidPathException("Parent node is not a directory: " + path);
    }
    DirectoryNode parentDir = (DirectoryNode) parent;
    String name = PathUtil.getName(normedPath);
    FileNode file = new FileNode(getNextID(), name, parentDir, size);
    parentDir.addChild(file);
  }

  public static void addLink(String path, String targetPath) {
    String normedTargetPath = PathUtil.normalize(targetPath);
    getNode(normedTargetPath); // validate target path
    String normedPath = PathUtil.normalize(path);
    Node parent = getNode(PathUtil.getParent(normedPath));
    if (parent.getType() == NodeType.LINK) {
      LinkNode link = (LinkNode) parent;
      parent = link.getLink();
    }
    if (parent.getType() != NodeType.DIRECTORY) {
      throw new InvalidPathException("Parent node is not a directory: " + path);
    }
    DirectoryNode parentDir = (DirectoryNode) parent;
    String name = PathUtil.getName(normedPath);
    LinkNode link = new LinkNode(getNextID(), name, parentDir, normedTargetPath);
    parentDir.addChild(link);
  }

  public static void remove(String path) {
    Node node = getNode(path);
    if (node == instance.root) {
      throw new InvalidPathException("Cannot remove root directory");
    }
    DirectoryNode parentDir = (DirectoryNode) node.getParent();
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
    Node node = getNode(path);
    SizeVisitor visitor = new SizeVisitor();
    node.accept(visitor);
    return visitor.getSize();
  }

  public static List<String> search(String path, String name) {
    Node node = getNode(path);
    SearchVisitor visitor = new SearchVisitor(path, name);
    node.accept(visitor);
    return visitor.getResults();
  }
}
