package exception;

public class RemoveNonEmptyDirectoryException extends RuntimeException {
  public RemoveNonEmptyDirectoryException(String message) {
    super(message);
  }
}
