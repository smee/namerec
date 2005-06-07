package com.bordag.sgz;

// app specific imports
import com.bordag.sgz.util.*;

/**
 * Main entry class for the Sachgebietszuordnungs program.
 * Starts up a connection to the Wortschatz-DB and if this is successful,
 * presents the user a TextConsole, which allows for user inputs and
 * launches of the different algorithms.<br>
 * The connection created here will be used throughout the whole program then.
 *
 * @author    Stefan Bordag
 * @date      28.12.2001
 * @see       com.bordag.sgy.util.TextConsole
 * @see       com.bordag.sgy.util.DBConnection
 */
public class DBKlassifikator
{

  /**
   * Constructor which creates the connection to the DB, reading the
   * optionsfile and if successful opens the TextConsole.<br>
   * @todo: Opening of some graphics representation might be added
   */
  public DBKlassifikator()
  {
    String url = Options.getInstance().getConUrl();
    String user = Options.getInstance().getConUser();
    String passwd = Options.getInstance().getConPasswd();

    DBConnection connection = null;
    try
    {
      connection = new CachedDBConnection(url, user, passwd);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      Output.println("Could not establish connection, exiting.");
      System.exit(0);
    }
    TextConsole console = new TextConsole(connection);
  }

  /**
   * Main method, just creates a new instance of this class, ignores as yet
   * all arguments.
   */
  public static void main(String argv[])
  {
    DBKlassifikator db = new DBKlassifikator();
  }
}
