package util;

import java.nio.file.Path;

import exception.InvalidPathException;

public class PathUtil {
  public static final String SEPARATOR = "/";

  public static boolean isAbsolute(String path) {
    Path p = Path.of(path);
    return p.isAbsolute();
  }

  public static String join(String... parts) {
    Path p = Path.of(String.join(SEPARATOR, parts));
    return p.toString();
  }

  public static String[] split(String path) {
    Path p = Path.of(path);
    String[] parts = new String[p.getNameCount()];
    for (int i = 0; i < p.getNameCount(); ++i) {
      parts[i] = p.getName(i).toString();
    }
    return parts;
  }

  public static String normalize(String path) {
    Path p = Path.of(path).normalize();
    return p.toString();
  }

  public static String getParent(String path) {
    Path p = Path.of(path);
    Path parent = p.getParent();
    if (parent == null || parent.toString().isEmpty()) {
      throw new InvalidPathException("No parent for path: " + path);
    }
    return parent.toString();
  }

  public static String getName(String path) {
    Path p = Path.of(path);
    Path name = p.getFileName();
    if (name == null || name.toString().isEmpty()) {
      throw new InvalidPathException("No name for path: " + path);
    }
    return name.toString();
  }
}
