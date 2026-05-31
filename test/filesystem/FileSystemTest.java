package test.filesystem;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import filesystem.FileSystem;
import filesystem.Node;
import filesystem.NodeType;
import exception.InvalidFileSizeException;
import exception.InvalidPathException;
import exception.RemoveNonEmptyDirectoryException;

public class FileSystemTest {

  @Test
  public void testAddInvalidDirectory1() {
    assertThrows(InvalidPathException.class, () -> FileSystem.addDirectory("/invalid/local"));
  }

  @Test
  public void testAddInvalidDirectory2() {
    FileSystem.addFile("/testAddInvalidDirectory2", 30);
    assertThrows(InvalidPathException.class, () -> FileSystem.addDirectory("/testAddInvalidDirectory2/invalid"));
  }

  @Test
  public void testAddExistingDirectory1() {
    FileSystem.addFile("/testAddExistingDirectory1-1", 30);
    FileSystem.addLink("/testAddExistingDirectory1-2", "/testAddExistingDirectory1-1");

    FileSystem.addDirectory("/testAddExistingDirectory1-1");
    Node node1 = FileSystem.getNode("/testAddExistingDirectory1-1");
    assertEquals(NodeType.DIRECTORY, node1.getType());

    FileSystem.addDirectory("/testAddExistingDirectory1-2");
    Node node2 = FileSystem.getNode("/testAddExistingDirectory1-1");
    assertEquals(NodeType.DIRECTORY, node2.getType());
  }

  @Test
  public void testAddExistingDirectory2() {
    FileSystem.addDirectory("/testAddExistingDirectory2");
    Node node1 = FileSystem.getNode("/testAddExistingDirectory2");
    long id1 = node1.getId();

    assertThrows(InvalidPathException.class, () -> FileSystem.addDirectory("/testAddExistingDirectory2"));
    Node node2 = FileSystem.getNode("/testAddExistingDirectory2");
    long id2 = node2.getId();

    assertEquals(id1, id2);
  }

  @Test
  public void testAddRootDirectory() {
    assertThrows(Exception.class, () -> FileSystem.addDirectory("/"));
  }

  @Test
  public void testAddNegativeFile() {
    assertThrows(InvalidFileSizeException.class, () -> FileSystem.addFile("/testAddNegativeFile", -30));
  }

  @Test
  public void testAddInvalidFile1() {
    assertThrows(InvalidPathException.class, () -> FileSystem.addFile("/invalid/testAddInvalidFile1", 30));
  }

  @Test
  public void testAddInvalidFile2() {
    FileSystem.addFile("/testAddInvalidFile2", 30);
    assertThrows(InvalidPathException.class, () -> FileSystem.addFile("/testAddInvalidFile2/invalid", 30));
  }

  @Test
  public void testAddExistingFile1() {
    FileSystem.addFile("/testAddExistingFile1", 30);
    FileSystem.addFile("/testAddExistingFile1", 40);
    assertEquals(40, FileSystem.sizeOf("/testAddExistingFile1"));
  }

  @Test
  public void testAddExistingFile2() {
    FileSystem.addDirectory("/testAddExistingFile2-1");
    FileSystem.addLink("/testAddExistingFile2-2", "/testAddExistingFile2-1");

    FileSystem.addFile("/testAddExistingFile2-1", 30);
    Node node1 = FileSystem.getNode("/testAddExistingFile2-1");
    assertEquals(NodeType.FILE, node1.getType());

    FileSystem.addFile("/testAddExistingFile2-2", 30);
    Node node2 = FileSystem.getNode("/testAddExistingFile2-2");
    assertEquals(NodeType.FILE, node2.getType());
  }

  @Test
  public void testAddRootFile() {
    assertThrows(InvalidPathException.class, () -> FileSystem.addFile("/", 30));
  }

  @Test
  public void testAddLinkTargetMustExist() {
    assertThrows(InvalidPathException.class, () -> FileSystem.addLink("/testAddLinkMissingTarget", "/missing-target"));
  }

  @Test
  public void testAddLinkParentMustExist() {
    FileSystem.addFile("/testAddLinkParentTarget", 10);
    assertThrows(InvalidPathException.class,
        () -> FileSystem.addLink("/missing-parent/testAddLinkParent", "/testAddLinkParentTarget"));
  }

