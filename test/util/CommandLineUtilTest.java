package test.util;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import command.Command;
import command.ListCommand;
import exception.CommandLineParseException;
import util.CommandLineUtil;

public class CommandLineUtilTest {

  @Test
  public void testParse1() {
    Command cmd = CommandLineUtil.parse("ls /usr/bin");
    assertTrue(cmd instanceof ListCommand);
    assertEquals("/usr/bin", ((ListCommand) cmd).getPath());
  }

  @Test
  public void testParse2() {
    Command cmd = CommandLineUtil.parse("  mkdir  /home/user  ");
    assertTrue(cmd instanceof command.MakeDirectoryCommand);
    assertEquals("/home/user", ((command.MakeDirectoryCommand) cmd).getPath());
  }

  @Test(expected = CommandLineParseException.class)
  public void testParseFail1() {
    CommandLineUtil.parse("unknowncommand");
  }

}
