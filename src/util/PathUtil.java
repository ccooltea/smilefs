package util;

import exception.InvalidPathException;

public class PathUtil {
  public static String join(String... parts) {
    if (parts.length > 0 && parts[0].equals("/")) {
      parts[0] = "";
    }
    return String.join("/", parts);
  }

  public static String[] split(String path) {
    String[] parts = path.split("/");
    if (parts.length > 0 && parts[0].isEmpty()) {
      parts[0] = "/";
    }
    return parts;
  }

  public static String normalize(String path) {
    String[] parts = path.split("/");
    StringBuilder sb = new StringBuilder();
    for (String part : parts) {
      if (part.isEmpty() || part.equals(".")) {
        continue;
      } else if (part.equals("..")) {
        int lastSlash = sb.lastIndexOf("/");
        if (lastSlash >= 0) {
          sb.delete(lastSlash, sb.length());
        }
      } else {
        sb.append("/").append(part);
      }
    }
    return sb.length() == 0 ? "/" : sb.toString();
  }

  public static String getParent(String path) {
    int lastSlash = path.lastIndexOf("/");
    if (lastSlash < 0) {
      throw new InvalidPathException("Invalid path: " + path);
    }
    if (lastSlash == path.length() - 1) {
      lastSlash = path.lastIndexOf("/", lastSlash - 1);
    }
    return lastSlash == 0 ? "/" : path.substring(0, lastSlash);
  }

  public static String getName(String path) {
    int lastSlash = path.lastIndexOf("/");
    if (lastSlash < 0) {
      throw new InvalidPathException("Invalid path: " + path);
    }
    if (lastSlash == path.length() - 1) {
      lastSlash = path.lastIndexOf("/", lastSlash - 1);
      return path.substring(lastSlash + 1, path.length() - 1);
    }
    return path.substring(lastSlash + 1);
  }
}