  @Test
  public void testAddLinkReplacesExistingEntry() {
    FileSystem.addFile("/testAddLinkReplaceTarget", 10);
    FileSystem.addFile("/testAddLinkReplace", 20);
    FileSystem.addLink("/testAddLinkReplace", "/testAddLinkReplaceTarget");
    Node node = FileSystem.getNode("/testAddLinkReplace");
    assertEquals(NodeType.LINK, node.getType());
    assertEquals(10, FileSystem.sizeOf("/testAddLinkReplace"));
  }

  @Test
  public void testAddLinkRootNotAllowed() {
    FileSystem.addFile("/testAddLinkRootTarget", 10);
    assertThrows(InvalidPathException.class, () -> FileSystem.addLink("/", "/testAddLinkRootTarget"));
  }

  @Test
  public void testListFile() {
    FileSystem.addFile("/testListFile", 30);
    List<String> res = FileSystem.list("/testListFile");
    assertEquals(1, res.size());
    assertEquals("testListFile", res.getFirst());
  }

  @Test
  public void testListDirectory() {
    FileSystem.addDirectory("/testListDirectory");
    FileSystem.addFile("/testListDirectory/b.txt", 30);
    FileSystem.addFile("/testListDirectory/a.txt", 30);
    List<String> res = FileSystem.list("/testListDirectory");
    assertEquals(2, res.size());
    assertArrayEquals(new String[] { "a.txt", "b.txt" }, res.toArray());
  }

  @Test
  public void testListLink1() {
    FileSystem.addDirectory("/testListLink1-1");
    FileSystem.addFile("/testListLink1-1/b.txt", 30);
    FileSystem.addFile("/testListLink1-1/a.txt", 30);
    FileSystem.addLink("/testListLink1-2", "/testListLink1-1");
    List<String> res = FileSystem.list("/testListLink1-2");
    assertEquals(2, res.size());
    assertArrayEquals(new String[] { "a.txt", "b.txt" }, res.toArray());
  }

  @Test
  public void testListLink2() {
    FileSystem.addFile("/testListLink2-1", 30);
    FileSystem.addLink("/testListLink2-2", "/testListLink2-1");
    List<String> res = FileSystem.list("/testListLink2-2");
    assertEquals(1, res.size());
    assertEquals("testListLink2-2", res.getFirst());
  }

  @Test
  public void testListInvalid() {
    assertThrows(InvalidPathException.class, () -> FileSystem.list("/invalid"));
  }

  @Test
  public void testSizeOfFile() {
    FileSystem.addFile("/testSizeOfFile", 30);
    assertEquals(30, FileSystem.sizeOf("/testSizeOfFile"));
  }

  @Test
  public void testSizeOfDirectory() {
    FileSystem.addDirectory("/testSizeOfDirectory");
    FileSystem.addFile("/testSizeOfDirectory/a.txt", 30);
    FileSystem.addFile("/testSizeOfDirectory/b.txt", 40);
    assertEquals(70, FileSystem.sizeOf("/testSizeOfDirectory"));
  }

  @Test
  public void testSizeOfLink1() {
    FileSystem.addFile("/testSizeOfLink1-1", 30);
    FileSystem.addLink("/testSizeOfLink1-2", "/testSizeOfLink1-1");
    assertEquals(30, FileSystem.sizeOf("/testSizeOfLink1-2"));
  }

  @Test
  public void testSizeOfLink2() {
    FileSystem.addDirectory("/testSizeOfLink2");
    FileSystem.addFile("/testSizeOfLink2/a.txt", 30);
    FileSystem.addFile("/testSizeOfLink2/b.txt", 40);
    FileSystem.addLink("/testSizeOfLink2/c.txt", "/testSizeOfLink2/a.txt");
    assertEquals(70, FileSystem.sizeOf("/testSizeOfLink2"));
  }

  @Test
  public void testSizeOfInvalid() {
    assertThrows(InvalidPathException.class, () -> FileSystem.sizeOf("/invalid"));
  }

