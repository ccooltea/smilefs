package exception;

public class CommandLineParseException extends RuntimeException {
  public CommandLineParseException(String message) {
    super(message);
  }
}
