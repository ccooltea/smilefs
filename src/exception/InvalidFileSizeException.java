package exception;

public class InvalidFileSizeException extends RuntimeException {
  public InvalidFileSizeException(String message) {
    super(message);
  }
}