  @Test
  public void testSearchStartFileOrLinkToFile() {
    FileSystem.addFile("/testSearchStartFile", 10);
    List<String> resFile = FileSystem.search("/testSearchStartFile", "testSearchStartFile");
    assertEquals(1, resFile.size());
    assertEquals("/testSearchStartFile", resFile.getFirst());

    FileSystem.addFile("/testSearchStartLinkTarget", 10);
    FileSystem.addLink("/testSearchStartLink", "/testSearchStartLinkTarget");
    List<String> resLinkName = FileSystem.search("/testSearchStartLink", "testSearchStartLink");
    assertEquals(1, resLinkName.size());
    assertEquals("/testSearchStartLink", resLinkName.getFirst());

    List<String> resLinkTargetName = FileSystem.search("/testSearchStartLink", "testSearchStartLinkTarget");
    assertTrue(resLinkTargetName.isEmpty());
  }

  @Test
  public void testSearchDirectoryRecursiveSorted() {
    FileSystem.addDirectory("/testSearchDir");
    FileSystem.addFile("/testSearchDir/b.txt", 10);
    FileSystem.addFile("/testSearchDir/a.txt", 10);
    FileSystem.addDirectory("/testSearchDir/child");
    FileSystem.addFile("/testSearchDir/child/c.txt", 10);
    FileSystem.addFile("/testSearchDir/child/a.txt", 10);

    List<String> res = FileSystem.search("/testSearchDir", "a.txt");
    assertArrayEquals(new String[] { "/testSearchDir/a.txt", "/testSearchDir/child/a.txt" }, res.toArray());
  }

  @Test
  public void testSearchLinkToDirectoryExpandedOnce() {
    FileSystem.addDirectory("/testSearchShared");
    FileSystem.addFile("/testSearchShared/target.txt", 10);
    FileSystem.addLink("/testSearchLinkA", "/testSearchShared");
    FileSystem.addLink("/testSearchLinkB", "/testSearchShared");

    List<String> res = FileSystem.search("/", "target.txt");
    assertEquals(1, res.size());
    assertEquals("/testSearchShared/target.txt", res.getFirst());
  }

  @Test
  public void testSearchLinkToDirectory() {
    FileSystem.addDirectory("/testSearchDirectoryLinkTarget");
    FileSystem.addFile("/testSearchDirectoryLinkTarget/target.txt", 10);
    FileSystem.addLink("/testSearchDirectoryLink", "/testSearchDirectoryLinkTarget");

    List<String> res = FileSystem.search("/testSearchDirectoryLink", "target.txt");
    assertEquals(1, res.size());
    assertEquals("/testSearchDirectoryLink/target.txt", res.getFirst());
  }

  @Test
  public void testSearchInvalidPathNoOutput() {
    assertThrows(InvalidPathException.class, () -> FileSystem.search("/testSearchMissing", "anything"));
  }

  @Test
  public void testRemoveFile() {
    FileSystem.addFile("/testRemoveFile", 10);
    FileSystem.remove("/testRemoveFile");
    assertNull(FileSystem.getNode("/testRemoveFile"));
  }

  @Test
  public void testRemoveLinkOnly() {
    FileSystem.addFile("/testRemoveLinkTarget", 10);
    FileSystem.addLink("/testRemoveLink", "/testRemoveLinkTarget");
    FileSystem.remove("/testRemoveLink");
    assertEquals(10, FileSystem.sizeOf("/testRemoveLinkTarget"));
    assertNull(FileSystem.getNode("/testRemoveLink"));
  }

  @Test
  public void testRemoveEmptyDirectory() {
    FileSystem.addDirectory("/testRemoveEmptyDir");
    FileSystem.remove("/testRemoveEmptyDir");
    assertNull(FileSystem.getNode("/testRemoveEmptyDir"));
  }

  @Test
  public void testRemoveNonEmptyDirectoryThrows() {
    FileSystem.addDirectory("/testRemoveNonEmptyDir");
    FileSystem.addFile("/testRemoveNonEmptyDir/a.txt", 10);
    assertThrows(RemoveNonEmptyDirectoryException.class, () -> FileSystem.remove("/testRemoveNonEmptyDir"));
  }

  @Test
  public void testRemoveInvalidPathThrows() {
    assertThrows(InvalidPathException.class, () -> FileSystem.remove("/testRemoveMissing"));
  }

  @Test
  public void testRemoveRootThrows() {
    assertThrows(InvalidPathException.class, () -> FileSystem.remove("/"));
  }

}
