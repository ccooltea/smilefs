package util;

import exception.InvalidPathException;

public class PathUtil {
  public static final String SEPARATOR = "/";
  public static final String SELF = ".";
  public static final String PARENT = "..";

  public static String join(String... parts) {
    if (parts.length > 0 && parts[0].equals(SEPARATOR)) {
      parts[0] = "";
    }
    return String.join(SEPARATOR, parts);
  }

  public static String[] split(String path) {
    if (path.equals(SEPARATOR)) {
      return new String[] { SEPARATOR };
    }
    String[] parts = path.split(SEPARATOR);
    if (parts.length > 0 && parts[0].isEmpty()) {
      parts[0] = SEPARATOR;
    }
    return parts;
  }

  public static String normalize(String path) {
    String[] parts = path.split(SEPARATOR);
    StringBuilder sb = new StringBuilder();
    for (String part : parts) {
      if (part.isEmpty() || part.equals(SELF)) {
        continue;
      } else if (part.equals(PARENT)) {
        int lastSlash = sb.lastIndexOf(SEPARATOR);
        if (lastSlash >= 0) {
          sb.delete(lastSlash, sb.length());
        } else {
          throw new InvalidPathException("Invalid path: " + path);
        }
      } else {
        sb.append(SEPARATOR).append(part);
      }
    }
    return sb.length() == 0 ? SEPARATOR : sb.toString();
  }

  public static String getParent(String path) {
    int lastSlash = path.lastIndexOf(SEPARATOR);
    if (lastSlash < 0) {
      throw new InvalidPathException("Invalid path: " + path);
    }
    if (lastSlash == path.length() - 1) {
      lastSlash = path.lastIndexOf(SEPARATOR, lastSlash - 1);
    }
    return lastSlash == 0 ? SEPARATOR : path.substring(0, lastSlash);
  }

  public static String getName(String path) {
    int lastSlash = path.lastIndexOf(SEPARATOR);
    if (lastSlash < 0) {
      throw new InvalidPathException("Invalid path: " + path);
    }
    if (lastSlash == path.length() - 1) {
      lastSlash = path.lastIndexOf(SEPARATOR, lastSlash - 1);
      return path.substring(lastSlash + 1, path.length() - 1);
    }
    return path.substring(lastSlash + 1);
  }
}
