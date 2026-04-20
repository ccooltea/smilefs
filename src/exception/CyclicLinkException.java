package exception;

public class CyclicLinkException extends RuntimeException {
  public CyclicLinkException(String message) {
    super(message);
  }
}
