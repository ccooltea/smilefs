package test.util;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import util.PathUtil;

public class PathUtilTest {
  @Test
  public void testJoin1() {
    String path = PathUtil.join("/", "usr", "bin");
    assertEquals("/usr/bin", path);
  }

  @Test
  public void testJoin2() {
    String path = PathUtil.join(".", "src", "main");
    assertEquals("./src/main", path);
  }

  @Test
  public void testJoin3() {
    String path = PathUtil.join(".", "src", "..", "test");
    assertEquals("./src/../test", path);
  }

  @Test
  public void testSplit1() {
    String path = "/usr/bin";
    String[] parts = PathUtil.split(path);
    String[] expected = new String[] { "/", "usr", "bin" };
    assertArrayEquals(expected, parts);
  }

  @Test
  public void testSplit2() {
    String path = "./src/main";
    String[] parts = PathUtil.split(path);
    String[] expected = new String[] { ".", "src", "main" };
    assertArrayEquals(expected, parts);
  }

  @Test
  public void testSplit3() {
    String path = "./src/../test";
    String[] parts = PathUtil.split(path);
    String[] expected = new String[] { ".", "src", "..", "test" };
    assertArrayEquals(expected, parts);
  }

  @Test
  public void testNormalize1() {
    String path = "/src/../test";
    String normed = PathUtil.normalize(path);
    assertEquals("/test", normed);
  }

  @Test
  public void testNormalize2() {
    String path = "//usr/local//";
    String normed = PathUtil.normalize(path);
    assertEquals("/usr/local", normed);
  }
}
