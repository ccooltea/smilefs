package filesystem;

import java.util.List;

import exception.InvalidFileSizeException;
import exception.InvalidPathException;
import exception.NodeExistenceException;
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
          throw new NodeExistenceException("Link target does not exist: " + link.getPath());
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
      throw new NodeExistenceException("Node does not exist: " + path);
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
      throw new NodeExistenceException("Parent node does not exist: " + path);
    }
    if (parent.getType() == NodeType.FILE) {
      throw new InvalidPathException("Parent node is a file:" + path);
    }
    DirectoryNode parentDir = (DirectoryNode) parent;
    String name = PathUtil.getName(path);
    if (parentDir.hasChild(name)) {
      throw new NodeExistenceException("Directory already exists: " + path);
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
      throw new NodeExistenceException("Parent node does not exist: " + path);
    }
    if (parent.getType() == NodeType.FILE) {
      throw new InvalidPathException("Parent node is a file:" + path);
    }
    DirectoryNode parentDir = (DirectoryNode) parent;
    String name = PathUtil.getName(path);
    if (parentDir.hasChild(name)) {
      Node child = parentDir.getChild(name);
      if (child.getType() != NodeType.FILE) {
        throw new NodeExistenceException("Directory already exists: " + path);
      }
    }
    FileNode file = new FileNode(getNextID(), name, parentDir, size);
    parentDir.addChild(file);
  }

  public static void addLink(String path, String targetPath) {
    Node parent = getNode(PathUtil.getParent(PathUtil.normalize(path)));
    if (parent == null) {
      throw new NodeExistenceException("Parent node does not exist: " + path);
    }
    if (parent.getType() == NodeType.FILE) {
      throw new InvalidPathException("Parent node is a file:" + path);
    }
    DirectoryNode parentDir = (DirectoryNode) parent;
    String name = PathUtil.getName(path);
    if (parentDir.hasChild(name)) {
      throw new NodeExistenceException("Node with same name already exists: " + path);
    }
    LinkNode link = new LinkNode(getNextID(), name, parentDir, PathUtil.normalize(targetPath));
    parentDir.addChild(link);
  }

  public static void remove(String path) {
    Node node = getNode(PathUtil.normalize(path));
    if (node == null) {
      throw new NodeExistenceException("Node does not exist: " + path);
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
    Node node = getNode(PathUtil.normalize(path));
    if (node == null) {
      throw new NodeExistenceException("Node does not exist: " + path);
    }
    return node.getSize();
  }

  private static List<String> search(Node node, String name, String currentPath) {
    List<String> result = new java.util.ArrayList<>();
    if (node.getName().equals(name)) {
      result.add(currentPath);
    }
    if (node.getType() == NodeType.DIRECTORY) {
      DirectoryNode dir = (DirectoryNode) node;
      for (Node child : dir.getChildrenNodes()) {
        result.addAll(search(child, name, PathUtil.join(currentPath, child.getName())));
      }
    }
    return result;
  }

  public static List<String> search(String path, String name) {
    Node node = getNode(PathUtil.normalize(path));
    if (node == null) {
      throw new NodeExistenceException("Node does not exist: " + path);
    }
    if (node.getType() == NodeType.FILE) {
      throw new InvalidPathException("Node is a file:" + path);
    }
    return search(node, name, path);
  }
}
